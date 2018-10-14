package gui

import org.lwjgl.opengl.GL30.*

import design.GlfWindow
import design.Utils
import org.joml.Matrix4f

class Renderer {

    private lateinit var shaderProgram: ShaderProgram

    @Throws(Exception::class)
    fun init() {
        // create shader
        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertexGui.glsl"))
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragmentGui.glsl"))
        shaderProgram.link()
        // create uniform
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("hasTexture")
        shaderProgram.createUniform("texture_sampler")
    }

    fun clear() {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT or GL_STENCIL_BUFFER_BIT)
    }

    fun render(window: GlfWindow, meshs: List<Mesh>) {
        val width = window.width.toFloat()
        val height = window.height.toFloat()

        glStencilFunc(GL_ALWAYS, 1, 0xFF) // all fragments should update the stencil buffer
        glStencilMask(0xFF) // enable writing to the stencil buffer
        /*do something for stencil buffer...*/

        shaderProgram.bind()

        // Update projection matrix
        val aspectRatio = width / height
        val projectionMatrix = Matrix4f().ortho(-aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)
        for(mesh in meshs) {
            shaderProgram.setUniform("hasTexture", mesh.hasTexture)
            shaderProgram.setUniform("texture_sampler", 0)
            mesh.render()
        }

        // Restore state
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        shaderProgram.unbind()
    }

    fun cleanup() {
        shaderProgram.cleanup()
    }
}
