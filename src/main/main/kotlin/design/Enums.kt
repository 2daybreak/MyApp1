package design

import design.Utils.loadResource
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.nanovg.NanoVG.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.*
import org.lwjgl.system.MemoryUtil.*
import java.nio.FloatBuffer

class Enums {

    private var vg: Long = 0


    /*companion object { //java compatibility */
    enum class Mode(val b: Boolean, val id: Int, val tag: String, val shorcut: Int) {
        IDLE(false, 0, "FPS", GLFW_KEY_F2),
        VIEW(true, 1, "View", GLFW_KEY_ESCAPE),
        CURV(true, 2, "Curve", GLFW_KEY_C),
        SURF(true, 3, "Surface", GLFW_KEY_S)
    }

    enum class View(val b: Boolean, val id: Int, val tag: String, val shortcut: Int) {
        IDLE(false, 0, "View", GLFW_KEY_ESCAPE),
        MOVE(true, 1, "Move", GLFW_KEY_LEFT_SHIFT),
        ROTA(true, 2, "Rotate", GLFW_KEY_LEFT_CONTROL),
    }

    enum class Curve(val b: Boolean, val id: Int, val tag: String, val shortcut: Int) {
        IDLE(false, 0, "Curve", GLFW_KEY_ESCAPE),
        ELIP(true, 1, "Elipse", GLFW_KEY_E),
        BSPL(true, 2, "Bspline", GLFW_KEY_B),
        SPLN(true, 3, "Spline", GLFW_KEY_S)
    }

    enum class Spline(val b: Boolean, val id: Int, val tag: String, val shortcut: Int) {
        IDLE(false, 0, "Spline", GLFW_KEY_ESCAPE),
        SLOP(true, 1, "Angle", GLFW_KEY_A),
        PICK(true, 2, "Pick", GLFW_KEY_P),
        DEL (true, 3, "Delete", GLFW_KEY_D),
        ADD (true, 4, "Add", GLFW_KEY_LEFT_SHIFT),
        MOVE(true, 5, "Move", GLFW_KEY_LEFT_CONTROL)
    }

}