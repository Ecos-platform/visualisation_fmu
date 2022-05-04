package no.ntnu.ais

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import no.ntnu.ais.schema.*
import java.io.File

internal class JsonFrame(
    val action: String,
    val data: Any? = null
) {

    fun toJson() = gson.toJson(this)

    companion object {

        fun fromJson(str: String): JsonFrame {
            return gson.fromJson(str, JsonFrame::class.java)
        }

        private val gson = GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create()
    }

}

fun TPosition.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("x", this.px)
        obj.addProperty("y", this.py)
        obj.addProperty("z", this.pz)
    }
}

fun TEuler.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("x", if (repr == TAngleRepr.DEG) Math.toRadians(x.toDouble()).toFloat() else x)
        obj.addProperty("y", if (repr == TAngleRepr.DEG) Math.toRadians(y.toDouble()).toFloat() else y)
        obj.addProperty("z", if (repr == TAngleRepr.DEG) Math.toRadians(z.toDouble()).toFloat() else z)
    }
}

fun Quaternion.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("x", this.x)
        obj.addProperty("y", this.y)
        obj.addProperty("z", this.z)
        obj.addProperty("w", this.w)
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
                obj.addProperty("radius", capsule.radius)
                obj.addProperty("height", capsule.height)
            }
            mesh != null -> {
                val source = File(mesh.source)
                obj.addProperty("type", "mesh")
                obj.addProperty("scale", mesh.scale)
                obj.addProperty("source", if (source.isAbsolute) source.path else source.relativeTo(File(".")).path)
            }
        }

    }
}

fun TTrail.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        obj.addProperty("color", color.toHex())
        obj.addProperty("maxLength", length)
    }
}

fun TGeometry.toJsonObject(): JsonObject {
    return JsonObject().also { obj ->
        offsetPosition?.also {
            obj.add("offsetPosition", offsetPosition.toJsonObject())
        }
        offsetRotation?.also {
            obj.add("offsetRotation", offsetRotation.toJsonObject())
        }
        obj.addProperty("opacity", opacity)
        obj.addProperty("wireframe", isWireframe)
        obj.addProperty("color", color.toHex())
        obj.add("shape", shape.toJsonObject())
    }
}

fun TTransform.toJsonObject(): JsonObject {
    val obj = JsonObject()
    obj.addProperty("name", name)
    obj.addProperty("parent", parent)
    position?.also {
        obj.add("position", it.toJsonObject())
    }
    rotation?.also {
        obj.add("rotation", it.toJsonObject())
    }


    obj.add("geometry", geometry.toJsonObject())
    obj.add("trail", trail?.toJsonObject())

    return obj
}

fun Transform.toJsonObject(): JsonObject {
    val obj = JsonObject()
    obj.addProperty("name", name)
    position?.also {
        obj.add("position", it.toJsonObject())
    }
    rotation?.also {
        obj.add("rotation", it.toJsonObject())
    }
    quaternion?.also {
        obj.add("quaternion", it.toJsonObject())
    }
    return obj
}

fun List<TTransform>.toJsonArray(): JsonArray {
    return JsonArray().also { array ->
        this.forEach { t ->
            array.add(t.toJsonObject())
        }
    }
}

@JvmName("toJsonArrayTransform")
fun List<Transform>.toJsonArray(): JsonArray {
    return JsonArray().also { array ->
        this.forEach { t ->
            array.add(t.toJsonObject())
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

fun TVisualFmuConfig.toJsonObject(): JsonObject {

    return JsonObject().also { obj ->
        obj.add("transforms", transform.toJsonArray())

        obj.add("water", water?.toJsonObject())
        obj.add("camera", cameraConfig?.toJsonObject())

    }

}
