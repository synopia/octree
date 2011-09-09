package de.funky_clan.voxel.data;

import de.funky_clan.octree.WritableRaster;
import de.funky_clan.coregl.geom.Cube;
import de.funky_clan.coregl.geom.Sphere;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public class OctreeNode implements WritableRaster {
    private static final int[][] OFFSETS = new int[][] {
        {0,0,0}, {1,0,0}, {0,1,0}, {1,1,0}, {0,0,1}, {1,0,1}, {0,1,1}, {1,1,1}
    };
    protected int x;
    protected int y;
    protected int z;
    protected int size;
    protected boolean visible;
    private OctreeNode[] children = new OctreeNode[8];

    private Sphere boundingSphere;
    private Cube   boundingBox;

    public OctreeNode(int x, int y, int z, int size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        visible = true;

        float hsize = size / 2;
        boundingSphere = new Sphere(new Vector3f(x+ hsize, y+ hsize, z+ hsize), (float) Math.sqrt(hsize*hsize*3));
        boundingBox    = new Cube(new Vector3f(x+ hsize, y+ hsize, z+ hsize), hsize);
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

        if( newSize>16 ) {
            if( children[code]==null ) {
                children[code] = new OctreeNode( this.x+OFFSETS[code][0]*newSize, this.y+OFFSETS[code][1]*newSize, this.z+OFFSETS[code][2]*newSize, newSize);
            }
        } else {
            if( children[code]==null ) {
                children[code] = new Chunk(this.x+OFFSETS[code][0]*newSize, this.y+OFFSETS[code][1]*newSize, this.z+OFFSETS[code][2]*newSize, newSize);
            }
        }
        children[code].setPixel(x, y, z, color);
    }

    @Override
    public int getPixel( int x, int y, int z ) {
        int color = 0;
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        int code = 0;
        int newSize = size/2;
        if( relX>=newSize ) code |= 1;
        if( relY>=newSize ) code |= 2;
        if( relZ>=newSize ) code |= 4;

        if( children[code]!=null ) {
            color = children[code].getPixel(x, y, z);
        }

        return color;
    }

    public OctreeNode[] getChildren() {
        return children;
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

    public Cube getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return x+", "+y+", "+z+" size="+size;
    }
}
