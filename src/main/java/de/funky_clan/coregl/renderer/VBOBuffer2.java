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
public final class VBOBuffer2 {
    public static final int BUFFER_SIZE = 0x10000;
    private ByteBuffer byteBuffer;
    private int vboId;
    private int vertices;
    private BufferedRenderer renderer;
    private MappedVertex buffer;
    private int maxVertices;

    public VBOBuffer2(BufferedRenderer renderer) {
        this.renderer = renderer;
        this.byteBuffer = createBuffer();
        this.vboId = renderer.genVBOId();
        buffer = MappedVertex.map(byteBuffer);
        maxVertices = BUFFER_SIZE / MappedVertex.SIZEOF;
    }
    
    protected ByteBuffer createBuffer() {
        return BufferUtils.createByteBuffer(BUFFER_SIZE);
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
        buffer.view = vertices;
        buffer.x = x;
        buffer.y = y;
        buffer.z = z;
        buffer.tx = tx;
        buffer.ty = ty;
        buffer.color = color;
        buffer.nx = nx;
        buffer.ny = ny;
        buffer.nz = nz;

        vertices ++;
    }

    public void ensureSpace(int vertices) {
        if( this.vertices+vertices > maxVertices ) {
            renderer.onBufferFull();
        }
    }

    public void upload() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        renderer.setupPointers();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STREAM_DRAW);
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

}
