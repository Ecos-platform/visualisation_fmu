package no.ntnu.ais

import com.google.gson.Gson
import java.io.File
import kotlin.math.PI
import kotlin.math.sin

fun main(args: Array<String>) {

    val fmu = VisualisationFmu(mapOf("instanceName" to "instance"))
    fmu.__define__()
    fmu.enterInitialisationMode()

    fmu.setString(longArrayOf(0), arrayOf(createConfig()))

    fmu.exitInitialisationMode()

    fun sine(t: Double): Double {
        return 5.0 * sin(2 * PI * 0.1 * t)
    }

    var t = 0.0
    var t0 = System.currentTimeMillis()
    var colorChanged = false
    while (true) {

        val dt = (System.currentTimeMillis() - t0).toDouble() / 1000
        t0 = System.currentTimeMillis()

        val h = sine(t)
        fmu.setReal(longArrayOf(1), doubleArrayOf(h))
        fmu.doStep(t, dt)
        t += dt

        if (!colorChanged && t > 5) {
            colorChanged = true
            val payload = JsonFrame(
                "colorChanged", mapOf(
                    "name" to "t2",
                    "color" to 0x00ff00
                )
            ).toJson()
            fmu.setString(longArrayOf(1), arrayOf(payload))
        }

        Thread.sleep(10)
    }

}

fun createConfig(): String {

    return VisualConfig(
        transforms = listOf(
            Transform(
                "t1", geometry = (
                        Geometry(Sphere(), color = 0xff0000)
                        )
            ),
            Transform(
                "t2", parent = "t1", position = Vector3(0.0, 10.0, 0.0), geometry = (
                        Geometry(Box())
                        )
            ),
            Transform(
                "tt", position = Vector3(30.0, 0.0, 0.0), geometry = (
                        Geometry(Mesh(File("C:\\Users\\laht\\dev\\Vico\\examples\\gunnerus\\obj\\Gunnerus.obj").absolutePath))
                        )
            )
        ),
        water = Water(64, 64),
        camera = Camera(position = Vector3(0.0, 5.0, -20.0))
    ).let { Gson().newBuilder().setPrettyPrinting().create().toJson(it) }

}

