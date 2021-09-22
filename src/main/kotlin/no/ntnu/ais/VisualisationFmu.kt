package no.ntnu.ais

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking
import no.ntnu.ais.fmu4j.export.fmi2.Fmi2Slave
import no.ntnu.ais.fmu4j.export.fmi2.ScalarVariable
import no.ntnu.ais.fmu4j.modeldescription.fmi2.Fmi2Causality
import no.ntnu.ais.fmu4j.modeldescription.fmi2.Fmi2Variability
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL
import java.util.*

class VisualisationFmu(
    args: Map<String, Any>
) : Fmi2Slave(args) {

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    private var port: Int = 9090

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    private var openBrowser: Boolean = true

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    private var config: String = ""

    private var app: NettyApplicationEngine? = null
    private val subscribers = Collections.synchronizedList(mutableListOf<SendChannel<Frame>>())

    private var visualConfig: VisualConfig? = null
    private lateinit var updateFrame: String

    private var t0 = System.currentTimeMillis()

    override fun registerVariables() {

        register(string("changeCommand") { "" }.setter {

        })

        for (i in 0..1000) {

            register(real("transform[$i].position.x") { visualConfig?.transforms?.getOrNull(i)?.position?.x ?: 0.0 }
                .setter { v -> visualConfig?.transforms?.getOrNull(i)?.position?.apply { x = v } }
                .causality(Fmi2Causality.input))
            register(real("transform[$i].position.y") { visualConfig?.transforms?.getOrNull(i)?.position?.x ?: 0.0 }
                .setter { v -> visualConfig?.transforms?.getOrNull(i)?.position?.apply { y = v } }
                .causality(Fmi2Causality.input))
            register(real("transform[$i].position.z") { visualConfig?.transforms?.getOrNull(i)?.position?.x ?: 0.0 }
                .setter { v -> visualConfig?.transforms?.getOrNull(i)?.position?.apply { z = v } }
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

    override fun exitInitialisationMode() {

        visualConfig = Gson().fromJson(config, VisualConfig::class.java)

        app = embeddedServer(Netty, port = port) {

            install(WebSockets)

            routing {

                resources("/")

                val cl = VisualisationFmu::class.java.classLoader
                get("/") {
                    call.respondText(ContentType.Text.Html) {
                        cl.getResourceAsStream("index.html")!!.bufferedReader().readText()
                    }
                }

                get("/assets") {
                    val file = File(call.request.queryString())
                    if (file.exists()) {
                        call.respondFile(file)
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
                                                    data = visualConfig?.toMap(true)
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
                    } catch (ex: Throwable) {
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
                    java.awt.Desktop.getDesktop().browse(URI("http://127.0.0.1:$port/index.html"))
                } catch (ex: UnsupportedOperationException) {
                    // Do nothing
                }
            }

        }.start(wait = false)

        updateFrame = JsonFrame(
            action = "update",
            data = visualConfig?.toMap(false)
        ).toJson()
    }

    override fun doStep(currentTime: Double, dt: Double) {
        val timeSinceUpdate = (System.currentTimeMillis() - t0).toDouble() / 1000
        if (timeSinceUpdate > MAX_UPDATE_RATE) {
            updateFrame = JsonFrame(
                action = "update",
                data = visualConfig?.toMap(false)
            ).toJson()

            t0 = System.currentTimeMillis()
        }
    }

    override fun terminate() {
        try {
            app?.stop(500, 500)
        } catch (ex: Exception) {
            // do nothing
        }
    }

    private companion object {
        private const val MAX_UPDATE_RATE = 1.0 / 60

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
