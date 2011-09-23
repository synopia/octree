package de.funky_clan.coregl.renderer;

import de.funky_clan.coregl.geom.Triangle;

/**
 * @author synopia
 */
public class TriangleRenderer {
    private BufferedRenderer renderer;

    public TriangleRenderer(BufferedRenderer renderer) {
        this.renderer = renderer;
    }

    public void renderTriangle(Triangle tri) {
        renderer.ensureSpace(3);
        renderer.addVertex(tri.getA());
        renderer.addVertex(tri.getB());
        renderer.addVertex(tri.getC());
    }

    public void renderTriangle(float[][] positions, float[][] texCoords, int color, float[] normal) {
        renderer.ensureSpace(3);
        renderer.addVertex(positions[0], texCoords[0], color, normal);
        renderer.addVertex(positions[1], texCoords[1], color, normal);
        renderer.addVertex(positions[2], texCoords[2], color, normal);
    }
}
