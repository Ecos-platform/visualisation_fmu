### Visualisation FMU

A generic FMU that provides 3D visualisation capabilities for any existing FMI based co-simulation.
The FMU embeds a web-server and HTML/Javascript code for visualisation in the browser. 
As such, users can connect to the visualisation even if the FMU is running in the cloud.

### Configuration

The structure of the simulation is configured through XML, `VisualConfig.xml`, 
which must be placed in the `resources` folder of the FMU.

See `VisualFmuConfig.xsd` for an API reference.

#### 3D models
OBJ/MTL, STL and GLTF are supported. The files must be located inside the `resources` folder in the FMU.

###### Example
```xml
<?xml version="1.0" encoding="UTF-8"?>
<vico:VisualFmuConfig xmlns:vico="http://github.com/Vico-platform/VisualisationFmu/VisualFmuConfig">

    <vico:CameraConfig>
        <vico:initialPosition px="-15" py="15" pz="-25"/>
    </vico:CameraConfig>

    <vico:Transform name="t1">
        <vico:Geometry color="blue">
            <vico:Shape>
                <vico:Sphere radius="0.5"/>
            </vico:Shape>
        </vico:Geometry>
    </vico:Transform>

    <vico:Transform name="t2" parent="t1">
        <vico:Geometry>
            <vico:Shape>
                <vico:Box xExtent="1" yExtent="1" zExtent="1"/>
            </vico:Shape>
        </vico:Geometry>
        <vico:Position px="5"/>
        <vico:Trail length="2"/>
    </vico:Transform>
    
</vico:VisualFmuConfig>
```

### Updating transforms
 
The FMU exposes input ports for position and rotation.

```
    transform[i].position.x
    transform[i].position.y
    transform[i].position.z
    
    transform[i].rotation.x
    transform[i].rotation.y
    transform[i].rotation.z
```

Here, `i` is used to update the position and/or rotation of the `ith` transform defined in the configuration XML.
Currently, only euler angles for rotations are supported are supported.

### Run-time commands

User defined commands can be triggered by sending JSON messages to the exposed `changeCommand` string parameter.

###### Example
```json
{
  "colorChanged": {
    "name": "t1",
    "color": "0xffffff"
  }
}
```

### Building the FMU

Simply run `./gradlew build`. The FMU is located in `build/generated`

### Requirements

The FMU is built using the JVM (JDK8), so a suitable run-time environment is required. That is, add `jvm.dll/libjvm.so` to PATH.

Python 3.7 < is known not work with FMU4j, which is used to generate the FMU.
