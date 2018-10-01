package design

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

class GlfWindow(val title: String, width: Int, height: Int, private var vSync: Boolean) {

    var width: Int = width
        private set

    var height: Int = height
        private set

    var windowHandle: Long = 0
        private set

    var isResized: Boolean = false

    val antialiasing = true

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
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
        // Center our window
        glfwSetWindowPos(
                windowHandle,
                (vidmode!!.width() - width) / 2,
                (vidmode!!.height() - height) / 2
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
        glClearColor(0.1f, 0.1f, 0.1f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)

        // Antialiasing
        if (antialiasing) {
            glfwWindowHint(GLFW_SAMPLES, 4)
        }
        
        
        
            InputStream stream = new FileInputStream("C:\\path\\to\\image.png");
    BufferedImage image = ImageIO.read(stream);

    int width = image.getWidth();
    int height = image.getHeight();

    int[] pixels = new int[width * height];
    image.getRGB(0, 0, width, height, pixels, 0, width);

    // convert image to RGBA format
    ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

    for (int y = 0; y < height; y++)
    {
        for (int x = 0; x < width; x++)
        {
            int pixel = pixels[y * width + x];

            buffer.put((byte) ((pixel >> 16) & 0xFF));  // red
            buffer.put((byte) ((pixel >> 8) & 0xFF));   // green
            buffer.put((byte) (pixel & 0xFF));          // blue
            buffer.put((byte) ((pixel >> 24) & 0xFF));  // alpha
        }
    }
    buffer.flip(); // this will flip the cursor image vertically

    // create a GLFWImage
    GLFWImage cursorImg= GLFWImage.create();
    cursorImg.width(width);     // setup the images' width
    cursorImg.height(height);   // setup the images' height
    cursorImg.pixels(buffer);   // pass image data

    // create custom cursor and store its ID
    int hotspotX = 0;
    int hotspotY = 0;
    long cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX , hotspotY);

    // set current cursor
    glfwSetCursor(window, cursorID);
        
    }

    fun restoreState() {
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_STENCIL_TEST)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
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
