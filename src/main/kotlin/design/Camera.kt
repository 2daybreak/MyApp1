package design

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector4f

class Camera {

    val position: Vector3f

    val front: Vector3f =
            Vector3f(0f, 0f, -1f)
    val up: Vector3f =
            Vector3f(0f, 1f, 0f)
    val right: Vector3f
        get() { return Vector3f(front).cross(up).normalize() }

    val quaternionf: Quaternionf = Quaternionf()

    val rotation: Vector3f = Vector3f()

    constructor() {
        position = Vector3f()
    }

    constructor(position: Vector3f) {
        this.position = position
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun movePosition(offsetX: Float, offsetY: Float, offsetZ: Float) {
        position.x += offsetX
        position.y += offsetY
        position.z += offsetZ
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }

    fun moveRotation(offsetX: Float, offsetY: Float, offsetZ: Float) {
        rotation.x += offsetX
        rotation.y += offsetY
        rotation.z += offsetZ
    }

    fun setRotation() {
        val f = Vector4f(front.x, front.y, front.z, 0f)
        val u = Vector4f(up.x, up.y, up.z, 0f)
        val rotation = Matrix4f().identity().rotate(quaternionf)
        f.mul(rotation)
        u.mul(rotation)
        front.x = f.x
        front.y = f.y
        front.z = f.z
        up.x = u.x
        up.y = u.y
        up.z = u.z
    }
}