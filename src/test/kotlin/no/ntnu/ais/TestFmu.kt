package no.ntnu.ais

import com.google.gson.Gson
import kotlin.math.PI
import kotlin.math.sin

fun main(args: Array<String>) {

    val fmu = VisualisationFmu(mapOf("instanceName" to "instance"))
    fmu.__define__()
    fmu.enterInitialisationMode()

    fmu.setString(longArrayOf(0), arrayOf(createConfig()))

    fmu.exitInitialisationMode()

    fun sine(t: Double): Double {
        return 2.0 * sin(2* PI* 0.1 * t)
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
        t+=dt

        if (!colorChanged && t > 10) {
            colorChanged = true
            val payload = JsonFrame("colorChanged", mapOf(
                "name" to "t2",
                "color" to 0x00ff00
            )).toJson()
            fmu.setString(longArrayOf(1), arrayOf(payload))
        }

        Thread.sleep(10)
    }

}

fun createConfig(): String {

    return VisualConfig(listOf(
        Transform("t1", geometry = (
            Geometry(Sphere()).apply { color = 0xff0000 }
        )),
        Transform("t2", Vector3(10.0, 1.0, -5.0), geometry = (
            Geometry(Box())
        ))
    )).let { Gson().newBuilder().setPrettyPrinting().create().toJson(it) }

}

