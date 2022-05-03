package no.ntnu.ais

import java.util.Scanner
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

object TestMesh {

    @JvmStatic
    fun main(args: Array<String>) {

        val fmu = VisualisationFmu(
            mapOf(
                "instanceName" to "instance",
                "resourceLocation" to "data/TestMeshConfig"
            )
        )
        fmu.__define__()
        fmu.enterInitialisationMode()
        fmu.exitInitialisationMode()

        val stop = AtomicBoolean(false)
        thread {
            val sc = Scanner(System.`in`)
            while (sc.hasNext()) {
                if (sc.next() == "q") {
                    stop.set(true)
                    break
                }
            }
        }

        var t = 0.0
        val dt = 0.1
        var angle = 0.0
        while (!stop.get()) {
            fmu.doStep(t, dt)
            fmu.setReal(longArrayOf(fmu.getValueRef("transform[0].rotation.y")), doubleArrayOf(angle))
            angle += 1
            t += dt
            Thread.sleep(10)
        }
        fmu.terminate()

    }
}
