package de.funky_clan.coregl.renderer;

/**
 * @author synopia
 */
public class CubeRenderer {
    private final static float[][][][] FACES = new float[][][][] {
            // FRONT
            { { { 0, 0, 1}, { 1, 0, 1}, { 1, 1, 1}, { 0, 1, 1} }, { {0,0}, {1,0}, {1,1}, {0,1} }, { { 0, 0, 1} } },
            // BACK
            { { { 0, 0, 0}, { 0, 1, 0}, { 1, 1, 0}, { 1, 0, 0} }, { {1,0}, {1,1}, {0,1}, {0,0} }, { { 0, 0,-1} } },
            // TOP
            { { { 0, 1, 0}, { 0, 1, 1}, { 1, 1, 1}, { 1, 1, 0} }, { {0,1}, {0,0}, {1,0}, {1,1} }, { { 0, 1, 0} } },
            // BOTTOM
            { { { 0, 0, 0}, { 1, 0, 0}, { 1, 0, 1}, { 0, 0, 1} }, { {1,1}, {0,1}, {0,0}, {1,0} }, { { 0,-1, 0} } },
            // RIGHT
            { { { 1, 0, 0}, { 1, 1, 0}, { 1, 1, 1}, { 1, 0, 1} }, { {1,0}, {1,1}, {0,1}, {0,0} }, { { 1, 0, 0} } },
            // LEFT
            { { { 0, 0, 0}, { 0, 0, 1}, { 0, 1, 1}, { 0, 1, 0} }, { {0,0}, {1,0}, {1,1}, {0,1} }, { {-1, 0, 0} } },
    };
    private QuadRenderer quadRenderer;
    private BaseBufferedRenderer renderer;

    public CubeRenderer(BaseBufferedRenderer renderer) {
        this.renderer = renderer;
        quadRenderer = new QuadRenderer(renderer);
    }

    public void renderCube(float x, float y, float z, float size, int color) {
        renderer.setTranslation(x, y, z);
        renderer.setScale(size, size, size);
        for (float[][][] FACE : FACES) {
            quadRenderer.renderQuad(FACE[0], FACE[1], color, FACE[2][0]);
        }

    }
    public void renderCubeFace(float x, float y, float z, float tx, float ty, int color, int faceId) {
        renderer.setTextureCoords( tx, ty );
        renderer.setTranslation(x, y, z);
        quadRenderer.renderQuad(FACES[faceId][0], FACES[faceId][1], color, FACES[faceId][2][0]);
    }

}
