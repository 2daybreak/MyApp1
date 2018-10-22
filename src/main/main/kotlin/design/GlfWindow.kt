package design

import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

class GlfWindow(val title: String, width: Int, height: Int, private var vSync: Boolean) {

    var width = width
        private set

    var height = height
        private set

    var windowHandle: Long = 0
        private set

    var isResized = false

    var perspective = true

    private val antialiasing = true

    private val zNear: Float = 0.01f

    private val zFar: Float = 10E6f

    private val fov = Math.toRadians(60.0).toFloat()

    val inverseProjMat4 = Matrix4f()

    fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)

        // Create the window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL)
        if (windowHandle == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowHandle) { window, width, height ->
            this.width = width
            this.height = height
            this.isResized = true
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle) { window, key, scancode, action, mods ->
            if (key == GLFW_KEY_F4 && mods == GLFW_MOD_ALT && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
            }
        }

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!
        // Center our window
        glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        )

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle)

        if (isvSync()) {
            // Enable v-sync
            glfwSwapInterval(1)
        }

        // Make the window visible
        glfwShowWindow(windowHandle)

        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.1f, 0.1f, 0.1f, 1f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)
        glEnable(GL_CULL_FACE)
        glEnable(GL_POINT_SMOOTH)
        glPointSize(5f)

        // Antialiasing
        if (antialiasing) {
            glfwWindowHint(GLFW_SAMPLES, 4)
        }
    }

    fun restoreState() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)
        glEnable(GL_CULL_FACE)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        //glDisable(GL_BLEND)
        //glDisable(GL_SCISSOR_TEST)
    }

    fun getProjMAt4(): Matrix4f {
        val aspectRatio = width.toFloat() / height.toFloat()
        val projectionMatrix = Matrix4f().identity()
        if(perspective) projectionMatrix.perspective(fov, aspectRatio, zNear, zFar)
        else projectionMatrix.ortho(-aspectRatio, aspectRatio, -1f, 1f, zNear, zFar)
        inverseProjMat4.set(Matrix4f(projectionMatrix).invert())
        return projectionMatrix
    }

    fun setClearColor(r: Float, g: Float, b: Float, alpha: Float) {
        glClearColor(r, g, b, alpha)
    }

    fun isKeyPressed(keyCode: Int): Boolean {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS
    }

    fun windowShouldClose(): Boolean {
        return glfwWindowShouldClose(windowHandle)
    }

    fun isvSync(): Boolean {
        return vSync
    }

    fun setvSync(vSync: Boolean) {
        this.vSync = vSync
    }

    fun update() {
        glfwSwapBuffers(windowHandle)
        glfwPollEvents()
    }
}
