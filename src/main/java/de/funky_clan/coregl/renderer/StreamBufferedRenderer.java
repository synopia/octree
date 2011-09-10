package de.funky_clan.coregl.renderer;

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

    @Override
    protected VBOBuffer getCurrentBuffer() {
        return buffers.get(bufferIndex);
    }

    @Override
    public boolean begin(Object key, boolean force) {
        if( buffers.size()==0 ) {
            for (int i = 0; i < NUMBER_OF_BUFFERS; i++) {
                buffers.add( createVBOBuffer() );
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
    public ArrayList<String> getDebugInfos() {
        ArrayList<String> infos = super.getDebugInfos();
        int noBuffers = buffers.size();
        infos.add(String.format("VBOBuffers: %d", noBuffers));
        return infos;
    }
}
