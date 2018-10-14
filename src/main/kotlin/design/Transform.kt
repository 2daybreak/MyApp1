package design

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class Transform {

    private val projectionMatrix = Matrix4f()

    private val worldMatrix = Matrix4f()

    fun getProjectionMatrix(fov: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        val aspectRatio = width / height
        projectionMatrix.identity()
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar)
        return projectionMatrix
    }

    fun getOrthographicMatrix(width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        val aspectRatio = width / height
        projectionMatrix.identity()
        projectionMatrix.ortho(-aspectRatio, aspectRatio, -1f, 1f, zNear, zFar)
        return projectionMatrix
    }

    fun getWorldMatrix(offset: Vector3f, rotation: Vector3f, scale: Float): Matrix4f {
        worldMatrix.identity().translate(offset).rotateX(Math.toRadians(rotation.x.toDouble()).toFloat()).rotateY(Math.toRadians(rotation.y.toDouble()).toFloat()).rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat()).scale(scale)
        return worldMatrix
    }

    fun getWorldMatrix(offset: Vector3f, quaternion: Quaternionf): Matrix4f {
        worldMatrix.identity().translate(offset).rotate(quaternion)
        return worldMatrix
    }

    fun getWorldMatrix(camera: Camera): Matrix4f {
        val p = camera.position
        val f = camera.front
        val u = camera.up

        worldMatrix.identity().lookAt(p, Vector3f(p.x + f.x, p.y + f.y, p.z + f.z), u)
        return worldMatrix
    }

}
