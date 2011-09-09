package de.funky_clan.coregl.renderer;

import de.funky_clan.coregl.geom.Vertex;

/**
 * @author synopia
 */
public interface VBORenderer {
    void begin(Object key);

    void setTranslation(float x, float y, float z);

    void addVertex(Vertex vertex);

    void addVertex(float[] position, float[] texCoord, int color, float[] normal);

    void addVertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz);

    void ensureSpace(int vertices);

    void flush();

    int getTrianglesTotal();

    int getStrideSize();

    void setScale(float x, float y, float z);

    void render();
}
