package de.funky_clan.coregl.renderer;

import de.funky_clan.coregl.geom.Quad;

/**
 * @author synopia
 */
public class QuadRenderer {
    private BaseBufferedRenderer renderer;

    public QuadRenderer(BaseBufferedRenderer renderer) {
        this.renderer = renderer;
    }

    public void renderQuad(Quad quad) {
        renderer.ensureSpace(6);
        renderer.addVertex(quad.getA());
        renderer.addVertex(quad.getB());
        renderer.addVertex(quad.getC());
        renderer.addVertex(quad.getA());
        renderer.addVertex(quad.getC());
        renderer.addVertex(quad.getD());
    }

    public void renderQuad(float[][] positions, float[][] texCoords, int color, float[] normal) {
        renderer.ensureSpace(6);
        renderer.addVertex(positions[0], texCoords[0], color, normal);
        renderer.addVertex(positions[1], texCoords[1], color, normal);
        renderer.addVertex(positions[2], texCoords[2], color, normal);
        renderer.addVertex(positions[0], texCoords[0], color, normal);
        renderer.addVertex(positions[2], texCoords[2], color, normal);
        renderer.addVertex(positions[3], texCoords[3], color, normal);
    }
}
