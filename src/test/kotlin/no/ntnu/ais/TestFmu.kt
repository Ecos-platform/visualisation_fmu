package no.ntnu.ais

import kotlin.math.PI
import kotlin.math.sin

object Demo {

    @JvmStatic
    fun main(args: Array<String>) {

        val configPath = Demo::class.java.classLoader.getResource("Config1.xml")!!.file

        val fmu = VisualisationFmu(mapOf("instanceName" to "instance"))
        fmu.__define__()
        fmu.enterInitialisationMode()

        fmu.setString(longArrayOf(0), arrayOf(configPath))

        fmu.exitInitialisationMode()

        fun sine(t: Double): Double {
            return 5.0 * sin(2 * PI
                    * 0.1 * t)
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
}
