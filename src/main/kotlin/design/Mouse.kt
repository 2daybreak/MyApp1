package design

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

class Mouse {

    // Normalized device coord.
    val currentPos: Vector2d = Vector2d()
    val motion: Vector2d = Vector2d()
    var offset: Double = 0.0; private set
    var isLeftButtonPressed: Boolean = false; private set
    var isLeftButtonReleased: Boolean = false; private set
    var isRightButtonPressed: Boolean = false; private set
    var isMiddleButtonPressed: Boolean = false; private set

    private val previousPos: Vector2d = Vector2d()
    private var scroll: Double = 0.0
    private var inWindow = false

    fun Vector2d.toFloat() = Vector2f(x.toFloat(), y.toFloat())

    fun init(window: GlfWindow) {
        glfwSetCursorPosCallback(window.windowHandle) {
            windowHandle, xpos, ypos ->
            currentPos.x = xpos
            currentPos.y = ypos
        }
        glfwSetCursorEnterCallback(window.windowHandle) {
            windowHandle, entered ->
            inWindow = entered
        }
        glfwSetScrollCallback(window.windowHandle) {
            windowHandle, dx, dy ->
            scroll = dy
        }
        glfwSetMouseButtonCallback(window.windowHandle) { windowHandle, button, action, mode ->
            isLeftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            isLeftButtonReleased = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE
            isRightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
            isMiddleButtonPressed = button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS
        }
    }

    fun input() {
        if (inWindow) {
            motion.x = (currentPos.x - previousPos.x)
            motion.y = (currentPos.y - previousPos.y)
            previousPos.set(currentPos)
        }
        offset = scroll
        scroll = 0.0
    }

    fun setCursorPos(window: GlfWindow, xpos: Double, ypos: Double) {
        glfwSetCursorPos(window.windowHandle, xpos, ypos)
    }
}