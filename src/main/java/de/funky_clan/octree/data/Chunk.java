package de.funky_clan.octree.data;

import de.funky_clan.octree.Morton;

/**
 * @author synopia
 */
public class Chunk extends OctreeElement {
    public static int COUNT = 0;
    private int[] map;
    private boolean dirty;
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

        if( this.color==null ) {
            singleColored = true;
        } else {
            singleColored = singleColored && this.color==color;
        }
        this.color    = color;
    }

    @Override
    public int getPixel(int x, int y, int z) {
        if( populated && singleColored ) {
            return color;
        }
        int relX = x-this.x;
        int relY = y-this.y;
        int relZ = z-this.z;
        if( relX<0 || relY<0 || relZ<0 || relX>=size || relY>=size || relZ>=size ) {
            return parent.getPixel(x, y, z);
        }
        if( map==null ) {
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

    public void setPopulated(boolean populated) {
        this.populated = populated;
        if( populated ) {
            setPopulated();
        }
    }

    @Override
    public void setPopulated() {
        this.populated = true;
        if( singleColored ) {
            map = null;
        }
        if( parent!=null ) {
            parent.setPopulated();
        }
    }

    public boolean isFullyPopulated() {
        return populated && fullyPopulated;
    }

    public void setFullyPopulated(boolean fullyPopulated) {
        this.fullyPopulated = fullyPopulated;
    }

    @Override
    protected void finalize() throws Throwable {
        COUNT--;
        super.finalize();
    }

    public int[] getMap() {
        return map;
    }

    @Override
    public String toString() {
        return x+", "+y+", "+z;
    }

    public static long toMorton(int x, int y, int z) {
        return Morton.mortonCode(x>>4, y>>4, z>>4);
    }
    public long toMorton() {
        return Morton.mortonCode(x >> 4, y >> 4, z >> 4);
    }

}
