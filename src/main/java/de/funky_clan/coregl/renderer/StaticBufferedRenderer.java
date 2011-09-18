package de.funky_clan.coregl.renderer;

import de.funky_clan.voxel.data.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author synopia
 */
public class StaticBufferedRenderer extends BaseBufferedRenderer {
    public static final int MAX_VBO_BUFFERS = 0x500;
    private HashMap<Object, VBO> buffers = new HashMap<Object, VBO>();
    private long totalVBOBytes = 0;
    private Object currentKey;
    private VBO currentBuffer;
    private List<Object> releaseList = new ArrayList<Object>();

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
        int bufferCount = buffers.size();
        if( bufferCount>=MAX_VBO_BUFFERS ) {
            VBO vbo;
            do {
                Object key = releaseList.remove( 0 );
                vbo = buffers.remove(key);
            } while (vbo==null && !buffers.isEmpty());

            if( vbo!=null ) {
                vbo.clear();
                return vbo;
            }
        }

        return super.createVBOBuffer();
    }

    @Override
    public void prepare() {
        super.prepare();
        totalVBOBytes = 0;
    }

    @Override
    public boolean begin(Object key) {
        boolean result = false;
        if( !buffers.containsKey( key ) ) {
            releaseList.remove(key);
            currentBuffer = createVBOBuffer();
            buffers.put(key, currentBuffer);
            result = true;
        } else {
            currentBuffer = buffers.get(currentKey);
        }
        currentKey    = key;

        return result;
    }

    @Override
    public void release(Object key) {
        if( !releaseList.contains(key) ) {
            releaseList.add(key);
        }
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
        infos.add( String.format("VBOBuffers: %d VBOSize: %d kB Waste: %d kB Free VBO: %d", noBuffers, (totalVBOBytes / 1024), (totalVBOBytes - trianglesTotal * 3 * getStrideSize()) / 1024, releaseList.size()));
        return infos;
    }

}
