package de.funky_clan.coregl.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author synopia
 */
public class StreamBufferedRenderer extends BaseBufferedRenderer {
    public static final int NUMBER_OF_BUFFERS = 8;

    protected List<Buffer> buffers = new ArrayList<Buffer>();
    protected int bufferIndex = 0;

    public StreamBufferedRenderer(int size) {
        super(size);
    }

    public StreamBufferedRenderer(int size, int texCoordFormat, int colorFormat, int normalFormat) {
        super(size, texCoordFormat, colorFormat, normalFormat);
    }

    public void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz) {
        buffers.get(bufferIndex).addVertex(x, y, z, tx, ty, color, nx, ny, nz);
    }

    @Override
    public boolean begin(Object key) {
        if( buffers.size()==0 ) {
            IntBuffer vboIds = BufferUtils.createIntBuffer(NUMBER_OF_BUFFERS);
            GL15.glGenBuffers(vboIds);
            for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
                buffers.add( new Buffer(createBuffer(), vboIds.get(i)));
            }
        }

        return true;
    }

    @Override
    public void end() {
    }

    @Override
    public void onBufferFull() {
        Buffer buffer = buffers.get(bufferIndex);
        if( buffer.getVertices()==0 ) {
            return;
        }
        buffer.upload();
        buffer.render();
        trianglesTotal += buffer.getVertices()/3;
        buffer.clear();
    }

    @Override
    public void render() {
        onBufferFull();
    }

    @Override
    public void ensureSpace(int vertices) {
        Buffer buffer = buffers.get(bufferIndex);
        buffer.ensureSpace(vertices);
    }
}
