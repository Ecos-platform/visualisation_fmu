package no.ntnu.ais

class VisualConfig(
    val transforms: List<Transform>
) {
    fun toMap(setup: Boolean): List<Map<String, Any>> {

        return transforms.map { it.toMap(setup) }
    }

}

class Vector3(
    var x: Double,
    var y: Double,
    var z: Double
)

class Euler(
    var x: Double,
    var y: Double,
    var z: Double,
    var repr: String = "deg"
)

class Transform(
    val name: String,
    val position: Vector3,
    val rotation: Euler,
    val geometries: List<Geometry>
) {

    fun toMap(setup: Boolean): Map<String, Any> {
        val map = mutableMapOf(
            "name" to name,
            "position" to position,
            "rotation" to rotation
        )

        if (setup) {
            map["geometries"] = geometries
        }

        return map
    }

}

class Geometry(
    val shape: Shape
) {

    val color: Int = 0x808080
    val opacity: Float = 1f
    val wireframe: Boolean = false

}

sealed class Shape

class Sphere(
    val radius: Float
) : Shape()

class Cylinder(
    val radius: Float,
    val height: Float
) : Shape()

class Box(
    val xExtent: Float,
    val yExtent: Float,
    val zExtent: Float
) : Shape()
