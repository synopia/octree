package de.funky_clan.voxel.data;

import de.funky_clan.voxel.Morton;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author synopia
 */
public class Chunk extends OctreeElement {
    public static int COUNT = 0;
    private int[] map;
    private boolean dirty;
    private boolean populated;
    private boolean fullyPopulated;

    public Chunk(Octree octree, int x, int y, int z, int size) {
        super(octree, x, y, z, size);
        dirty   = true;
        COUNT ++;
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {
        if( map==null ) {
            map = new int[size*size*size];
        }
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        map[relX + ( relY*size+relZ ) * size] = color;
        visible = true;
        dirty   = true;
    }

    @Override
    public int getPixel(int x, int y, int z) {
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        if( relX<0 || relY<0 || relZ<0 || relX>=size || relY>=size || relZ>=size ) {
            return parent.getPixel(x, y, z);
        }
        if( map==null ) {
            System.out.println("map is null");
            return 0;
        }
        return map[relX + ( relY*size+relZ ) * size];
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    public boolean isFullyPopulated() {
        return populated && fullyPopulated;
    }

    public void setFullyPopulated(boolean fullyPopulated) {
        this.fullyPopulated = fullyPopulated;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize chunk "+this);
        COUNT--;
        octree.remove(this);
        super.finalize();
    }

    public int[] getMap() {
        return map;
    }

    @Override
    public String toString() {
        return x+", "+y+", "+z;
    }

    public static long toMorton(long x, long y, long z) {
        return Morton.mortonCode(x>>4, y>>4, z>>4);
    }
    public long toMorton() {
        return Morton.mortonCode(x>>4, y>>4, z>>4 );
    }

}
