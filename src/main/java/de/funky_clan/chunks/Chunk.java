package de.funky_clan.chunks;

import com.google.inject.Inject;
import de.funky_clan.filesystem.BlockDevice;
import de.funky_clan.octree.Morton;
import de.funky_clan.octree.WritableRaster;
import de.funky_clan.octree.data.OctreeNode;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * @author synopia
 */
public class Chunk implements WritableRaster {
    public  static final int QUEUED              = 2;
    public  static final int NEIGHBORS_POPULATED = 3;
    public  static final int POPULATED           = 4;
    public  static final int POPULATED_SIDE      = 5;
    public  static final int DIRTY               = 6;
    public  static final int SINGLE_COLORED      = 7;
    private static final int MAX                 = 8;

    public static int[] COUNT = new int[64];
    public static final int SIZE = OctreeNode.CHUNK_SIZE;

    @Inject
    private BlockDevice blockDevice;

    private ByteBuffer map;
    private Integer color;

    private boolean[] state = new boolean[MAX];

    protected long morton;

    protected boolean visible;

    private int x;
    private int y;
    private int z;
    private int depth;

    public Chunk(int x, int y, int z, int depth) {
        COUNT[depth] ++;
        state[SINGLE_COLORED] = false;
        this.x = x<<OctreeNode.CHUNK_BITS;
        this.y = y<<OctreeNode.CHUNK_BITS;
        this.z = z<<OctreeNode.CHUNK_BITS;
        this.depth = depth;
        morton = Morton.mortonCode(x, y, z, depth);
        visible = true;
        map     = ByteBuffer.allocate(2*SIZE*SIZE*SIZE);

    }

    @Override
    public void setPixel(int x, int y, int z, int color) {
        if( x<0 || y<0 || z<0 || x>=SIZE || y>=SIZE || z>=SIZE ) {
            throw new IllegalArgumentException("Out of range "+x+", "+y+", "+z);
        }
        map.putShort(2*(x + ( y*SIZE+z ) * SIZE), (short) color);
        if( this.color==null ) {
            state[SINGLE_COLORED] = true;
        } else {
            state[SINGLE_COLORED] &= this.color==color;
        }
        this.color    = color;
    }

    @Override
    public int getPixel(int x, int y, int z) {
        if( isSingleColored() ) {
            return color;
        }
        if( x<0 || y<0 || z<0 || x>=SIZE || y>=SIZE || z>=SIZE ) {
            throw new IllegalArgumentException("Out of range "+x+", "+y+", "+z);
        }
        return map.getShort(2*(x + ( y*SIZE+z ) * SIZE));
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalize chunk "+this);
        COUNT[depth]--;
        super.finalize();
    }

    public void finishPopulation() {

        if( isSingleColored() ) {
//            map = null;
        } else {
        }
    }
    
    public ByteBuffer getMap() {
        return map;
    }

    public boolean isSingleColored() {
        return state[SINGLE_COLORED];
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
            state[POPULATED_SIDE] = false;
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

    public boolean isPartialyPopulated() {
        return state[POPULATED_SIDE];
    }

    public void setPartialyPopulated() {
        state[POPULATED_SIDE] = true;
    }

    public boolean isQueued() {
        return state[QUEUED];
    }

    public void setQueued() {
        state[QUEUED] = true;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", depth=" + depth +
                ", morton=" + morton +
                ", isPartiallyPopulated=" + isPartialyPopulated() +
                ", isPopulated=" + isPopulated() +
                ", isNeighborPopulated=" + isNeighborsPopulated() +
                '}';
    }
}
