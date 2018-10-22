package design

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

class Mouse {

    // Normalized device coord.
    val currentPos: Vector2d = Vector2d()
    val motion = Vector2f()
    var offset: Double = 0.0; private set
    var isLeftButtonPressed: Boolean = false; private set
    var isRightButtonPressed: Boolean = false; private set
    var isMiddleButtonPressed: Boolean = false; private set
    private val previousPos: Vector2d = Vector2d()
    private var scroll: Double = 0.0
    private var inWindow = false

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
            isRightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
            isMiddleButtonPressed = button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS
        }
    }

    fun input() {
        if (inWindow) {
            motion.x = (currentPos.x - previousPos.x).toFloat()
            motion.y = (currentPos.y - previousPos.y).toFloat()
            previousPos.set(currentPos)
        }
        offset = scroll
        scroll = 0.0
    }
}