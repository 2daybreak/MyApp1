package design

import geoModel.*
import linearAlgebra.Vector3
import org.joml.*

import org.lwjgl.glfw.GLFW.*

class DummyDesign : IDesign {

    enum class Mode(val b: Boolean, val id: Int, val tag: String) {
        VIEW(false, 0, "View"),
        CURV(true,  1, "Curve"),
        SURF(true,  2, "Surface")
    }

    private var mode = Mode.VIEW

    private var moving = false

    private var rotating = false

    private val hud: Hud = Hud()

    private val render: Render = Render() /*Renderer = Renderer()*/

    private val designItems = mutableListOf<DesignItem>()

    private val curve = mutableListOf<ParametricCurve>()

    private var leftButtonPressed = false

    init {

    }

    @Throws(Exception::class)
    override fun init(window: GlfWindow) {

        hud.init()
        render.initShader()
        // Create the Mesh
        val positions = floatArrayOf(
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f
                )
        val colours = floatArrayOf(
                // V0
                1.0f, 1.0f, 1.0f,
                // V1
                1.0f, 0.0f, 0.0f,
                // V2
                0.0f, 1.0f, 0.0f,
                // V3
                0.0f, 0.0f, 1.0f,
                // V4
                0.0f, 1.0f, 1.0f,
                // V5
                1.0f, 1.0f, 0.0f,
                // V6
                1.0f, 0.0f, 1.0f,
                // V7
                1.0f, 1.0f, 1.0f
        )

        val grid = Grid(positions, colours)

        val designItem = DesignItem(grid)
        designItem.setPosition(0f, 0f, -4f)
        designItems.add(designItem)
        designItems.add(designItem)

    }

    override fun input(window: GlfWindow, mouse: Mouse) {

        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) mode = Mode.VIEW
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) moving = true
        if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) rotating = true
        if (window.isKeyPressed(GLFW_KEY_C)) {
            curve.add(InterpolatedBspline())
            mode = Mode.CURV
            println("C pressed")
        }

        val aux = mouse.isLeftButtonPressed
        if (aux && !this.leftButtonPressed) {
            when(mode) {
                Mode.CURV -> {
                    val pos: Vector2d = mouse.position
                    updateCurve(pos)
                    println("x=${pos.x}, y=${pos.y}")
                }
                else -> {}
            }
        }
        this.leftButtonPressed = aux
    }

    override fun update(interval: Float, mouse: Mouse) {

        val off: Double = mouse.offset
        updateScale(off)

        if (mouse.isLeftButtonPressed) {
            val del: Vector2f = mouse.displVec
            when(mode) {
                Mode.VIEW -> {
                    updateTranslation(del)
                    updateRotation(del)
                }
                Mode.CURV -> {
                    updateTranslation(del)
                }
                Mode.SURF -> {}
            }
        }
    }

    private fun updateTranslation(del: Vector2f) {
        val mouseSensitivity = 5f
        for (gameItem in designItems) if (moving) {
            val pos = gameItem.position
            pos.x += mouseSensitivity * del.x
            pos.y += mouseSensitivity * del.y
            gameItem.setPosition(pos.x, pos.y, pos.z)
        }
        moving = false
    }

    private fun updateRotation(del: Vector2f) {
        val mouseSensitivity = 5f
        for (gameItem in designItems) if (rotating) {
            val q = gameItem.quaternion
            q.rotateLocalX(mouseSensitivity * -del.y)
            q.rotateLocalY(mouseSensitivity * del.x)
        }
        rotating = false
    }

    private fun updateScale(offset: Double) {
        val wheelSensitivity = 0.05f
        for (gameItem in designItems) {
            val q = gameItem.quaternion
            var scale = gameItem.scale
            scale += (wheelSensitivity * offset).toFloat()
            q.scale(scale)
            gameItem.setQuaternion(q.x, q.y, q.z, q.w)
        }
    }

    private fun updateCurve(e: Vector2d) {

        val v = Vector4d(e.x, e.y, -4.0, 1.0).mul(render.projectMat4.invert())
        val c = curve[0]
        c.addPts(Vector3(v.x, v.y, 0.0))
        val item = DesignItem(c.getGrid())
        item.setPosition(0f, 0f, -4.0f)
        designItems[0] = item
    }

    override fun render(window: GlfWindow) {
        render.render(window, designItems)
        hud.render(window)
    }

    override fun cleanup() {
        render.cleanup()
        for (gameItem in designItems) {
            gameItem.grid.cleanUp()
        }
    }

}
