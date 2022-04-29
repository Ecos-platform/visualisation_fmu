@file:DependsOn("info.laht.sspgen:dsl:0.5.1")

import java.io.File
import java.io.FileInputStream
import java.nio.file.FileSystems
import java.nio.file.Files

import no.ntnu.ihb.sspgen.dsl.*

val visFile = File("VisualisationFmu.fmu")
if (!visFile.exists()) {
    Files.copy(FileInputStream("../build/generated/VisualisationFmu.fmu"), File("VisualisationFmu.fmu").toPath())
    // copy VisualConfig.xml into VisualisationFmu
    FileSystems.newFileSystem(visFile.toPath(), null).use { fs ->
        Files.copy(FileInputStream("VisualConfig.xml"), fs.getPath("resources/VisualConfig.xml"))
    }
}

ssp("VisualisationFmuDemo") {

    resources {
        file("VisualisationFmu.fmu")
        url("https://github.com/modelica/fmi-cross-check/raw/master/fmus/2.0/cs/win64/Test-FMUs/0.0.2/BouncingBall/BouncingBall.fmu")
    }

    ssd("DefaultSsd") {

        system("DefaultSystem") {

            elements {

                component("BouncingBall", "resources/BouncingBall.fmu") {
                    connectors {
                        real("h", output)
                    }
                }
                component("VisualisationFmu", "resources/VisualisationFmu.fmu") {
                    connectors {
                        real("transform[0].position.y", input)
                    }
                }

            }

            connections {
                "BouncingBall.h" to "VisualisationFmu.transform[0].position.y"
            }

        }

        defaultExperiment {
            annotations {
                annotation("com.opensimulationplatform") {
                    """
                        <osp:Algorithm>
                            <osp:FixedStepAlgorithm baseStepSize="0.1" numWorkerThreads="-1" />
                        </osp:Algorithm>
                    """
                }
            }
        }

        namespaces {
            namespace("osp", "http://opensimulationplatform.com/SSP/OSPAnnotations")
        }

    }

}.build()
