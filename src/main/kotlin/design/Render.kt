package design

import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import design.Enums.*

class Render {

    private val transformation = Transform()

    private lateinit var shaderProgram: Shader

    private lateinit var hudShaderProgram: Shader

    @Throws(Exception::class)
    fun init() {
        // Create shader
        shaderProgram = Shader()
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"))
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
        shaderProgram.setUniform("projectionMatrix", window.getProjMat4())

        // Update world matrix & Render each gameItem
        for (item in designItems) {
            val modelMatrix = Matrix4f().set(transformation.getViewMatrix(item.position, item.rotation))
            shaderProgram.setUniform("worldMatrix",modelMatrix.mul(getViewMatrix(camera)))
            // Render
            item.mesh.render(item.type)
        }
        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }

    fun getViewMatrix(camera: Camera): Matrix4f {
        return Matrix4f(transformation.getViewMatrix(camera))
    }
}