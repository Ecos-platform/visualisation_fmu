package no.ntnu.ais

const val DEFAULT_COLOR = 0x808080

class VisualConfig(
    val transforms: List<Transform>
) {

    fun toMap(setup: Boolean): List<Map<String, Any>> {

        return transforms.map { it.toMap(setup) }
    }

}

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
    val position: Vector3 = Vector3(),
    val rotation: Euler = Euler(),
    val geometry: Geometry? = null
) {

    fun toMap(setup: Boolean): Map<String, Any> {
        val map = mutableMapOf(
            "name" to name,
            "position" to position,
            "rotation" to rotation
        )

        if (setup && geometry != null) {
            map["geometry"] = geometry
        }

        return map
    }

}

class Geometry(
    val shape: Shape
) {

    var color: Int = DEFAULT_COLOR
    var opacity: Float = 1f
    var wireframe: Boolean = false

}

open class Shape {
    val type: String = javaClass.simpleName.lowercase()
}

class Sphere(
    val radius: Float = 0.5f
) : Shape()

class Cylinder(
    val radius: Float = 1f,
    val height: Float = 0.5f
) : Shape()

class Box(
    val xExtent: Float = 1f,
    val yExtent: Float = 1f,
    val zExtent: Float = 1f
) : Shape()
