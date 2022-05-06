package no.ntnu.ais

import java.util.Scanner
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

internal object TestFmu {

    private var stop = AtomicBoolean(false)

    private fun sine(t: Double): Double {
        return 5.0 * sin(2 * PI * 0.1 * t)
    }

    private fun listenForInput() {
        thread {
            val sc = Scanner(System.`in`)
            while(sc.hasNext()) {
                if (sc.next() == "q") {
                    stop.set(true)
                    break
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {

        VisualisationFmu(mapOf(
            "instanceName" to "instance",
            "resourceLocation" to "data/TestFmuConfig"
        )).use { fmu ->

            fmu.__define__()
            fmu.enterInitialisationMode()
            fmu.exitInitialisationMode()

            listenForInput()

            var t = 0.0
            var t0 = System.currentTimeMillis()
            var colorChanged = false
            while (!stop.get()) {

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
                    fmu.setString(longArrayOf(0), arrayOf(payload))
                }

                Thread.sleep(10)
            }

        }


    }
}
