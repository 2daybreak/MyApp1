package design

import org.joml.Quaternionf
import org.joml.Vector3f

class DesignItem(var mesh: Mesh) {

    val position: Vector3f = Vector3f(0f, 0f, 0f)

    val rotation: Vector3f = Vector3f(0f, 0f, 0f)

    var scale: Float = 1f

    val quaternion: Quaternionf = Quaternionf(0f, 0f, 0f, 1f)

    fun setPosition(x: Float, y: Float, z: Float) {
        this.position.x = x
        this.position.y = y
        this.position.z = z
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        this.rotation.x = x
        this.rotation.y = y
        this.rotation.z = z
    }

    fun setQuaternion(x: Float, y: Float, z: Float, w: Float) {
        this.quaternion.x = x
        this.quaternion.y = y
        this.quaternion.z = z
        this.quaternion.w = w
    }
}