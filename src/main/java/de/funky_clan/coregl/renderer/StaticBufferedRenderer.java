package de.funky_clan.coregl.renderer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author synopia
 */
public class StaticBufferedRenderer extends BaseBufferedRenderer {
    private HashMap<Object, VBOBuffer> buffers = new HashMap<Object, VBOBuffer>();
    private long totalVBOBytes = 0;
    private Object currentKey;
    private VBOBuffer currentBuffer;

    public StaticBufferedRenderer(int size) {
        super(size);
    }

    public StaticBufferedRenderer(int size, int texCoordFormat, int colorFormat, int normalFormat) {
        super(size, texCoordFormat, colorFormat, normalFormat);
    }

    @Override
    protected VBOBuffer getCurrentBuffer() {
        return currentBuffer;
    }

    @Override
    public void prepare() {
        super.prepare();
        totalVBOBytes = 0;
    }

    @Override
    public boolean begin(Object key, boolean force) {
        boolean result = false;
        if( !buffers.containsKey( key ) ) {
            currentBuffer = createVBOBuffer();
            buffers.put(key, currentBuffer);
            result = true;
        } else {
            currentBuffer = buffers.get(currentKey);
        }
        currentKey    = key;
        currentBuffer = buffers.get(currentKey);

        if( force ) {
            currentBuffer.clear();
        }
        return result;
    }

    @Override
    public void onBufferFull() {
        currentBuffer.resize();
    }

    @Override
    public void render() {
        currentBuffer.render();
        trianglesTotal += currentBuffer.getVertices()/3;
        totalVBOBytes += currentBuffer.getByteBuffer().capacity();
    }

    @Override
    public void end() {
        currentBuffer.upload();
    }

    @Override
    public ArrayList<String> getDebugInfos() {
        ArrayList<String> infos = super.getDebugInfos();
        int noBuffers = buffers.size();
        infos.add( String.format("VBOBuffers: %d VBOSize: %d kB Waste: %d kB", noBuffers, (totalVBOBytes/1024), (totalVBOBytes-trianglesTotal*3*getStrideSize())/1024));
        return infos;
    }

}
