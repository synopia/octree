package de.funky_clan.voxel.data;

import de.funky_clan.voxel.Morton;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author synopia
 */
public class OctreeNode extends OctreeElement {
    private static final int[][] OFFSETS = new int[][] {
        {0,0,0}, {1,0,0}, {0,1,0}, {1,1,0}, {0,0,1}, {1,0,1}, {0,1,1}, {1,1,1}
    };
    public static final int CHUNK_SIZE = 32;
    protected Reference<OctreeElement>[] children = new Reference[8];

    public OctreeNode(Octree octree, int x, int y, int z, int size) {
        super(octree, x, y, z, size);
    }
    
    public void removeChunk( int x, int y, int z ) {
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        int code = 0;
        int newSize = size/2;
        if( relX>=newSize ) code |= 1;
        if( relY>=newSize ) code |= 2;
        if( relZ>=newSize ) code |= 4;

        if( newSize==CHUNK_SIZE ) {
            children[code] = null;
        } else {
            ((OctreeNode)children[code].get()).removeChunk(x, y, z);
        }
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
        return ((OctreeNode) getChild(code)).getChunk(x, y, z);
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

    public OctreeElement getChild( int code ) {
        OctreeElement node;
        if( children[code]==null ) {
            node = createNode(code);
            children[code] = new WeakReference<OctreeElement>(node);
            return node;
        }

        node = children[code].get();
        if( node==null ) {
            node = createNode(code);
            children[code] = new WeakReference<OctreeElement>(node);
        }

        return node;
    }

    private OctreeElement createNode(int code) {
        OctreeElement node;
        int newSize = size/2;
        int newX = this.x + OFFSETS[code][0] * newSize;
        int newY = this.y + OFFSETS[code][1] * newSize;
        int newZ = this.z + OFFSETS[code][2] * newSize;

        node = octree.createNode(newX, newY, newZ, newSize);
        node.setParent(this);

        return node;
    }


    @Override
    public String toString() {
        return x+", "+y+", "+z+" size="+size;
    }

}
