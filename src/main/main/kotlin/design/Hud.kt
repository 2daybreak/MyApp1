package design

import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.text.SimpleDateFormat
import java.util.Date
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL
import design.Enums.*

class Hud {

    private var vg: Long = 0

    private var colour: NVGColor = NVGColor.create()

    private var paint: NVGPaint = NVGPaint.create()

    private var fontBuffer: ByteBuffer? = null

    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    private var posx: DoubleBuffer = MemoryUtil.memAllocDouble(1)

    private var posy: DoubleBuffer = MemoryUtil.memAllocDouble(1)

    private var counter: Int = 0

    private var dir = NVG_CW

    private val font = Font.OPENSANSB

    enum class Font(val id: Int, val file: String) {
        SCRIPT   (0, "/fonts/FREESCPT.ttf"),
        GEORGIA  (1, "/fonts/georgia.ttf"),
        GEORGIAB (2, "/fonts/georgiab.ttf"),
        GEORGIAI (3, "/fonts/georgiai.ttf"),
        GEORGIAZ (4, "/fonts/georgiaz.ttf"),
        OPENSANS (5, "/fonts/OpenSans-Regular.ttf"),
        OPENSANSB(6, "/fonts/OpenSans-Bold.ttf"),
        PRECIOUS (7, "/fonts/Precious.ttf"),
        VIVALDI  (8, "/fonts/VIVALDII.ttf")
    }

    @Throws(Exception::class)
    fun init() {
        this.vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES)
        if (this.vg == NULL) throw Exception("Could not init nanovg")
        fontBuffer = Utils.ioResourceToByteBuffer(font.file, 150 * 1024)
        val nvgfont = nvgCreateFontMem(vg, font.toString(), fontBuffer?: ByteBuffer.allocate(0), 0)
        if (nvgfont == -1) throw Exception("Could not add font")
    }

    fun render(window: GlfWindow, mode: Mode, curv: Curve, spln: Spline) {
        val width = window.width.toFloat()
        val height = window.height.toFloat()
        glfwGetCursorPos(window.windowHandle, posx, posy)

        nvgBeginFrame(vg, width, height, 1f)
        nvgFontFace(vg, font.toString())

        // Menu
        fun menu(x: Float, y: Float, w: Float, h: Float) {
            nvgLinearGradient(vg, x, y, w, h,
                    rgba(60, 10, 70, 200),
                    rgba(10, 45, 80, 200),
                    paint)
            // rect
            nvgBeginPath(vg)
            nvgRect(vg, x, y, w, h)
            nvgFillPaint(vg, paint)
            val hover = posy.get(0) < h
            if (hover) nvgFill(vg)
            // Render hour text
            nvgFontSize(vg, 30.0f)
            nvgTextAlign(vg, NVG_ALIGN_RIGHT or NVG_ALIGN_TOP)
            nvgFillColor(vg, rgba(255, 255, 255, 200))
            if (hover) nvgText(vg, width, 0f, dateFormat.format(Date()))

        }
        menu(0f, 0f, width, 30f)

        // Tree
        fun tree(x: Float, y: Float, w: Float, h: Float) {
            nvgLinearGradient(vg, x, y, w, h,
                    rgba(60, 10, 70, 120),
                    rgba(10, 45, 80, 120),
                    paint)
            // rect
            nvgBeginPath(vg)
            nvgRect(vg, x, y, w, h)
            nvgFillPaint(vg, paint)
            val hover = posx.get(0) < w
            if (hover) nvgFill(vg)
        }
        tree(0f, 0f, 200f, height)

        // Control Circle
        fun circle(x: Float, y: Float, r: Float) {
            val deg = 2 * NVG_PI * counter / 360f
            val deg2 = deg * 2f + 0.00001f
            nvgLinearGradient(vg, x - r, y - r, x + r, y + r,
                    color(1f, 1f, 0.2f, 0.5f),
                    color(1f, 0.2f, 0.2f, 0.5f),
                    paint)
            nvgStrokePaint(vg, paint)
            // arc
            nvgBeginPath(vg)
            nvgArc(vg, x, y, r, deg, 0.25f * NVG_PI + deg, NVG_CCW)
            nvgStrokeWidth(vg, 0.25f * r)
            nvgStroke(vg)
            // arc
            /*nvgBeginPath(vg)
            nvgArc(vg, x, y, r, deg2, 0.25f * NVG_PI + deg2, NVG_CCW)
            nvgStrokeWidth(vg, 0.12f * r)
            nvgStroke(vg)*/
            // arc
            nvgBeginPath(vg)
            nvgArc(vg, x, y, r, NVG_PI - deg, NVG_PI - deg2, dir)
            nvgStrokeWidth(vg, 0.12f * r)
            nvgStroke(vg)
            nvgBeginPath(vg)
            // text
            nvgBeginPath(vg)
            nvgFontSize(vg, 0.7f * r)
            val hover = Math.pow((posx.get(0) - x), 2.0) + Math.pow((posy.get(0) - y), 2.0) < Math.pow(r.toDouble(), 2.0)
            if (hover)
                nvgFillColor(vg, color(1f, 1f, 0.2f, 0.5f))
            else
                nvgFillPaint(vg, paint)
            nvgTextAlign(vg, NVG_ALIGN_CENTER or NVG_ALIGN_MIDDLE)
            when(spln) {
                Spline.IDLE -> when(curv) {
                    Curve.IDLE -> nvgText(vg, x, y, mode.tag)
                    else -> nvgText(vg, x, y, curv.tag)
                }
                else ->  nvgText(vg, x, y, spln.tag)
            }

            counter += 1
            if (counter >= 360) {
                counter = 0
                dir = when (dir) {
                    NVG_CW -> NVG_CCW
                    NVG_CCW -> NVG_CW
                    else -> NVG_CW
                }
            }

            if (hover) {
                when(curv) {
                    Curve.SPLN -> {
                        nvgTextAlign(vg, NVG_ALIGN_CENTER or NVG_ALIGN_MIDDLE)
                        nvgText(vg, x, y - 6.5f * r, Spline.SLOP.tag)
                        nvgText(vg, x, y - 5.5f * r, Spline.PICK.tag)
                        nvgText(vg, x, y - 4.5f * r, Spline.DEL.tag)
                        nvgText(vg, x, y - 3.5f * r, Spline.ADD.tag)
                        nvgText(vg, x, y - 1.5f * r, Spline.MOVE.tag)
                    }
                    Curve.IDLE -> {
                        when(mode) {
                            Mode.VIEW -> {
                                nvgTextAlign(vg, NVG_ALIGN_CENTER or NVG_ALIGN_MIDDLE)
                                nvgText(vg, x, y - 3.5f * r, View.MOVE.tag)
                                nvgText(vg, x, y - 2.5f * r, View.ROTA.tag)
                                nvgText(vg, x, y - 1.5f * r, View.IDLE.tag)
                            }

                            Mode.CURV -> {
                                nvgTextAlign(vg, NVG_ALIGN_CENTER or NVG_ALIGN_MIDDLE)
                                nvgText(vg, x, y - 3.5f * r, Curve.ELIP.tag)
                                nvgText(vg, x, y - 2.5f * r, Curve.BSPL.tag)
                                nvgText(vg, x, y - 1.5f * r, Curve.SPLN.tag)
                            }
                        }
                    }
                }
            }

        }
        circle(0.5f * width, 0.82f * height, 0.08f * height)



        nvgEndFrame(vg)

        // Restore state
        window.restoreState()

    }

    private fun rgba(r: Int, g: Int, b: Int, a: Int, colour: NVGColor): NVGColor {
        colour.r(r / 255.0f)
        colour.g(g / 255.0f)
        colour.b(b / 255.0f)
        colour.a(a / 255.0f)

        return colour
    }

    private fun rgba(r: Int, g: Int, b: Int, a: Int): NVGColor {
        return color(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    private fun color(r: Float, g: Float, b: Float, a: Float): NVGColor {
        val c = NVGColor.create()
        c.r(r)
        c.g(g)
        c.b(b)
        c.a(a)
        return c
    }

    fun cleanup() {
        nvgDelete(vg)
        MemoryUtil.memFree(posx)
        MemoryUtil.memFree(posy)
    }

}
