package de.funky_clan.coregl.geom;

/**
 * @author synopia
 */
public final class Vertex {
    private float x;
    private float y;
    private float z;

    private float tx;
    private float ty;

    private int color;

    private float nx;
    private float ny;
    private float nz;

    public Vertex(float x, float y, float z, float tx, float ty, int color, float nx, float ny, float nz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tx = tx;
        this.ty = ty;
        this.color = color;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getTx() {
        return tx;
    }

    public float getTy() {
        return ty;
    }

    public int getColor() {
        return color;
    }

    public float getNx() {
        return nx;
    }

    public float getNy() {
        return ny;
    }

    public float getNz() {
        return nz;
    }
}
