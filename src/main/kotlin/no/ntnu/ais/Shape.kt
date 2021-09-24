package no.ntnu.ais

import java.io.File

sealed class Shape {
    val type = javaClass.simpleName.lowercase()
}

class Sphere(
    val radius: Float = 1f
) : Shape()

class Circle(
    val radius: Float = 0.5f
) : Shape()

class Plane(
    val width: Float = 1f,
    val height: Float = 1f
) : Shape()

class Box(
    val width: Float = 1f,
    val height: Float = 1f,
    val depth: Float = 1f
) : Shape()

class Cylinder(
    val radius: Float = 0.5f,
    val height: Float = 1f
) : Shape()

class Capsule(
    val radius: Float = 0.5f,
    val height: Float = 1f
) : Shape()

class Heightmap(
    val width: Float = 1f,
    val height: Float = 1f,
    val widthSegments: Int = 1,
    val heightSegments: Int = 1,
    val heights: FloatArray
) : Shape()

class Mesh(
    source: String
) : Shape() {

    val source = File(source).relativeTo(File(".")).path
}
