package de.funky_clan.coregl.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
* @author synopia
*/
public class VBOBuffer2 implements VBO{
    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private int vboId;
    private int vertices;
    private BaseBufferedRenderer renderer;
    private int[] rawBuffer;
    private int pos;

    public VBOBuffer2(BaseBufferedRenderer renderer) {
        this.renderer = renderer;
        this.byteBuffer = renderer.createBuffer();
        this.intBuffer  = byteBuffer.asIntBuffer();
        this.vboId = renderer.genVBOId();
        rawBuffer = new int[byteBuffer.capacity()>>2];
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public int getVboId() {
        return vboId;
    }

    public int getVertices() {
        return vertices;
    }

    public void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz) {
        ensureSpace(1);
        vertices ++;

        rawBuffer[pos] = Float.floatToRawIntBits(x); pos++;
        rawBuffer[pos] = Float.floatToRawIntBits(y); pos++;
        rawBuffer[pos] = Float.floatToRawIntBits(z); pos++;
        if( renderer.getTexCoordFormat()!=0 ) {
            rawBuffer[pos] = Float.floatToRawIntBits(tx); pos++;
            rawBuffer[pos] = Float.floatToRawIntBits(ty); pos++;
        }
        if( renderer.getColorFormat()!=0 ) {
            rawBuffer[pos] = color; pos++;
        }
        if( renderer.getNormalFormat()!=0 ) {
            rawBuffer[pos] = Float.floatToRawIntBits(nx); pos++;
            rawBuffer[pos] = Float.floatToRawIntBits(ny); pos++;
            rawBuffer[pos] = Float.floatToRawIntBits(nz); pos++;
        }
    }

    public void ensureSpace(int vertices) {
        if( (rawBuffer.length-pos)<<2<vertices* renderer.getStrideSize() ) {
            renderer.onBufferFull();
        }
    }

    public void upload() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        renderer.setupPointers();
        intBuffer.rewind();
        intBuffer.put(rawBuffer);
        byteBuffer.rewind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STREAM_DRAW);
    }

    public void render() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        renderer.setupPointers();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices);
    }

    public void clear() {
        byteBuffer.clear();
        pos = 0;
        vertices = 0;
    }

    public void resize() {
        int size = byteBuffer.capacity()*2;
        byteBuffer = BufferUtils.createByteBuffer(size);
        intBuffer = byteBuffer.asIntBuffer();

        int[] newBuffer = new int[size>>2];
        System.arraycopy(rawBuffer, 0, newBuffer, 0, pos);

        rawBuffer = newBuffer;
    }
}
