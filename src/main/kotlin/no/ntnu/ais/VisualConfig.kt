package no.ntnu.ais

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

const val DEFAULT_COLOR = 0x808080

class VisualConfig(
    val transforms: List<Transform>,
    val camera: Camera = Camera(),
    val water: Water? = null

) {

    fun toMap(setup: Boolean): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "transforms" to transforms.map { it.toMap(setup) }
        )

        if (setup) {
            map["water"] = water
            map["camera"] = camera
        }

        return map
    }

}

class Camera(
    val position: Vector3 = Vector3(45.0, 45.0, 45.0),
    val fov: Int? = null,
    val target: String? = null
)

class Water(
    val width: Int = 512,
    val height: Int = 512
)

class Vector3(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0
)

class Euler(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var repr: String = "deg"
)

class Transform(
    val name: String,
    val parent: String? = null,
    val position: Vector3? = null,
    val rotation: Euler? = null,
    val geometry: Geometry? = null
) {

    fun toMap(setup: Boolean): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            "name" to name,
            "position" to position,
            "rotation" to rotation
        )

        if (setup) {
            map["parent"] = parent
            map["geometry"] = geometry?.toMap()
        }

        return map
    }

}

class Geometry(
    val shape: Shape,
    var color: Int = DEFAULT_COLOR,
    var opacity: Float = 1f,
    var wireframe: Boolean = false
) {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "color" to color,
            "opacity" to opacity,
            "wireframe" to wireframe,
            "shape" to shape
        )
    }

}

class GeometryDeserializer: JsonDeserializer<Geometry> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Geometry {

        return json.asJsonObject.let { obj ->
            val color = obj.get("color")?.asInt ?: DEFAULT_COLOR
            val shape = obj.get("shape").asJsonObject
            when(val type = shape.get("type")?.asString) {
                "sphere" -> {
                    Sphere(shape.get("radius").asFloat)
                }
                "box" -> {
                    Box(shape.get("width").asFloat, shape.get("height").asFloat, shape.get("depth").asFloat)
                }
                "mesh" -> {
                    Mesh(shape.get("source").asString)
                }
                else -> throw UnsupportedOperationException(type)
            }.let {

                Geometry(it,color = color)
            }

        }

    }
}
