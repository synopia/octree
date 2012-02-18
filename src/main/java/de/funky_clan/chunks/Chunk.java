package de.funky_clan.chunks;

import de.funky_clan.octree.Morton;
import de.funky_clan.octree.WritableRaster;
import de.funky_clan.octree.data.OctreeNode;

import java.util.Arrays;

/**
 * @author synopia
 */
public class Chunk implements WritableRaster {
    public static int[] COUNT = new int[64];
    public static final int SIZE = OctreeNode.CHUNK_SIZE;
    private int[] map;
    protected Integer color;
    protected boolean singleColored;
    protected boolean partialyPopulated;
    protected boolean populated;
    protected boolean neighborsPopulated;
    protected boolean dirty;
    protected boolean queued;
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

    @Override
    public void setPixel(int x, int y, int z, int color) {
        if( x<0 || y<0 || z<0 || x>=SIZE || y>=SIZE || z>=SIZE ) {
            throw new IllegalArgumentException("Out of range "+x+", "+y+", "+z);
        }
        if( map==null ) {
            map = new int[SIZE*SIZE*SIZE];
            if( singleColored && this.color!=null ) {
                Arrays.fill(map, this.color);
            }
        }
        map[x + ( y*SIZE+z ) * SIZE] = color;
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
        if( map==null ) {
            return 0;
        }
        return map[x + ( y*SIZE+z ) * SIZE];
    }

    @Override
    protected void finalize() throws Throwable {
        COUNT[depth]--;
        super.finalize();
    }

    public void finishPopulation() {
        if( singleColored ) {
            map = null;
        }
    }
    
    public int[] getMap() {
        return map;
    }

    public boolean isSingleColored() {
        return singleColored;
    }

    public boolean isPopulated() {
        return populated;
    }

    public long getMorton() {
        return morton;
    }

    public boolean isNeighborsPopulated() {
        return neighborsPopulated;
    }

    public void setNeighborsPopulated(boolean neighborsPopulated) {
        this.neighborsPopulated = neighborsPopulated;
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
        finishPopulation();
        this.populated = populated;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public int getSize() {
        return OctreeNode.CHUNK_SIZE<<depth;
    }

    public boolean isPartialyPopulated() {
        return partialyPopulated;
    }

    public void setPartialyPopulated(boolean partialyPopulated) {
        this.partialyPopulated = partialyPopulated;
    }

    public boolean isQueued() {
        return queued;
    }

    public void setQueued(boolean queued) {
        this.queued = queued;
    }
}
