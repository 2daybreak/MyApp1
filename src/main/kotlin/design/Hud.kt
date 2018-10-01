package design

import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.nanovg.NanoVGGL3.*
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL

class Hud {

    enum class Curve(val b: Boolean, val id: Int, val tag: String, val shortcut: object) {
        SLOPE(flase, 0, "Slope", GLFW_KEY_S),
        ADD   (true, 1, "Add", GLFW_KEY_A),
        DELETE(true, 2, "Delete", GLFW_KEY_D),
        PICK  (true, 3, "Pick", GLFW_KEY_P)
        INSERT(true, 4, "Insert", GLFW_KEY_SHIFT)
        MOVE  (true, 5, "Move", GLFW_KEY_CTRL)
    }
    
    private var vg: Long = 0

    private var colour: NVGColor? = null

    private var fontBuffer: ByteBuffer? = null

    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    private var posx: DoubleBuffer? = null

    private var posy: DoubleBuffer? = null

    private var counter: Int = 0

    @Throws(Exception::class)
    fun init() {
        this.vg = nvgCreate(NVG_ANTIALIAS or NVG_STENCIL_STROKES) //nvgCreate(NVG_STENCIL_STROKES)
        if (this.vg == NULL) {
            throw Exception("Could not init nanovg")
        }

        fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024)
        val font = nvgCreateFontMem(vg, FONT_NAME, fontBuffer!!, 0)
        if (font == -1) {
            throw Exception("Could not add font")
        }
        colour = NVGColor.create()

        posx = MemoryUtil.memAllocDouble(1)
        posy = MemoryUtil.memAllocDouble(1)

        counter = 0
    }

    fun render(window: GlfWindow) {
        val width = window.width.toFloat()
        val height = window.height.toFloat()

        nvgBeginFrame(vg, width, height, 1f)

        // Upper ribbon
        nvgBeginPath(vg)
        nvgRect(vg, 0f, height - 100, width, 50f)
        nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour!!))
        nvgFill(vg)

        // Lower ribbon
        nvgBeginPath(vg)
        nvgRect(vg, 0f, height - 50, width, 10f)
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour!!))
        nvgFill(vg)

        glfwGetCursorPos(window.windowHandle, posx, posy)
        val xcenter = 50
        val ycenter = height - 75
        val radius = 20
        val x = posx!!.get(0).toInt()
        val y = posy!!.get(0).toInt()
        val hover = Math.pow((x - xcenter).toDouble(), 2.0) + Math.pow((y - ycenter).toDouble(), 2.0) < Math.pow(radius.toDouble(), 2.0)

        // Circle
        nvgBeginPath(vg)
        nvgCircle(vg, xcenter.toFloat(), ycenter.toFloat(), radius.toFloat())
        nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour!!))
        nvgFill(vg)

        // Clicks Text
        nvgFontSize(vg, 25.0f)
        nvgFontFace(vg, FONT_NAME)
        nvgTextAlign(vg, NVG_ALIGN_CENTER or NVG_ALIGN_TOP)
        if (hover) {
            nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour!!))
        } else {
            nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour!!))

        }
        nvgText(vg, 50f, height - 87, String.format("%02d", counter))

        // Render hour text
        nvgFontSize(vg, 40.0f)
        nvgFontFace(vg, FONT_NAME)
        nvgTextAlign(vg, NVG_ALIGN_LEFT or NVG_ALIGN_TOP)
        nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour!!))
        nvgText(vg, width - 150, height - 95, dateFormat.format(Date()))

        nvgEndFrame(vg)

        // Restore state
        window.restoreState()
    }

    fun incCounter() {
        counter++
        if (counter > 99) {
            counter = 0
        }
    }

    private fun rgba(r: Int, g: Int, b: Int, a: Int, colour: NVGColor): NVGColor {
        colour.r(r / 255.0f)
        colour.g(g / 255.0f)
        colour.b(b / 255.0f)
        colour.a(a / 255.0f)

        return colour
    }

    fun cleanup() {
        nvgDelete(vg)
        if (posx != null) {
            MemoryUtil.memFree(posx)
        }
        if (posy != null) {
            MemoryUtil.memFree(posy)
        }
    }

    companion object {

        private val FONT_NAME = "BOLD"
    }
}
