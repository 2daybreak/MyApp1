package linearAlgebra

import org.joml.Vector3f

class Vector3k(val i: Float, val j: Float, val k: Float): Vector3f(i, j, k) {

    constructor(v: Vector3f) : this(v.x, v.y, v.z)

    operator fun Double.times(v: Vector3) = v.times(unaryPlus())

    override fun toString()        = "[x = ${x}, y = ${y}, z = ${z}]"
    operator fun unaryPlus()       = Vector3f(+x, +y, +z)
    operator fun unaryMinus()      = Vector3f(-x, -y, -z)
    operator fun times(f: Float)   = Vector3f(x * f, y * f, z * f)
    operator fun plus (v: Vector3f) = Vector3f(x + v.x, y + v.y, z + v.z)
    operator fun minus(v: Vector3f) = Vector3f(x - v.x, y - v.y, z - v.z)
}