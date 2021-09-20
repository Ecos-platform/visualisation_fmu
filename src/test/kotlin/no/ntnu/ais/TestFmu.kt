package no.ntnu.ais

fun main(args: Array<String>) {

    val fmu = VisualisationFmu(mapOf("instanceName" to "instance"))
    fmu.enterInitialisationMode()
    fmu.exitInitialisationMode()

}

