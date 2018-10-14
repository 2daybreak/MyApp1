package geoModel

import design.Mesh
import linearAlgebra.Vector3

abstract class ParametricCurve: Parametric {

    protected abstract var degree: Int

    override operator fun invoke(t: Double): Vector3 {
        return curvePoint(t)
    }

    override operator fun invoke(kmax: Int, t: Double): Array<Vector3> {
        return curveDers(kmax, t)
    }

    override operator fun invoke(v: Vector3): Vector3 {
        return closestPoint(v)
    }

    override fun distance(v: Vector3): Double {
        val d = closestPoint(v) - v
        return d.length
    }

    /**
     * Determine degree (& order), then
     * Option A (original) : Assign parameter to each point by chord
     * length method, and then evaluate knots vector reflecting the
     * distribution of parameter
     * Option B (covariant) : Assign uniform knots vector, ans then
     * evaluate parameter by averaging the knots
     *
     * @param p the specified points
     * @see Bspline
     */
    protected abstract fun properties(p: MutableList<Vector3>)

    /**
     * Evaluate the point on a curve at a specified parameter
     *
     * @param t a specific parameter within a range from 0 to 1
     * @return the point on a curve
     * @see Bspline
     */
    protected abstract fun curvePoint(t: Double): Vector3

    /**
     * Evaluate the derivatives on a curve at a specified parameter
     *
     * @param kmax the max. degree of the derivatives
     * @param t a specific parameter within a range from 0 to 1
     * @return the derivatives at a specified parameter
     * @see Bspline
     */
    protected abstract fun curveDers(kmax: Int, t: Double): Array<Vector3>

    /**
     * Calculate the closest point on a curve from a specified position
     *
     * @param v the specified position away from a curve
     * @return the closest point on a curve
     * @see Bspline
     */
    protected abstract fun closestPoint(v: Vector3): Vector3

    fun getMesh(): Mesh {
        val c = this
        val linePerNode = 8
        val node = mutableListOf<Vector3>()
        //Draw curve (interpolation of pts)
        val n = linePerNode * (c.ctrlPts.size - 1)
        if (c.ctrlPts.size > 1) for (i in 0..n) {
            val t = i.toDouble() / n.toDouble()
            node.add(c(t))
        }
        val tmp = mutableListOf<Float>()
        for (v in node) {
            tmp.add(v[0].toFloat()); tmp.add(v[1].toFloat()); tmp.add(v[2].toFloat())
        }

        val positions: FloatArray = tmp.toFloatArray()
        tmp.clear()
        for(i in positions.indices) {
            tmp.add(1.0f)
        }
        val colours : FloatArray = tmp.toFloatArray()

        return Mesh(positions, colours)
    }
}