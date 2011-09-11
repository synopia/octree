package de.funky_clan.coregl.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author synopia
 */
public class StaticBufferedRenderer extends BaseBufferedRenderer {
    private static class Entry {
        private VBO buffer;
        private int count;
    }
    private HashMap<Object, Entry> buffers = new HashMap<Object, Entry>();
    private long totalVBOBytes = 0;
    private Object currentKey;
    private VBO currentBuffer;
    private List<VBO> freeList = new ArrayList<VBO>();

    public StaticBufferedRenderer(int size) {
        super(size);
    }

    public StaticBufferedRenderer(int size, int texCoordFormat, int colorFormat, int normalFormat) {
        super(size, texCoordFormat, colorFormat, normalFormat);
    }

    @Override
    protected VBO getCurrentBuffer() {
        return currentBuffer;
    }

    @Override
    protected VBO createVBOBuffer() {
        if( !freeList.isEmpty() ) {
            return freeList.remove(0);
        } else {
            return super.createVBOBuffer();
        }
    }

    @Override
    public void prepare() {
        super.prepare();
        totalVBOBytes = 0;
        Set<Object> keys = new HashSet<Object>(buffers.keySet());

        for (Object key : keys) {
            Entry entry = buffers.get(key);
            if( entry.count<=0 ) {
                buffers.remove(key);
                freeList.add(entry.buffer);
                entry.buffer.free();
            } else {
                entry.count --;
            }
        }
    }

    @Override
    public boolean begin(Object key) {
        boolean result = false;
        if( !buffers.containsKey( key ) ) {
            Entry entry = new Entry();
            currentBuffer = createVBOBuffer();
            entry.buffer = currentBuffer;
            entry.count  = 100;
            buffers.put(key, entry);
            result = true;
        } else {
            Entry entry = buffers.get(currentKey);
            entry.count = 100;
            currentBuffer = entry.buffer;
        }
        currentKey    = key;

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
    public void clear() {
        currentBuffer.clear();
    }

    @Override
    public ArrayList<String> getDebugInfos() {
        ArrayList<String> infos = super.getDebugInfos();
        int noBuffers = buffers.size();
        infos.add( String.format("VBOBuffers: %d VBOSize: %d kB Waste: %d kB Free VBO: %d", noBuffers, (totalVBOBytes / 1024), (totalVBOBytes - trianglesTotal * 3 * getStrideSize()) / 1024, freeList.size()));
        return infos;
    }

}
