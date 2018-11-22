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
    private var leftButtonReleased = false

    private val hud: Hud = Hud()
    private val gui: Gui = Gui()
    private val camera: Camera = Camera()
    private val render: Render = Render()
    private val rayTrace: RayTrace = RayTrace()
    private val designItems = mutableListOf<DesignItem>()
    private val curves = mutableListOf<ParametricCurve>()
    private var gapOfpick: Vector3 = Vector3()
    private var currentIndex = 0
    private var indexOfPoint = -1

    private val colorOfCurve = floatArrayOf(0f, 1f, 0f)
    private val colorOfPoint = floatArrayOf(0f, 1f, 1f)
    private val colorOfCtrlPts = floatArrayOf(0.5f, 0.5f, 0.5f)
    private val colorOfTufts = floatArrayOf(1f, 0f, 0f)
    private val colorOfSelected = floatArrayOf(1f, 1f, 0f)

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
        val designItem = DesignItem(GL_LINE_STRIP, mesh)
        designItems.add(designItem)
    }

    fun Vector2d.toFloat() = Vector2f(x.toFloat(), y.toFloat())
    fun Vector3. toFloat() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    fun Vector3f.toDouble() = Vector3(x.toDouble(), y.toDouble(), z.toDouble())

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
                        if (window.isKeyPressed(GLFW_KEY_R) && spln != Spline.REMO) {
                            spln = Spline.REMO
                            println("R pressed")
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

        rayTrace.set(camera.position, window.getProjMat4(), render.getViewMatrix(camera))
        val vertex = rayTrace.rayPlane(window, mouse.currentPos, Vector3f(), Vector3f(0f, 0f, -camera.front.z/*1f*/)) // get vertex on z-plane
        //println("x=${vertex.x}, y=${vertex.y}, z=${vertex.z}")

        // mouse left button callback
        if (mouse.isLeftButtonPressed && !this.leftButtonPressed) {
            Save().colorPick(window, mouse.currentPos)
            when(mode) {
                Mode.VIEW -> {
                    val offset = camera.position.length() * 0.02
                    if(!curves.isEmpty()) for(curve in curves) {
                        var flag = false
                        if(curve.prm.size == 1) {
                            val radius = camera.position.length() * 0.02f
                            val v = curve(1.0)
                            flag = rayTrace.raySphere(window, mouse.currentPos,
                                    v.toFloat(), radius)
                        }
                        else {
                            val rayDir = rayTrace.rayDirection(window, mouse.currentPos)
                            val boundingPos = curve.getBoundingPolygon(offset, rayDir.toDouble())
                            for(i in 0 until boundingPos.size - 2) {
                                val v0 = boundingPos[i + 0].toFloat()
                                val v1 = boundingPos[i + 1].toFloat()
                                val v2 = boundingPos[i + 2].toFloat()
                                val l = rayTrace.rayTriangle(window, mouse.currentPos, v0, v1, v2)
                                //println("l=$l")
                                flag = l > 0f
                                if(flag) break
                            }
                        }
                        //println(flag)
                        if(flag) {
                            mode = Mode.CURV
                            curv = Curve.SPLN
                            currentIndex = curves.indexOf(curve)
                            updateDrawCurve(currentIndex, curve)
                        }
                    }
                }
                Mode.CURV -> {

                    val curve = curves[currentIndex]
                    when (spln) {
                        Spline.SLOP -> {
                            val radius = camera.position.length() * 0.1f
                            for (p in curve.prm) {
                                val v = curve(p)
                                val index = curve.prm.indexOf(p)
                                val flag = rayTrace.raySphere(window, mouse.currentPos, v.toFloat(), radius)
                                if (flag) {
                                    curve.addSlope(index, (vertex.toDouble() - v).normalize())
                                    break
                                }
                            }
                        }
                        Spline.REMO -> {
                            val radius = camera.position.length() * 0.02f
                            for (p in curve.prm) {
                                val v = curve(p)
                                val index = curve.prm.indexOf(p)
                                val flag = rayTrace.raySphere(window, mouse.currentPos, v.toFloat(), radius)
                                if (flag) {
                                    curve.removeSlope(index)
                                    break
                                }
                            }
                        }
                        Spline.PICK -> { }
                        Spline.DEL -> {
                            val radius = camera.position.length() * 0.02f
                            for (p in curve.prm) {
                                val v = curve(p)
                                val index = curve.prm.indexOf(p)
                                val flag = rayTrace.raySphere(window, mouse.currentPos, v.toFloat(), radius)
                                if (flag) {
                                    curve.removePts(index)
                                    break
                                }
                            }

                        }
                        Spline.ADD -> {
                            curve.addPts(vertex.toDouble())
                        }
                        Spline.MOVE -> {
                            val radius = camera.position.length() * 0.05f
                            for (p in curve.prm) {
                                val v = curve(p)
                                val index = curve.prm.indexOf(p)
                                val flag = rayTrace.raySphere(window, mouse.currentPos, v.toFloat(), radius)
                                if (flag){
                                    indexOfPoint = index
                                    val vertex = rayTrace.rayPlane(window, mouse.currentPos, Vector3f(), Vector3f(0f, 0f, 1f))
                                    //gapOfpick = v - vertex.toDouble()
                                    val v4 = Vector4f(v.x.toFloat(), v.y.toFloat(), v.z.toFloat(),1f)
                                            .mul(render.getViewMatrix(camera))
                                            .mul(window.getProjMat4())

                                    println("x= ${v4.x}, y= ${v4.y}")
                                    mouse.setCursorPos(window,
                                            (v4.x.toDouble() + 1.0) * window.width / 2,
                                            -(v4.y.toDouble() - 1.0) * window.height / 2)
                                    println("picked")
                                }
                            }
                        }
                    }
                    updateDrawCurve(currentIndex, curve)
                }
            }
        }
        this.leftButtonPressed = mouse.isLeftButtonPressed

        // mouse release callback
        if (mouse.isLeftButtonReleased && !this.leftButtonReleased) {
            when(spln) {
                Spline.MOVE -> {
                    indexOfPoint = -1
                }
            }
        }
        this.leftButtonReleased = mouse.isLeftButtonReleased
    }

    override fun update(interval: Float, window: GlfWindow, mouse: Mouse) {

        //zoom along to the ray direction
        val rayDir = rayTrace.rayDirection(window, mouse.currentPos)
        camera.translate(rayDir.mul(mouse.offset.toFloat()))

        if (mouse.isRightButtonPressed) {
            updateTranslation(mouse.motion.toFloat())
            updateRotation(mouse.motion.toFloat())
        }
        if (mouse.isLeftButtonPressed and (indexOfPoint != -1)) {
            val curve = curves[currentIndex]
            when(spln) {
                Spline.MOVE -> {
                    val vertex = rayTrace.rayPlane(window, mouse.currentPos, Vector3f(), Vector3f(0f, 0f, 1f))
                    //curve.modPts(indexOfPoint, vertex.toDouble())
                    updateDrawCurve(currentIndex, curve)
                }
            }
        }
    }

    private fun updateTranslation(v: Vector2f) {
        val mouseSensitivity = 0.01f
        v.mul(mouseSensitivity)
        if (!rotating) camera.translate(-v.x, v.y)
    }

    private fun updateRotation(v: Vector2f) {
        val mouseSensitivity = 0.05f
        v.mul(mouseSensitivity)
        if (rotating) camera.rotateGlobal(-v.y, -v.x)
        rotating = false
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