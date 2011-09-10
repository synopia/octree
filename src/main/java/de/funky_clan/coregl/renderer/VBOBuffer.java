package de.funky_clan.coregl.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

/**
* @author synopia
*/
public class VBOBuffer {
    private ByteBuffer byteBuffer;
    private int vboId;
    private int vertices;
    private BaseBufferedRenderer renderer;

    public VBOBuffer(BaseBufferedRenderer renderer) {
        this.renderer = renderer;
        this.byteBuffer = renderer.createBuffer();
        this.vboId = renderer.genVBOId();
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

        byteBuffer.putFloat(x);
        byteBuffer.putFloat(y);
        byteBuffer.putFloat(z);
        if( renderer.getTexCoordFormat()!=0 ) {
            byteBuffer.putFloat(tx);
            byteBuffer.putFloat(ty);
        }
        if( renderer.getColorFormat()!=0 ) {
            byteBuffer.putInt(color);
        }
        if( renderer.getNormalFormat()!=0 ) {
            byteBuffer.putFloat(nx);
            byteBuffer.putFloat(ny);
            byteBuffer.putFloat(nz);
        }
    }

    public void ensureSpace(int vertices) {
        if( byteBuffer.remaining()<vertices* renderer.getStrideSize() ) {
            renderer.onBufferFull();
        }
    }

    public void upload() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        renderer.setupPointers();
        byteBuffer.flip();
        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STREAM_DRAW );
    }

    public void render() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        renderer.setupPointers();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices);
    }

    public void clear() {
        byteBuffer.clear();
        vertices = 0;
    }

    public void resize() {
        int size = byteBuffer.capacity()*2;
        ByteBuffer newByteBuffer = BufferUtils.createByteBuffer(size);
        byteBuffer.rewind();
        newByteBuffer.put(byteBuffer);
        newByteBuffer.position(vertices* renderer.getStrideSize());

        byteBuffer = newByteBuffer;
    }
}
