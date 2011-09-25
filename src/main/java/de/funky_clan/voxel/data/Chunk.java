package de.funky_clan.voxel.data;

/**
 * @author synopia
 */
public class Chunk extends OctreeNode {
    public static int COUNT = 0;
    private int[] map;
    private boolean dirty;

    public Chunk(Octree octree, int x, int y, int z, int size) {
        super(octree, x, y, z, size);


        visible = true;
        dirty   = true;
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

    @Override
    protected void finalize() throws Throwable {
        COUNT++;
        super.finalize();
    }
}
