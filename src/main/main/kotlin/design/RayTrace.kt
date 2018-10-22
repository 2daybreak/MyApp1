package design

import linearAlgebra.Vector3k
import org.joml.*

class RayTrace {

    lateinit var origin: Vector3f
    lateinit var inverseProjMat4: Matrix4f
    lateinit var inverseViewMat4: Matrix4f

    fun init(point: Vector3f, invProj: Matrix4f, invView: Matrix4f) {
        // Assignment of Reference
        origin = point
        inverseProjMat4 = invProj
        inverseViewMat4 = invView
    }

    fun rayDirection(window: GlfWindow, screenPos: Vector2d): Vector3f {
        val w = window.width
        val h = window.height
        // Normalized device coord.
        val ndc = Vector3f()
        ndc.x = (2 * screenPos.x / w).toFloat() - 1f
        ndc.y = 1f - (2 * screenPos.y / h).toFloat()
        val v4 = Vector4f(ndc, 1f)  // clip space (NDC)
        v4.mul(inverseProjMat4)     // to view space
        v4.z = -1f // unnecessary if nNear is set to 1f
        v4.w = 1f
        val pos = Vector4f(v4)
        pos.mul(inverseViewMat4)    // to world space
        v4.w = 0f
        val dir = Vector4f(v4)
        dir.mul(inverseViewMat4)    // to world space
        return Vector3f(dir.x, dir.y, dir.z).normalize()
    }

    fun rayPlane(window: GlfWindow, screenPos: Vector2d, point: Vector3f, normal: Vector3f): Vector3f {
        val dir = rayDirection(window, screenPos)
        return RayTrace().rayPlane(origin, dir, point, normal, 0.000001f)
    }

    fun raySphere(window: GlfWindow, screenPos: Vector2d, center: Vector3f, radius: Float): Boolean {
        val dir = rayDirection(window, screenPos)
        return raySphere(origin, dir, center, radius)
    }

    fun rayTriangle(window: GlfWindow, screenPos: Vector2d, v0: Vector3f, v1: Vector3f, v2: Vector3f): Float {
        val dir = rayDirection(window, screenPos)
        return rayTriangle(origin, dir, v0, v1, v2, 0.000001f)
    }

    fun rayPlane(origin: Vector3f, dir: Vector3f, point: Vector3f, normal: Vector3f, epsilon: Float): Vector3f {
        val a = Vector3k(dir)
        val b = Vector3k(origin)
        val x = Intersectionf.intersectRayPlane(origin, dir, point, normal, epsilon)
        return b + a * x
    }

    fun raySphere(origin: Vector3f, dir: Vector3f, center: Vector3f, radius: Float): Boolean {
        val radiusSquared = radius * radius
        val t = Vector2f()
        return Intersectionf.intersectRaySphere(origin, dir, center, radiusSquared, t)
    }

    fun rayTriangle(origin: Vector3f, dir: Vector3f, v0: Vector3f, v1: Vector3f, v2: Vector3f, epsilon: Float): Float {
        val a = Vector3k(dir)
        val b = Vector3k(origin)
        val x = Intersectionf.intersectRayTriangle(origin, dir, v0, v1, v2, epsilon)
        //return b + a * x
        return x
    }

    fun rayBox(origin: Vector3f, dir: Vector3f, min: Vector3f, max: Vector3f): Boolean {
        val t = Vector2f()
        return Intersectionf.intersectRayAab(origin, dir, min, max, t)
    }
}