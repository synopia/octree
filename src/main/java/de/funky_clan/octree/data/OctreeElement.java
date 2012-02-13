package de.funky_clan.octree.data;

import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.octree.WritableRaster;

/**
 * @author synopia
 */
public abstract class OctreeElement implements WritableRaster {
    protected int x;
    protected int y;
    protected int z;
    protected int depth;
    protected int size;

    protected Sphere boundingSphere;
    protected OctreeNode parent;
    protected Octree octree;

    public OctreeElement(Octree octree, int x, int y, int z, int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.depth = depth;
        this.size = 1<<(depth+OctreeNode.CHUNK_BITS);
        this.octree = octree;

        this.octree = octree;
        buildBoundingSphere();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getSize() {
        return size;
    }

    public boolean isLeaf() {
        return false;
    }

    public Sphere getBoundingSphere() {
        return boundingSphere;
    }

    public OctreeNode getParent() {
        return parent;
    }

    public void setParent(OctreeNode parent) {
        this.parent = parent;
    }

    public Octree getOctree() {
        return octree;
    }

    private void buildBoundingSphere() {
        float hsize = size / 2;
        boundingSphere = new Sphere(x+ hsize, y+ hsize, z+ hsize, (float) Math.sqrt(hsize*hsize*3));
    }
}
