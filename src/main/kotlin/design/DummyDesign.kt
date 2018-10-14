package design

import design.Enums.*
import geoModel.*
import linearAlgebra.Vector3
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import gui.DummyGui

class DummyDesign : IDesign {

    private var mode: Mode = Mode.VIEW

    private var curv: Curve = Curve.IDLE

    private var spln: Spline = Spline.IDLE

    private var moving = false

    private var rotating = false

    private val hud: Hud = Hud()

    private val gui = DummyGui()

    private val render: Render = Render()

    private val designItems = mutableListOf<DesignItem>()

    private val curves = mutableListOf<ParametricCurve>()

    private var leftButtonPressed = false

    private val camera = Camera(Vector3f(0f, 0f, 3f))

    @Throws(Exception::class)
    override fun init(window: GlfWindow) {

        gui.init()
        hud.init()
        render.init()

        // Create the gui.Mesh
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

        val mesh = Mesh(positions, colours)
        val designItem = DesignItem(mesh)
        designItem.setPosition(0f, 0f, 0f)
        designItems.add(designItem)
        designItems.add(designItem)
    }

    override fun input(window: GlfWindow, mouse: Mouse) {

        if (window.isKeyPressed(GLFW_KEY_W)) camera.position.add(Vector3f(camera.front).mul(0.05f))
        if (window.isKeyPressed(GLFW_KEY_S)) camera.position.add(Vector3f(camera.front).mul(-0.05f))
        if (window.isKeyPressed(GLFW_KEY_A)) camera.position.add(Vector3f(camera.front).cross(Vector3f(camera.up)).normalize().mul(-0.05f))
        if (window.isKeyPressed(GLFW_KEY_D)) camera.position.add(Vector3f(camera.front).cross(Vector3f(camera.up)).normalize().mul(0.05f))

        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
            mode = Mode.VIEW
            curv = Curve.IDLE
            spln = Spline.IDLE
        }
        if (window.isKeyPressed(GLFW_KEY_F2)) {
            mode = Mode.IDLE
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            when {
                mode == Mode.IDLE -> moving = true
                mode == Mode.VIEW -> moving = true
                curv == Curve.BSPL -> spln = Spline.INSE
                curv == Curve.SPLN -> spln = Spline.INSE
            }
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            when {
                mode == Mode.IDLE -> rotating = true
                mode == Mode.VIEW -> rotating = true
                curv == Curve.BSPL -> spln = Spline.MOVE
                curv == Curve.SPLN -> spln = Spline.MOVE
            }
        }
        if (window.isKeyPressed(GLFW_KEY_C)) {
            mode = Mode.CURV
        }
        if (window.isKeyPressed(GLFW_KEY_C)) {
            if(mode == Mode.CURV) {
                curv = Curve.SPLN
                curves.add(InterpolatedBspline())
            }
        }
        val aux = mouse.isLeftButtonPressed
        if (aux && !this.leftButtonPressed) {
            when(mode) {
                Mode.CURV -> {
                    when(curv) {
                        Curve.SPLN -> {
                            val pos: Vector2d = mouse.position
                            updateCurve(pos)
                        }

                    }
                }
                else -> {}
            }
        }
        this.leftButtonPressed = aux
    }

    override fun update(interval: Float, mouse: Mouse) {

        val off: Double = mouse.offset
        updateZoom(off)

        if (mouse.isLeftButtonPressed) {
            val del: Vector2d = mouse.motion
            updateTranslation(del)
            updateRotation(del)
        }
    }

    private fun updateTranslation(x: Float, y: Float, z: Float) {
        val mouseSensitivity = 5f
        camera.position.add(Vector3f(camera.front).cross(Vector3f(camera.up)).normalize().mul(-x * mouseSensitivity))
        camera.position.add(Vector3f(camera.up).mul(-y * mouseSensitivity))
    }

    private fun updateTranslation(del: Vector2d) {
        if (moving) updateTranslation(del.x.toFloat(), del.y.toFloat(), 0f)
        moving = false
    }

    private fun updateRotation(del: Vector2d) {
        if (rotating) {
            val mouseSensitivity = 5f
            when(mode.b) {
                true -> {
                    val q = render.quaternionf
                    q.rotateLocalX(mouseSensitivity * -del.y.toFloat())
                    q.rotateLocalY(mouseSensitivity * del.x.toFloat())
                }
                false -> {
                    camera.quaternionf.identity()
                    camera.quaternionf.rotateAxis(mouseSensitivity * del.y.toFloat(), camera.right)
                    camera.quaternionf.rotateAxis(mouseSensitivity * -del.x.toFloat(), camera.up)
                    camera.setRotation()
                }
            }
            rotating = false
        }
    }

    private fun updateZoom(offset: Double) {
        val wheelSensitivity = 1f
        camera.position.add(Vector3f(camera.front).mul(wheelSensitivity * offset.toFloat()))
    }

    private fun updateCurve(e: Vector2d) {
        if (!moving) {
            val p = camera.position
            // NDC to model coord.
            val v = Vector4f(e.x.toFloat(), e.y.toFloat(), -1f, 1f).mul(Matrix4f(render.projMat4).invert()).mul(Matrix4f(render.viewMat4).invert())
            val c = curves[0]
            c.addPts(Vector3(v.x, v.y, v.z))
            println("x=${v.x}, y=${v.y}, z=${v.z}")
            val item = DesignItem(c.getMesh())
            designItems[0] = item
        }
    }

    override fun render(window: GlfWindow) {
        render.render(window, mode, camera, designItems)
        gui.render(window)
        hud.render(window, mode, curv, spln)
    }

    override fun cleanup() {
        render.cleanup()
        for (gameItem in designItems) {
            gameItem.mesh.cleanUp()
        }
        hud.cleanup()
    }
}

