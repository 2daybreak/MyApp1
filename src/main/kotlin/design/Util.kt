package design

import org.lwjgl.BufferUtils
import org.lwjgl.BufferUtils.createByteBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Scanner

object Utils {

    @Throws(Exception::class)
    fun loadResource(fileName: String): String {
        var result: String = ""
        Class.forName(Utils::class.java.name).
                getResourceAsStream(fileName).
                use { `in` -> Scanner(`in`, "UTF-8").
                        use { scanner -> result =
                                scanner.useDelimiter("\\A").next() } }
        return result
    }

    @Throws(IOException::class)
    fun ioResourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
        lateinit var buffer: ByteBuffer

        val path = Paths.get(resource)
        if (Files.isReadable(path)) {
            Files.newByteChannel(path).use { fc ->
                buffer = BufferUtils.createByteBuffer(fc.size().toInt() + 1)
                while (fc.read(buffer) != -1);
            }
        } else {
            Utils::class.java.getResourceAsStream(resource).use { source ->
                Channels.newChannel(source).use { rbc ->
                    buffer = createByteBuffer(bufferSize)

                    while (true) {
                        val bytes = rbc.read(buffer)
                        if (bytes == -1) {
                            break
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                        }
                    }
                }
            }
        }

        buffer.flip()
        return buffer
    }

    private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = BufferUtils.createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }
}
