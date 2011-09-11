package de.funky_clan.coregl.renderer;

import java.nio.ByteBuffer;

/**
 * @author synopia
 */
public interface VBO {
    ByteBuffer getByteBuffer();

    int getVboId();

    int getVertices();

    void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz);

    void ensureSpace(int vertices);

    void upload();

    void render();

    void clear();

    void resize();

    void free();
}
