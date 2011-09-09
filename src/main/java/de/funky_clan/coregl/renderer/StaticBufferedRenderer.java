package de.funky_clan.coregl.renderer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author synopia
 */
public class StaticBufferedRenderer extends BaseBufferedRenderer {
    private HashMap<Object, VBOBuffer> buffers = new HashMap<Object, VBOBuffer>();
    private Object currentKey;
    private long totalVBOBytes = 0;

    public StaticBufferedRenderer(int size) {
        super(size);
    }

    public StaticBufferedRenderer(int size, int texCoordFormat, int colorFormat, int normalFormat) {
        super(size, texCoordFormat, colorFormat, normalFormat);
    }

    @Override
    public void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz) {
        VBOBuffer buffer = buffers.get(currentKey);
        buffer.addVertex(x, y, z, tx, ty, color, nx, ny, nz);
    }

    @Override
    public void prepare() {
        super.prepare();
        totalVBOBytes = 0;
    }

    @Override
    public boolean begin(Object key, boolean force) {
        boolean result = false;
        VBOBuffer buffer = null;
        if( !buffers.containsKey( key ) ) {
            buffer = new VBOBuffer(BaseBufferedRenderer.this);
            buffers.put(key, buffer);
            result = true;
        }
        currentKey = key;
        if( force ) {
            buffer = buffers.get(key);
            buffer.clear();
        }
        return result;
    }

    @Override
    public void onBufferFull() {
        VBOBuffer buffer = buffers.get(currentKey);
        buffer.resize();
    }

    @Override
    public void render() {
        VBOBuffer buffer = buffers.get(currentKey);
        buffer.render();
        trianglesTotal += buffer.getVertices()/3;
        totalVBOBytes += buffer.getByteBuffer().capacity();
    }

    @Override
    public void end() {
        VBOBuffer buffer = buffers.get(currentKey);
        buffer.upload();

    }

    @Override
    public void ensureSpace(int vertices) {
        VBOBuffer buffer = buffers.get(currentKey);
        buffer.ensureSpace(vertices);
    }

    @Override
    public ArrayList<String> getDebugInfos() {
        ArrayList<String> infos = super.getDebugInfos();
        int noBuffers = buffers.size();
        infos.add( String.format("VBOBuffers: %d VBOSize: %d kB Waste: %d kB", noBuffers, (totalVBOBytes/1024), (totalVBOBytes-trianglesTotal*3*getStrideSize())/1024));
        return infos;
    }

}
