package no.ntnu.ais

import java.util.Scanner

object TestMesh {

    @JvmStatic
    fun main(args: Array<String>) {

        val fmu = VisualisationFmu(mapOf(
            "instanceName" to "instance",
            "resourceLocation" to "data/TestMeshConfig"
        ))
        fmu.__define__()
        fmu.enterInitialisationMode()

        fmu.exitInitialisationMode()

        val sc = Scanner(System.`in`)
        while (sc.hasNext()) {
            if (sc.next() == "q")
                break
        }

        fmu.terminate()

    }
}
