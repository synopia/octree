package de.funky_clan.coregl.geom;

/**
 * @author synopia
 */
public final class Triangle {
    private Vertex a;
    private Vertex b;
    private Vertex c;

    public Triangle(Vertex a, Vertex b, Vertex c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Vertex getA() {
        return a;
    }

    public Vertex getB() {
        return b;
    }

    public Vertex getC() {
        return c;
    }
}
