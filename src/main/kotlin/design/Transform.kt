package design

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class Transform {

    fun getProjectionMatrix(fov: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        val aspectRatio = width / height
        val projectionMatrix = Matrix4f().identity()
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar)
        return projectionMatrix
    }

    fun getOrthographicMatrix(width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        val aspectRatio = width / height
        val projectionMatrix = Matrix4f().identity()
        projectionMatrix.ortho(-aspectRatio, aspectRatio, -1f, 1f, zNear, zFar)
        return projectionMatrix
    }

    fun getViewMatrix(offset: Vector3f, rotation: Vector3f): Matrix4f {
        val viewMatrix = Matrix4f().identity()
                .translate(offset)
                .rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
                .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
                .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
        return viewMatrix
    }

    fun getViewMatrix(offset: Vector3f, rotation: Vector3f, scale: Float): Matrix4f {
        val viewMatrix = Matrix4f().identity()
                .translate(offset)
                .rotateX(Math.toRadians(rotation.x.toDouble()).toFloat())
                .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
                .rotateZ(Math.toRadians(rotation.z.toDouble()).toFloat())
                .scale(scale)
        return viewMatrix
    }

    fun getViewMatrix(offset: Vector3f, quaternion: Quaternionf): Matrix4f {
        val viewMatrix = Matrix4f().identity()
                .translate(offset)
                .rotate(quaternion)
        return viewMatrix
    }

    fun getViewMatrix(offset: Vector3f, quaternion: Quaternionf, scale: Float): Matrix4f {
        val viewMatrix = Matrix4f().identity()
                .translate(offset)
                .rotate(quaternion)
                .scale(scale)
        return viewMatrix
    }

    fun getViewMatrix(camera: Camera): Matrix4f {
        val p = camera.position
        val f = camera.front
        val u = camera.up
        val viewMatrix = Matrix4f().identity()
                .lookAt(p, Vector3f(p.x + f.x, p.y + f.y, p.z + f.z), u)
        return viewMatrix
    }

    fun getViewMatrix(camera: Camera, quaternion: Quaternionf, scale: Float): Matrix4f {
        val p = camera.position
        val f = camera.front
        val u = camera.up
        val viewMatrix = Matrix4f().identity()
                .lookAt(p, Vector3f(p.x + f.x, p.y + f.y, p.z + f.z), u)
                .rotate(quaternion)
                .scale(scale)
        return viewMatrix
    }

}