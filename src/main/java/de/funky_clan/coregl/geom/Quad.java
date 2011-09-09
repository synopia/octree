package de.funky_clan.coregl.geom;

/**
 * @author synopia
 */
public final class Quad {
    private Vertex a;
    private Vertex b;
    private Vertex c;
    private Vertex d;

    public Quad(Vertex a, Vertex b, Vertex c, Vertex d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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

    public Vertex getD() {
        return d;
    }
}
