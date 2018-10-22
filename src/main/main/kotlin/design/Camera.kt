package design

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector4f

class Camera {

    val position: Vector3f

    val front: Vector3f

    val up: Vector3f

    val right: Vector3f
        get() { return Vector3f(front).cross(up).normalize() }

    val quaternionf: Quaternionf = Quaternionf()

    constructor() {
        position = Vector3f(0f, 0f, 1f)
        front = Vector3f(0f, 0f, -1f)
        up = Vector3f(0f, 1f, 0f)
    }

    fun setCamera(position: Vector3f, front: Vector3f, up: Vector3f) {
        this.position.set(position)
        this.front.set(front)
        this.up.set(up)
    }

    fun zoom(scale: Float) {
        position.mul(scale)
    }

    fun translate(moveX: Float, moveY: Float) {
        val amp = position.length()
        position.add(Vector3f(front).cross(up).mul(moveX * amp))
        position.add(Vector3f(up).mul(moveY * amp))
    }

    fun rotateGlobal(angleX: Float, angleY: Float) {
        quaternionf.identity()
        quaternionf.rotateAxis(angleX, right)
        quaternionf.rotateAxis(angleY, up)
        position.rotate(quaternionf)
        front.rotate(quaternionf)
        up.rotate(quaternionf)
    }

    fun rotateLocal(angleX: Float, angleY: Float) {
        quaternionf.identity()
        quaternionf.rotateAxis(angleX, right)
        quaternionf.rotateAxis(angleY, up)
        front.rotate(quaternionf)
        up.rotate(quaternionf)
    }

}