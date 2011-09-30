package de.funky_clan.voxel.data;

import de.funky_clan.octree.WritableRaster;
import de.funky_clan.coregl.geom.Cube;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.voxel.Morton;
import org.lwjgl.util.vector.Vector3f;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author synopia
 */
public class OctreeNode implements WritableRaster {
    private static final int[][] OFFSETS = new int[][] {
        {0,0,0}, {1,0,0}, {0,1,0}, {1,1,0}, {0,0,1}, {1,0,1}, {0,1,1}, {1,1,1}
    };
    public static final int CHUNK_SIZE = 32;
    protected int x;
    protected int y;
    protected int z;
    protected int size;
    protected boolean visible;
    protected Reference<OctreeNode>[] children = new Reference[8];
    protected OctreeNode parent;
    protected Octree octree;

    private Sphere boundingSphere;

    public OctreeNode(Octree octree, int x, int y, int z, int size) {
        this.octree = octree;
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        visible = true;

        float hsize = size / 2;
        boundingSphere = new Sphere(x+ hsize, y+ hsize, z+ hsize, (float) Math.sqrt(hsize*hsize*3));
    }

    public Chunk getChunk( int x, int y, int z ) {
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        int code = 0;
        int newSize = size/2;
        if( relX>=newSize ) code |= 1;
        if( relY>=newSize ) code |= 2;
        if( relZ>=newSize ) code |= 4;

        if( newSize==CHUNK_SIZE ) {
            return (Chunk) getChild(code);
        }
        return getChild(code).getChunk(x, y, z);
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        int code = 0;
        int newSize = size/2;
        if( relX>=newSize ) code |= 1;
        if( relY>=newSize ) code |= 2;
        if( relZ>=newSize ) code |= 4;

        getChild(code).setPixel(x, y, z, color);
    }

    @Override
    public int getPixel( int x, int y, int z ) {
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        if( relX<0 || relY<0 || relZ<0 || relX>=size || relY>=size || relZ>=size ) {
            return octree.getPixel(x, y, z);
        }
        int code = 0;
        int newSize = size/2;
        if( relX>=newSize ) code |= 1;
        if( relY>=newSize ) code |= 2;
        if( relZ>=newSize ) code |= 4;

        return getChild(code).getPixel(x, y, z);
    }

    public OctreeNode getChild( int code ) {
        OctreeNode node;
        if( children[code]==null ) {
            node = createNode(code);
            children[code] = new WeakReference<OctreeNode>(node);
            return node;
        }

        node = children[code].get();
        if( node==null ) {
            node = createNode(code);
            children[code] = new WeakReference<OctreeNode>(node);
        }

        return node;
    }

    private OctreeNode createNode(int code) {
        OctreeNode node;
        int newSize = size/2;
        int newX = this.x + OFFSETS[code][0] * newSize;
        int newY = this.y + OFFSETS[code][1] * newSize;
        int newZ = this.z + OFFSETS[code][2] * newSize;

        if( newSize> CHUNK_SIZE) {
            node = new OctreeNode(octree, newX, newY, newZ, newSize);
            node.setParent(this);
            octree.add(node);
        } else {
            node = new Chunk(octree, newX, newY, newZ, newSize);
            node.setParent(this);
            octree.add(node);
        }
        return node;
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


    @Override
    public String toString() {
        return x+", "+y+", "+z+" size="+size;
    }

    public OctreeNode getParent() {
        return parent;
    }

    public void setParent(OctreeNode parent) {
        this.parent = parent;
    }

    public static long toMorton(long x, long y, long z) {
        return Morton.mortonCode(new long[]{x, y, z} )[0];
    }
    public long toMorton() {
        return Morton.mortonCode(new long[]{x, y, z} )[0];
    }
}
