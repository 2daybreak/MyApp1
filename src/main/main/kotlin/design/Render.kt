package design

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import design.Enums.*

class Render {

    val inverseView = Matrix4f()

    private val transformation = Transform()

    private lateinit var shaderProgram: Shader

    private lateinit var hudShaderProgram: Shader

    @Throws(Exception::class)
    fun init() {
        // Create shader
        shaderProgram = Shader()
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex0.glsl"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment0.glsl"))
        shaderProgram.link()

        // Create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("worldMatrix")
        //shaderProgram.createUniform("texture_sampler")
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun render(window: GlfWindow, mode: Mode, camera: Camera, designItems: List<DesignItem>) {
        clear()

        if (window.isResized) {
            glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }

        // Bind(finalize) the data
        shaderProgram.bind()

        // Update projection matrix
        shaderProgram.setUniform("projectionMatrix", window.getProjMAt4())

        // Set world matrix for this item
        val viewMatrix = Matrix4f(transformation.getWorldMatrix(camera))
        inverseView.set(Matrix4f(viewMatrix).invert())

        // Update world matrix & Render each gameItem
        for (item in designItems) {
            val modelMatrix = Matrix4f(transformation.getWorldMatrix(item.position, item.rotation))
            val worldMatrix = Matrix4f(viewMatrix.mul(modelMatrix))
            shaderProgram.setUniform("worldMatrix",worldMatrix)
            // Render
            item.mesh.render(item.type)
        }
        shaderProgram.unbind()
    }

    fun getProjMat4() {

    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}