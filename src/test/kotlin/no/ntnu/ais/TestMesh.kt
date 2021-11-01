package no.ntnu.ais

object TestMesh {

    @JvmStatic
    fun main(args: Array<String>) {

        val configPath = TestMesh::class.java.classLoader.getResource("TestMesh.xml")!!.file

        val fmu = VisualisationFmu(mapOf("instanceName" to "instance"))
        fmu.__define__()
        fmu.enterInitialisationMode()

        fmu.setString(longArrayOf(0), arrayOf(configPath))

        fmu.exitInitialisationMode()

    }
}
