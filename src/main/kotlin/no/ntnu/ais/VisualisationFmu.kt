package no.ntnu.ais

import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import no.ntnu.ais.fmu4j.export.fmi2.Fmi2Slave
import no.ntnu.ais.fmu4j.export.fmi2.ScalarVariable
import no.ntnu.ais.fmu4j.modeldescription.fmi2.Fmi2Causality
import no.ntnu.ais.fmu4j.modeldescription.fmi2.Fmi2Variability
import no.ntnu.ais.schema.TEuler
import no.ntnu.ais.schema.TPosition
import no.ntnu.ais.schema.TTransform
import no.ntnu.ais.schema.TVisualFmuConfig
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import javax.xml.bind.JAXB
import kotlin.concurrent.thread

private const val NUM_TRANSFORMS = 50
private const val MAX_FILE_SIZE = 50 * 8 * 1000000 // 50 MB
private val supportedFormats = listOf("obj", "mtl", "stl", "gltf", "glb", "jpg", "png", "dds")

class VisualisationFmu(
    args: Map<String, Any>
) : Fmi2Slave(args) {

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    private var port: Int = 9090

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    private var openBrowser: Boolean = true

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    private var configPath: String = ""

    private var app: NettyApplicationEngine? = null
    private val subscribers = Collections.synchronizedList(mutableListOf<SendChannel<Frame>>())

    private var visualConfig: TVisualFmuConfig? = null
    private lateinit var updateFrame: String

    private var t0 = System.currentTimeMillis()

    private var t: Thread? = null

    private fun getTransform(index: Int): TTransform? {
        val transform = visualConfig?.transform?.getOrNull(index)
        transform?.apply {
            if (position == null) {
                position = TPosition()
            }
            if (rotation == null) {
                rotation = TEuler()
            }
        }
        return transform
    }

    override fun registerVariables() {

        register(
            string("changeCommand") { "" }.setter(::handleChangeCommand)
                .causality(Fmi2Causality.parameter).variability(Fmi2Variability.tunable)
        )

        for (i in 0..NUM_TRANSFORMS) {

            register(real("transform[$i].position.x") { getTransform(i)?.position?.px?.toDouble() ?: 0.0 }
                .setter { v -> getTransform(i)?.position?.apply { px = v.toFloat() } }
                .causality(Fmi2Causality.input))
            register(real("transform[$i].position.y") { getTransform(i)?.position?.py?.toDouble() ?: 0.0 }
                .setter { v -> getTransform(i)?.position?.apply { py = v.toFloat() } }
                .causality(Fmi2Causality.input))
            register(real("transform[$i].position.z") { getTransform(i)?.position?.pz?.toDouble() ?: 0.0 }
                .setter { v -> getTransform(i)?.position?.apply { pz = v.toFloat() } }
                .causality(Fmi2Causality.input))

            register(real("transform[$i].rotation.x") { getTransform(i)?.rotation?.x?.toDouble() ?: 0.0 }
                .setter { v -> getTransform(i)?.rotation?.apply { x = v.toFloat() } }
                .causality(Fmi2Causality.input))
            register(real("transform[$i].rotation.y") { getTransform(i)?.rotation?.y?.toDouble() ?: 0.0 }
                .setter { v -> getTransform(i)?.rotation?.apply { y = v.toFloat() } }
                .causality(Fmi2Causality.input))
            register(real("transform[$i].rotation.z") { getTransform(i)?.rotation?.z?.toDouble() ?: 0.0 }
                .setter { v -> getTransform(i)?.rotation?.apply { z = v.toFloat() } }
                .causality(Fmi2Causality.input))

        }

    }

    private fun sendSubs(frame: JsonFrame) {
        val json = frame.toJson()
        synchronized(subscribers) {
            runBlocking {
                subscribers.forEach { sub ->
                    sub.send(Frame.Text(json))
                }
            }
        }
    }

    private fun isChild(child: Path, parentText: String): Boolean {
        val parent: Path = Paths.get(parentText).toAbsolutePath()
        return child.toAbsolutePath().startsWith(parent)
    }

    override fun exitInitialisationMode() {

        require(configPath.isNotEmpty())
        val visualConfig = JAXB.unmarshal(File(configPath).absoluteFile, TVisualFmuConfig::class.java)
        this.visualConfig = visualConfig

        app = embeddedServer(Netty, port = port) {

            install(WebSockets)

            routing {

                static("assets") {
                    resources("js")
                    resources("textures")
                }

                val cl = VisualisationFmu::class.java.classLoader
                get("/") {
                    call.respondText(ContentType.Text.Html) {
                        cl.getResourceAsStream("index.html")!!.bufferedReader().readText()
                    }
                }

                val files =
                    visualConfig.transform.mapNotNull { it.geometry?.shape?.mesh?.source?.let { File(it).absoluteFile } }

                get("/assets") {

                    val file = File(call.request.queryString().replace("%20", " "))
                    if (file.exists()) {
                        val child = files.find { isChild(file.toPath(), it.parentFile.absolutePath) } != null
                        if (!child) {
                            call.response.status(HttpStatusCode.BadRequest)
                        }
                        if (file.length() > MAX_FILE_SIZE) {
                            call.response.status(HttpStatusCode.BadRequest)
                        }
                        if (file.extension.lowercase() in supportedFormats) {
                            call.respondFile(file)
                        }
                    }
                }

                webSocket("/") {

                    try {

                        incoming.consumeAsFlow()
                            .mapNotNull {
                                val read = (it as Frame.Text).readText()
                                JsonFrame.fromJson(read)
                            }.collect { frame ->
                                when (frame.action) {
                                    "subscribe" -> {
                                        outgoing.send(
                                            Frame.Text(
                                                JsonFrame(
                                                    action = "setup",
                                                    data = visualConfig?.toJsonObject(true)
                                                ).toJson()
                                            )
                                        )
                                        synchronized(subscribers) {
                                            subscribers.add(outgoing)
                                        }
                                    }
                                    "update" -> {
                                        outgoing.send(Frame.Text(updateFrame))
                                    }
                                }
                            }

                    } catch (ex: ClosedReceiveChannelException) {
                        // ignore
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    } finally {
                        synchronized(subscribers) {
                            subscribers.remove(outgoing)
                        }
                    }

                }

            }

            println(
                """
                    
                Serving on http://$hostName:$port (public)
                Serving on http://127.0.0.1:$port (local)
                
            """.trimIndent()
            )

            if (openBrowser) {
                try {
                    java.awt.Desktop.getDesktop().browse(URI("http://127.0.0.1:$port"))
                } catch (ex: UnsupportedOperationException) {
                    // Do nothing
                }
            }

        }

        t = thread {
            app?.start(wait = true)
        }

        updateFrame = JsonFrame(
            action = "update",
            data = visualConfig?.toJsonObject(false)
        ).toJson()
    }

    override fun doStep(currentTime: Double, dt: Double) {
        val timeSinceUpdate = (System.currentTimeMillis() - t0).toDouble() / 1000
        if (timeSinceUpdate > MAX_UPDATE_RATE) {
            updateFrame = JsonFrame(
                action = "update",
                data = visualConfig?.toJsonObject(false)
            ).toJson()

            t0 = System.currentTimeMillis()
        }
    }

    override fun terminate() {
        try {
            app?.stop(500, 500, TimeUnit.MILLISECONDS)
            t?.join()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun close() {
        terminate()
    }

    fun handleChangeCommand(str: String) {
        val cmd = JsonFrame.fromJson(str)
        when (cmd.action) {
            "colorChanged" -> {
                val data = cmd.data as Map<*, *>
                val name = data["name"] as String
                val color = (data["color"] as Number).toInt()
                val transform = visualConfig?.transform?.find { it.name == name }
                transform?.geometry?.color = color.toHexString()

                sendSubs(
                    JsonFrame(
                        action = "colorChanged",
                        data = mapOf(
                            "name" to name,
                            "color" to color
                        )
                    )
                )
            }
        }
    }

    private companion object {
        private const val MAX_UPDATE_RATE = 1.0 / 120

        private val hostName by lazy {
            var hostAddress: String? = "127.0.0.1"
            try {
                val url = URL("http://checkip.amazonaws.com/")
                val `in` = url.openStream().bufferedReader()
                val read = `in`.readLine().trim { it <= ' ' }
                if (read.isNotEmpty()) {
                    hostAddress = read
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            hostAddress
        }

    }

}

internal class JsonFrame(
    val action: String,
    val data: Any? = null
) {

    fun toJson() = gson.toJson(this)

    companion object {

        fun fromJson(str: String): JsonFrame {
            return gson.fromJson(str, JsonFrame::class.java)
        }

        private val gson = GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create()

    }

}
