package de.funky_clan.octree.data;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author synopia
 */
public class OctreeNode extends OctreeElement {
    private static final int[][] OFFSETS = new int[][] {
            {0,0,0}, {1,0,0}, {0,1,0}, {1,1,0}, {0,0,1}, {1,0,1}, {0,1,1}, {1,1,1}
    };
    public static final int CHUNK_BITS = 5;
    public static final int CHUNK_SIZE = 1<<CHUNK_BITS;
    @SuppressWarnings("unchecked")
    protected Reference<OctreeNode>[] children = new Reference[8];

    private int getCode( int x, int y, int z ) {
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        int code = 0;
        if( !isLeaf() ) {
            int newSize = size/2;
            if( relX>=newSize ) code |= 1;
            if( relY>=newSize ) code |= 2;
            if( relZ>=newSize ) code |= 4;
            return code;
        } else {
            return -1;
        }        
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {
        if( !isLeaf() ) {
            int code = getCode(x, y, z);
            getChild(code).setPixel(x, y, z, color);
        }
    }

    @Override
    public int getPixel( int x, int y, int z ) {
        if( !isLeaf() ) {
            int code = getCode(x, y, z);
            return getChild(code).getPixel(x, y, z);
        }
        return 0;
    }

    public OctreeElement getChild( int code) {
        return getChild(code, true);
    }

    public OctreeElement getChild( int code, boolean create) {
        if( create && (children[code]==null || children[code].get()==null) ) {
            OctreeNode node = createNode(code);
            children[code] = new WeakReference<OctreeNode>(node);
            return node;
        }

        return children[code]!=null ? children[code].get() : null;
    }

    private OctreeNode createNode(int code) {
        OctreeNode node;
        int newSize = size/2;
        int newX = this.x + OFFSETS[code][0] * newSize;
        int newY = this.y + OFFSETS[code][1] * newSize;
        int newZ = this.z + OFFSETS[code][2] * newSize;
        int newDepth = depth-1;

        node = octree.createNode(newX, newY, newZ, newDepth);
        node.setParent(this);
        return node;
    }

    @Override
    public String toString() {
        return x+", "+y+", "+z+" size="+size;
    }

    @Override
    public boolean isLeaf() {
        return depth==0;
    }
}
