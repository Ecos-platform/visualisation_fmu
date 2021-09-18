package no.ntnu.ais

import no.ntnu.ais.fmu4j.export.fmi2.Fmi2Slave
import no.ntnu.ais.fmu4j.export.fmi2.ScalarVariable
import no.ntnu.ais.fmu4j.modeldescription.fmi2.Fmi2Causality
import no.ntnu.ais.fmu4j.modeldescription.fmi2.Fmi2Variability

class VisualisationFmu(
    args: Map<String, Any>
): Fmi2Slave(args) {

    @ScalarVariable(causality = Fmi2Causality.parameter, variability = Fmi2Variability.fixed)
    var numTransforms: Int = 0

    @ScalarVariable(causality = Fmi2Causality.input)
    val positions: DoubleArray = DoubleArray(1000*3)
    @ScalarVariable(causality = Fmi2Causality.input)
    var rotations: DoubleArray = DoubleArray(1000*3)

    override fun doStep(currentTime: Double, dt: Double) {

    }

}
