package no.ntnu.ais

import no.ntnu.ais.schema.*

data class Quaternion(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 1f
)

class Transform(t: TTransform) {

    val name: String? = t.name
    val parent: String? = t.parent

    val geometry: TGeometry? = t.geometry

    var position: TPosition? = t.position
    var rotation: TEuler? = t.rotation
    var quaternion: Quaternion? = null

    val trail: TTrail? = t.trail

}
