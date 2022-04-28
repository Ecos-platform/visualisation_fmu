package no.ntnu.ais

import java.util.Scanner

object TestMesh {

    @JvmStatic
    fun main(args: Array<String>) {

        val configPath = TestMesh::class.java.classLoader.getResource("TestMesh.xml")!!.file

        val fmu = VisualisationFmu(mapOf(
            "instanceName" to "instance",
            "resourceLocation" to "data/obj/female02"
        ))
        fmu.__define__()
        fmu.enterInitialisationMode()

        fmu.setString(longArrayOf(0), arrayOf(configPath))

        fmu.exitInitialisationMode()

        val sc = Scanner(System.`in`)
        while (sc.hasNext()) {
            if (sc.next() == "q")
                break
        }

        fmu.terminate()

    }
}
