package no.ntnu.ais

import java.util.Scanner

internal object TestWater {

    @JvmStatic
    fun main(args: Array<String>) {

        val fmu = VisualisationFmu(
            mapOf(
                "instanceName" to "instance",
                "resourceLocation" to "data/TestWaterConfig"
            )
        )
        fmu.__define__()
        fmu.enterInitialisationMode()
        fmu.exitInitialisationMode()

        println("Press eny key to exit..")
        val sc = Scanner(System.`in`)
        while (sc.hasNext()) {
            break
        }

        fmu.terminate()

    }
}
