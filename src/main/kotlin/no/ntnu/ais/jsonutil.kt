package no.ntnu.ais

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import no.ntnu.ais.schema.*
import java.io.File
import java.util.*

fun TPosition?.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("x", this?.px ?: 0f)
        obj.addProperty("y", this?.py ?: 0f)
        obj.addProperty("z", this?.pz ?: 0f)
    }
}

fun TEuler?.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        if (this == null) {
            obj.addProperty("x", 0f)
            obj.addProperty("y", 0f)
            obj.addProperty("z", 0f)
        } else {
            obj.addProperty("x", if (repr == TAngleRepr.DEG) Math.toRadians(x.toDouble()).toFloat() else x)
            obj.addProperty("y", if (repr == TAngleRepr.DEG) Math.toRadians(y.toDouble()).toFloat() else y)
            obj.addProperty("z", if (repr == TAngleRepr.DEG) Math.toRadians(z.toDouble()).toFloat() else z)
        }
    }
}

fun TShape.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->

        when {
            plane != null -> {
                obj.addProperty("type", "plane")
                obj.addProperty("width", plane.width)
                obj.addProperty("height", plane.height)
            }
            box != null -> {
                obj.addProperty("type", "box")
                obj.addProperty("width", box.xExtent)
                obj.addProperty("height", box.yExtent)
                obj.addProperty("depth", box.zExtent)
            }
            sphere != null -> {
                obj.addProperty("type", "sphere")
                obj.addProperty("radius", sphere.radius)
            }
            cylinder != null -> {
                obj.addProperty("type", "cylinder")
                obj.addProperty("radius", cylinder.radius)
                obj.addProperty("height", cylinder.height)
            }
            capsule != null -> {
                obj.addProperty("type", "capsule")
                obj.addProperty("radius", capsule.radius)
                obj.addProperty("height", capsule.height)
            }
            mesh != null -> {
                val source = File(mesh.source)
                obj.addProperty("type", "mesh")
                obj.addProperty("source", if (source.isAbsolute) source.path else source.relativeTo(File(".")).path)
            }
        }

    }
}

fun TGeometry.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("opacity", opacity)
        obj.addProperty("wireframe", isWireframe)
        obj.addProperty("color", color.toHex())
        obj.add("shape", shape.toJsonObject())
    }
}

fun TTransform.toJsonObject(setup: Boolean): JsonObject {
    val obj = JsonObject()
    obj.addProperty("name", name)
    obj.add("position", position.toJsonObject())
    obj.add("rotation", rotation.toJsonObject())
    if (setup) {
        obj.add("geometry", geometry.toJsonObject())
    }
    return obj
}

fun List<TTransform>.toJsonArray(setup: Boolean): JsonArray {
    return JsonArray().also { array ->
        this.forEach { t ->
            array.add(t.toJsonObject(setup))
        }
    }
}

fun TWater.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("width", width)
        obj.addProperty("height", height)
    }
}

fun TCameraConfig.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("target", target)
        obj.add("position", initialPosition.toJsonObject())
    }
}

private fun String.toHex(): Int {
    return if (startsWith("0x")) {
        Integer.decode(this.replace("0x", "#"))
    } else {
        ColorConstants.getByName(this)
    }
}

fun TVisualFmuConfig.toJsonObject(setup: Boolean): JsonObject {

    return JsonObject().also { obj ->
        obj.add("transforms", transform.toJsonArray(setup))
        if (setup) {
            obj.add("water", water?.toJsonObject())
            obj.add("camera", cameraConfig?.toJsonObject())
        }
    }

}
