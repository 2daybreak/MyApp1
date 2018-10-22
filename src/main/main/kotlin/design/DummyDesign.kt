package design

import design.Enums.*
import geoModel.*
import gui.Gui
import linearAlgebra.Vector3
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*

class DummyDesign : IDesign {

    private var mode: Mode = Mode.VIEW
    private var curv: Curve = Curve.IDLE
    private var spln: Spline = Spline.IDLE

    private var rotating = false
    private var leftButtonPressed = false

    private val hud: Hud = Hud()
    private val gui: Gui = Gui()
    private val camera: Camera = Camera()
    private val render: Render = Render()
    private val rayTrace: RayTrace = RayTrace()
    private val designItems = mutableListOf<DesignItem>()
    private val curves = mutableListOf<ParametricCurve>()
    private var currentIndex = 0

    private val colorOfCurve = floatArrayOf(0f, 1f, 0f)
    private val colorOfPoint = floatArrayOf(1f, 1f, 1f)
    private val colorOfCtrlPts = floatArrayOf(0.5f, 0.5f, 0.5f)
    private val colorOfTufts = floatArrayOf(1f, 0f, 0f)
    private val colorOfSelected = floatArrayOf(1f, 1f, 0f)

    @Throws(Exception::class)
    override fun init(window: GlfWindow) {

        gui.init()
        hud.init()
        render.init()
        rayTrace.init(camera.position, window.inverseProjMat4, render.inverseView)

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
        val designItem = DesignItem(GL_LINE_STRIP, mesh)
        designItems.add(designItem)
    }

    override fun input(window: GlfWindow, mouse: Mouse) {

        if (window.isKeyPressed(GLFW_KEY_UP))
            camera.position.add(Vector3f(camera.front).mul(0.05f))

        if (window.isKeyPressed(GLFW_KEY_DOWN))
            camera.position.add(Vector3f(camera.front).mul(-0.05f))

        if (window.isKeyPressed(GLFW_KEY_LEFT))
            camera.position.add(Vector3f(camera.front).cross(camera.up).mul(-0.05f))

        if (window.isKeyPressed(GLFW_KEY_RIGHT))
            camera.position.add(Vector3f(camera.front).cross(camera.up).mul(0.05f))

        if (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) rotating = true

        if (window.isKeyPressed(GLFW_KEY_F))
            camera.setCamera(Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, -1f), Vector3f(0f, 1f, 0f))

        if (window.isKeyPressed(GLFW_KEY_ESCAPE)) {
            mode = Mode.VIEW
            curv = Curve.IDLE
            spln = Spline.IDLE
            if(currentIndex > -1) {
                val curve = curves[currentIndex]
                drawCurve(currentIndex, curve)
            }
        }

        if (window.isKeyPressed(GLFW_KEY_F3)) {
            mode = Mode.IDLE
            curv = Curve.IDLE
            spln = Spline.IDLE
        }

        if (window.isKeyPressed(GLFW_KEY_F4)) {
            Save().saveScreenShot(window.width, window.height, "screenshot.png")
        }
        when(mode) {
            Mode.IDLE, Mode.VIEW -> {
                if (window.isKeyPressed(GLFW_KEY_C)) {
                    mode = Mode.CURV
                    println("C pressed")
                }
                if (window.isKeyPressed(GLFW_KEY_S)) {
                    mode = Mode.SURF
                    println("S pressed")
                }
            }
            Mode.CURV -> {
                when (curv) {
                    Curve.IDLE -> {
                        if (window.isKeyPressed(GLFW_KEY_E) && curv != Curve.ELIP) {
                            curv = Curve.ELIP
                            //curves.add(Circle())
                            println("E pressed")
                        }
                        if (window.isKeyPressed(GLFW_KEY_S) && curv != Curve.SPLN) {
                            curv = Curve.SPLN
                            curves.add(InterpolatedBspline())
                            currentIndex = curves.size - 1
                            val curve = curves[currentIndex]
                            prepareDrawCurve(curve)
                            println("S pressed")
                        }
                        if (window.isKeyPressed(GLFW_KEY_B) && curv != Curve.BSPL) {
                            curv = Curve.BSPL
                            curves.add(Bspline())
                            currentIndex = curves.size - 1
                            val curve = curves[currentIndex]
                            prepareDrawCurve(curve)
                            println("B pressed")
                        }
                    }
                    Curve.ELIP -> {
                    }
                    Curve.SPLN -> {
                        if (window.isKeyPressed(GLFW_KEY_L) && spln != Spline.SLOP) {
                            spln = Spline.SLOP
                            println("L pressed")
                        }
                        if (window.isKeyPressed(GLFW_KEY_P) && spln != Spline.PICK) {
                            spln = Spline.PICK
                            println("P pressed")
                        }
                        if (window.isKeyPressed(GLFW_KEY_D) && spln != Spline.DEL) {
                            spln = Spline.DEL
                            println("D pressed")
                        }
                        if (window.isKeyPressed(GLFW_KEY_A) && spln != Spline.ADD) {
                            spln = Spline.ADD
                            println("A pressed")
                        }
                        if (window.isKeyPressed(GLFW_KEY_M) && spln != Spline.MOVE) {
                            spln = Spline.MOVE
                            println("M pressed")
                        }
                    }
                    Curve.BSPL -> {
                        if (window.isKeyPressed(GLFW_KEY_A) && spln != Spline.ADD) {
                            spln = Spline.ADD
                            println("A pressed")
                        }
                    }
                }
            }
            Mode.SURF -> {}
        }
        val aux = mouse.isLeftButtonPressed
        if (aux && !this.leftButtonPressed) {

            Save().colorPick(window, mouse.currentPos)

            when(mode) {
                Mode.VIEW -> {
                    val offset = camera.position.length() * 0.02
                    if(!curves.isEmpty()) for(curve in curves) {
                        val boundingPos = curve.getBoundingPolygon(offset)
                        for(i in 0 until boundingPos.size - 2) {
                            val v0 = Vector3f(boundingPos[i].x.toFloat(), boundingPos[i].y.toFloat(), boundingPos[i].z.toFloat())
                            val v1 = Vector3f(boundingPos[i + 1].x.toFloat(), boundingPos[i + 1].y.toFloat(), boundingPos[i + 1].z.toFloat())
                            val v2 = Vector3f(boundingPos[i + 2].x.toFloat(), boundingPos[i + 2].y.toFloat(), boundingPos[i + 2].z.toFloat())
                            val l = rayTrace.rayTriangle(window, mouse.currentPos, v0, v1, v2)
                            println("l=$l")
                            val flag = l > 0f
                            println(flag)
                            if(flag) {
                                mode = Mode.CURV
                                curv = Curve.SPLN
                                currentIndex = curves.indexOf(curve)
                                updateDrawCurve(currentIndex, curve)
                                break
                            }
                        }
                    }
                }
                Mode.CURV -> {
                    // get vertex on z-plane
                    val v = rayTrace.rayPlane(window, mouse.currentPos, Vector3f(), Vector3f(0f, 0f, 1f))
                    when (spln) {
                        Spline.SLOP -> { }
                        Spline.PICK -> { }
                        Spline.DEL -> {
                            val curve = curves[currentIndex]
                            val radius = camera.position.length() * 0.02f
                            for (p in curve.prm) {
                                val v = curve(p)
                                val index = curve.prm.indexOf(p)
                                val flag = rayTrace.raySphere(window, mouse.currentPos,
                                        Vector3f(v.x.toFloat(), v.y.toFloat(), v.z.toFloat()), radius)
                                if (flag) {
                                    curve.removePts(index)
                                    break
                                }
                            }
                            updateDrawCurve(currentIndex, curve)
                        }
                        Spline.ADD -> {
                            val curve = curves[currentIndex]
                            curve.addPts(Vector3(v.x, v.y, v.z))
                            println("x=${v.x}, y=${v.y}, z=${v.z}")
                            updateDrawCurve(currentIndex, curve)
                        }
                        Spline.MOVE -> { }
                    }
                }
            }
        }
        this.leftButtonPressed = aux
    }

    override fun update(interval: Float, mouse: Mouse) {
        updateZoom(mouse.offset)
        if (mouse.isRightButtonPressed) {
            updateTranslation(mouse.motion)
            updateRotation(mouse.motion)
        }
    }

    private fun updateTranslation(v: Vector2f) {
        val mouseSensitivity = 0.01f
        v.mul(mouseSensitivity)
        if (!rotating) camera.translate(-v.x, v.y)
    }

    private fun updateRotation(v: Vector2f) {
        val mouseSensitivity = 1f
        v.mul(mouseSensitivity)
        if (rotating) camera.rotateGlobal(-v.y, -v.x)
        rotating = false
    }

    private fun updateZoom(offset: Double) {
        val wheelSensitivity = 0.1f
        //render.quaternionf.scale((1f + wheelSensitivity * offset).toFloat())
        //render.scale *= (1f + wheelSensitivity * offset).toFloat()
        //camera.position.add(Vector3f(camera.front).mul(wheelSensitivity * offset.toFloat()))
        val scale = (1 - wheelSensitivity * offset.toFloat())
        camera.zoom(scale)
    }

    private fun prepareDrawCurve(curve: ParametricCurve) {
        designItems.add(DesignItem(GL_POINTS, curve.getPts(colorOfPoint)))
        designItems.add(DesignItem(GL_LINE_STRIP, curve.getCurve(colorOfCurve)))
        designItems.add(DesignItem(GL_POINTS, curve.getCtrlPts(colorOfCtrlPts)))
        designItems.add(DesignItem(GL_LINE_STRIP, curve.getCtrlPts(colorOfCtrlPts)))
        designItems.add(DesignItem(GL_LINES, curve.getTufts(colorOfTufts)))
    }

    private fun updateDrawCurve(index: Int, curve: ParametricCurve) {
        designItems[5 * index + 0 + 1].mesh = curve.getPts(colorOfSelected)
        designItems[5 * index + 1 + 1].mesh = curve.getCurve(colorOfSelected)
        designItems[5 * index + 2 + 1].mesh = curve.getCtrlPts(colorOfCtrlPts)
        designItems[5 * index + 3 + 1].mesh = curve.getCtrlPts(colorOfCtrlPts)
        designItems[5 * index + 4 + 1].mesh = curve.getTufts(colorOfTufts)
    }

    private fun drawCurve(index: Int, curve: ParametricCurve) {
        designItems[5 * index + 0 + 1].mesh = curve.getPts(colorOfPoint)
        designItems[5 * index + 1 + 1].mesh = curve.getCurve(colorOfCurve)
        designItems[5 * index + 2 + 1].mesh = Mesh(floatArrayOf(), floatArrayOf())
        designItems[5 * index + 3 + 1].mesh = Mesh(floatArrayOf(), floatArrayOf())
        designItems[5 * index + 4 + 1].mesh = Mesh(floatArrayOf(), floatArrayOf())
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