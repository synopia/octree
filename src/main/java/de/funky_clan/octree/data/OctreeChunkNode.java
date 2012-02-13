package de.funky_clan.octree.data;

/**
 * @author synopia
 */
public class OctreeChunkNode extends OctreeNode {
    private Chunk chunk;
    
    public OctreeChunkNode(Octree octree, int x, int y, int z, int depth) {
        super(octree, x, y, z, depth);
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {        
        if( isLeaf() ) {
            getChunk().setPixel(x - this.x, y - this.y, z - this.z, color);
        } else {
            super.setPixel(x, y, z, color);
        }
    }

    @Override
    public int getPixel(int x, int y, int z) {
        if( isLeaf() ) {
            return getChunk().getPixel(x - this.x, y - this.y, z - this.z);
        } else {
            return super.getPixel(x, y, z);
        }
    }

    public Chunk getChunk() {
        if( chunk==null ) {
            if( isLeaf() ) {
                chunk = octree.getChunkForVoxel(x, y, z, depth );
            } else {
                chunk = octree.getChunkForVoxel(x, y, z, depth );
            }
            octree.add(chunk);
        }
        return chunk;
    }
}
