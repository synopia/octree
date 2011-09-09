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

    protected List<VBOBuffer> buffers = new ArrayList<VBOBuffer>();
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
    public boolean begin(Object key, boolean force) {
        if( buffers.size()==0 ) {
            for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
                buffers.add( new VBOBuffer());
            }
        }

        return true;
    }

    @Override
    public void end() {
    }

    @Override
    public void onBufferFull() {
        VBOBuffer buffer = buffers.get(bufferIndex);
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
        VBOBuffer buffer = buffers.get(bufferIndex);
        buffer.ensureSpace(vertices);
    }

    @Override
    public ArrayList<String> getDebugInfos() {
        ArrayList<String> infos = super.getDebugInfos();
        int noBuffers = buffers.size();
        infos.add( String.format("VBOBuffers: %d", noBuffers));
        return infos;
    }
}
