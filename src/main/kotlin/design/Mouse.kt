package design

import org.joml.Vector2d

import org.lwjgl.glfw.GLFW.*

class Mouse {

    private val previousPos: Vector2d = Vector2d(-1.0, -1.0)

    val position: Vector2d = Vector2d()

    val motion: Vector2d = Vector2d()

    private var currentScroll: Double = 0.toDouble()

    var offset: Double = 0.0
        private set

    var isLeftButtonPressed = false
        private set

    var isRightButtonPressed = false
        private set

    var isMiddleButtonPressed = false
        private set

    private var inWindow = false

    fun init(window: GlfWindow) {
        glfwSetCursorPosCallback(window.windowHandle) { windowHandle, xpos, ypos ->
            val w = window.width
            val h = window.height
            position.x = 2 * xpos / w - 1.0
            position.y = 1.0 - 2 * ypos / h
        }
        glfwSetCursorEnterCallback(window.windowHandle) { windowHandle, entered -> inWindow = entered }
        glfwSetMouseButtonCallback(window.windowHandle) { windowHandle, button, action, mode ->
            isLeftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            isRightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
            isMiddleButtonPressed = button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS
        }
        glfwSetScrollCallback(window.windowHandle) { windowHandle, dx, dy -> currentScroll = dy }
    }

    fun input(window: GlfWindow) {
        if (inWindow) {
            motion.x = position.x - previousPos.x
            motion.y = position.y - previousPos.y
        }
        previousPos.x = position.x
        previousPos.y = position.y

        offset = currentScroll
        currentScroll = 0.0
    }
}
