package de.funky_clan.coregl.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.HashMap;

/**
 * @author synopia
 */
public class StaticBufferedRenderer extends BaseBufferedRenderer {
    private HashMap<Object, Buffer> buffers = new HashMap<Object, Buffer>();
    private Object currentKey;

    public StaticBufferedRenderer(int size) {
        super(size);
    }

    public StaticBufferedRenderer(int size, int texCoordFormat, int colorFormat, int normalFormat) {
        super(size, texCoordFormat, colorFormat, normalFormat);
    }

    @Override
    public void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz) {
        Buffer buffer = buffers.get(currentKey);
        buffer.addVertex(x, y, z, tx, ty, color, nx, ny, nz);
    }

    @Override
    public boolean begin(Object key) {
        boolean result = false;
        if( !buffers.containsKey( key ) ) {
            IntBuffer vboIds = BufferUtils.createIntBuffer(1);
            GL15.glGenBuffers(vboIds);
            Buffer buffer = new Buffer( createBuffer(), vboIds.get(0));
            buffers.put(key, buffer);
            result = true;
        }
        currentKey = key;

        return result;
    }

    @Override
    public void onBufferFull() {
        Buffer buffer = buffers.get(currentKey);
        buffer.resize();
    }

    @Override
    public void render() {
        Buffer buffer = buffers.get(currentKey);
        buffer.render();
        trianglesTotal += buffer.getVertices()/3;
    }

    @Override
    public void end() {
        Buffer buffer = buffers.get(currentKey);
        buffer.upload();

    }

    @Override
    public void ensureSpace(int vertices) {
        Buffer buffer = buffers.get(currentKey);
        buffer.ensureSpace(vertices);
    }
}
