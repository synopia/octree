package de.funky_clan.chunks;

import de.funky_clan.octree.Morton;
import de.funky_clan.octree.WritableRaster;
import de.funky_clan.octree.data.OctreeNode;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * @author synopia
 */
public class Chunk implements WritableRaster {
    public  static final int ALLOCATED           = 1;
    public  static final int QUEUED              = 2;
    public  static final int NEIGHBORS_POPULATED = 3;
    public  static final int POPULATED           = 4;
    public  static final int POPULATED_SIDE_0    = 5;
    public  static final int POPULATED_SIDE_1    = 6;
    public  static final int POPULATED_SIDE_2    = 7;
    public  static final int POPULATED_SIDE_3    = 8;
    public  static final int POPULATED_SIDE_4    = 9;
    public  static final int POPULATED_SIDE_5    = 10;
    public  static final int POPULATED_SIDE_6    = 11;
    public  static final int DIRTY               = 12;
    private static final int MAX                 = 13;

    public static int[] COUNT = new int[64];
    public static final int SIZE = OctreeNode.CHUNK_SIZE;
    private ByteBuffer map;
    private Integer color;
    private boolean singleColored;

    private boolean[] state = new boolean[MAX];

    protected long morton;

    protected boolean visible;

    private int x;
    private int y;
    private int z;
    private int depth;

    public Chunk(int x, int y, int z, int depth) {
        COUNT[depth] ++;
        singleColored = false;
        this.x = x;
        this.y = y;
        this.z = z;
        this.depth = depth;
        morton = Morton.mortonCode(x>>OctreeNode.CHUNK_BITS, y>>OctreeNode.CHUNK_BITS, z>>OctreeNode.CHUNK_BITS, depth);
        visible = true;
    }

    public void allocate(ByteBuffer map) {
        this.map = map;
        state[ALLOCATED] = true;
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {
        if( x<0 || y<0 || z<0 || x>=SIZE || y>=SIZE || z>=SIZE ) {
            throw new IllegalArgumentException("Out of range "+x+", "+y+", "+z);
        }
        map.putShort(2*(x + ( y*SIZE+z ) * SIZE), (short) color);
        if( this.color==null ) {
            singleColored = true;
        } else {
            singleColored = singleColored && this.color==color;
        }
        this.color    = color;
    }

    @Override
    public int getPixel(int x, int y, int z) {
        if( singleColored ) {
            return color;
        }
        if( x<0 || y<0 || z<0 || x>=SIZE || y>=SIZE || z>=SIZE ) {
            throw new IllegalArgumentException("Out of range "+x+", "+y+", "+z);
        }
        return map.getShort(2*(x + ( y*SIZE+z ) * SIZE));
    }

    @Override
    protected void finalize() throws Throwable {
        COUNT[depth]--;
        super.finalize();
    }

    public void finishPopulation() {
        if( singleColored ) {
//            map = null;
        }
    }
    
    public ByteBuffer getMap() {
        return map;
    }

    public boolean isSingleColored() {
        return singleColored;
    }

    public boolean isPopulated() {
        return state[POPULATED];
    }

    public long getMorton() {
        return morton;
    }

    public boolean isNeighborsPopulated() {
        return state[NEIGHBORS_POPULATED];
    }

    public void setNeighborsPopulated(boolean neighborsPopulated) {
        state[NEIGHBORS_POPULATED] = neighborsPopulated;
    }
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
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

    public int getDepth() {
        return depth;
    }

    public void setPopulated(boolean populated) {
        if( populated ) {
            finishPopulation();
            state[POPULATED] = true;
        } else {
            state[POPULATED] = false;
            for (int i = POPULATED_SIDE_0; i <= POPULATED_SIDE_6; i++) {
                state[POPULATED_SIDE_0] = false;
            }
        }
    }

    public boolean isDirty() {
        return state[DIRTY];
    }

    public void setDirty(boolean dirty) {
        state[DIRTY] = dirty;
    }
    
    public int getSize() {
        return OctreeNode.CHUNK_SIZE<<depth;
    }

    public boolean isPartialyPopulated(int side) {
        return state[POPULATED_SIDE_0+side];
    }

    public void setPartialyPopulated(int side) {
        state[POPULATED_SIDE_0+side] = true;
    }

    public boolean isQueued() {
        return state[QUEUED];
    }

    public void setQueued() {
        state[QUEUED] = true;
    }

    public boolean isAllocated() {
        return state[ALLOCATED];
    }

    public void setAllocated(boolean allocated) {
        state[ALLOCATED] = allocated;
        if( !allocated ) {
            map = null;
        }
    }
}
