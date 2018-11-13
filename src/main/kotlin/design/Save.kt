package design

import org.joml.Vector2d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import org.lwjgl.stb.STBImageWrite
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class Save {

    fun colorPick(window: GlfWindow, screenPos: Vector2d) {
        val x = screenPos.x.toInt()
        val y = window.height - screenPos.y.toInt()
        val buffer = MemoryUtil.memAlloc(4)
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
        fun Byte.toPositiveInt() = toInt() and 0xFF
        val r = buffer.get(0).toPositiveInt()
        val g = buffer.get(1).toPositiveInt()
        val b = buffer.get(2).toPositiveInt()
        val a = buffer.get(3).toPositiveInt()
        println("(x,y)=($x,$y) / rgba=$r,$g,$b,$a")
    }

    fun saveScreenShot(w: Int, h: Int, name: String) {
        val image = MemoryUtil.memAlloc(w * h * 4)

        // TODO: Make this work for GLES
        GL11C.glReadPixels(0, 0, w, h, GL11C.GL_RGBA, GL11C.GL_UNSIGNED_BYTE, image)
        unpremultiplyAlpha(image, w, h, w * 4)
        //setAlpha(image, w, h, w * 4, 255.toByte())
        flipHorizontal(image, w, h, w * 4)
        STBImageWrite.stbi_write_png(name, w, h, 4, image, w * 4)
        MemoryUtil.memFree(image)
    }

    private fun mini(a: Int, b: Int): Int {
        return if (a < b) a else b
    }

    private fun unpremultiplyAlpha(image: ByteBuffer, w: Int, h: Int, stride: Int) {
        var x: Int = 0
        var y: Int = 0

        // Unpremultiply
        while (y < h) {
            var row = y * stride
            while (x < w) {
                val r = image.get(row + 0).toInt()
                val g = image.get(row + 1).toInt()
                val b = image.get(row + 2).toInt()
                val a = image.get(row + 3).toInt()
                if (a != 0) {
                    image.put(row + 0, mini(r * 255 / a, 255).toByte())
                    image.put(row + 1, mini(g * 255 / a, 255).toByte())
                    image.put(row + 2, mini(b * 255 / a, 255).toByte())
                }
                row += 4
                x++
            }
            y++
        }

        // Defringe
        y = 0
        while (y < h) {
            var row = y * stride
            x = 0
            while (x < w) {
                var r = 0
                var g = 0
                var b = 0
                val a = image.get(row + 3).toInt()
                var n = 0
                if (a == 0) {
                    if (x - 1 > 0 && image.get(row - 1).toInt() != 0) {
                        r += image.get(row - 4).toInt()
                        g += image.get(row - 3).toInt()
                        b += image.get(row - 2).toInt()
                        n++
                    }
                    if (x + 1 < w && image.get(row + 7).toInt() != 0) {
                        r += image.get(row + 4).toInt()
                        g += image.get(row + 5).toInt()
                        b += image.get(row + 6).toInt()
                        n++
                    }
                    if (y - 1 > 0 && image.get(row - stride + 3).toInt() != 0) {
                        r += image.get(row - stride).toInt()
                        g += image.get(row - stride + 1).toInt()
                        b += image.get(row - stride + 2).toInt()
                        n++
                    }
                    if (y + 1 < h && image.get(row + stride + 3).toInt() != 0) {
                        r += image.get(row + stride).toInt()
                        g += image.get(row + stride + 1).toInt()
                        b += image.get(row + stride + 2).toInt()
                        n++
                    }
                    if (n > 0) {
                        image.put(row + 0, (r / n).toByte())
                        image.put(row + 1, (g / n).toByte())
                        image.put(row + 2, (b / n).toByte())
                    }
                }
                row += 4
                x++
            }
            y++
        }
    }

    private fun setAlpha(image: ByteBuffer, w: Int, h: Int, stride: Int, a: Byte) {
        var x: Int
        var y: Int
        y = 0
        while (y < h) {
            val row = y * stride
            x = 0
            while (x < w) {
                image.put(row + x * 4 + 3, a)
                x++
            }
            y++
        }
    }

    private fun flipHorizontal(image: ByteBuffer, w: Int, h: Int, stride: Int) {
        var i = 0
        var j = h - 1
        var k: Int
        while (i < j) {
            val ri = i * stride
            val rj = j * stride
            k = 0
            while (k < w * 4) {
                val t = image.get(ri + k)
                image.put(ri + k, image.get(rj + k))
                image.put(rj + k, t)
                k++
            }
            i++
            j--
        }
    }
}