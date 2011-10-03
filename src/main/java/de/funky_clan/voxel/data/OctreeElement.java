package de.funky_clan.voxel.data;

import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.octree.WritableRaster;

/**
 * @author synopia
 */
public abstract class OctreeElement implements WritableRaster {
    protected int x;
    protected int y;
    protected int z;
    protected int size;
    protected boolean visible;
    protected Sphere boundingSphere;
    protected OctreeNode parent;
    protected Octree octree;

    public OctreeElement(Octree octree, int x, int y, int z, int size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.octree = octree;

        visible = true;
        float hsize = size / 2;
        boundingSphere = new Sphere(x+ hsize, y+ hsize, z+ hsize, (float) Math.sqrt(hsize*hsize*3));
        this.octree = octree;
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

}