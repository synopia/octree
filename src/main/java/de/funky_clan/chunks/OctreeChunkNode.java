package de.funky_clan.chunks;

import com.google.inject.Inject;
import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author synopia
 */
public class OctreeChunkNode extends OctreeNode {
    private Reference<Chunk> chunk;
    @Inject
    private ChunkStorage storage;

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
        if( chunk==null || chunk.get()==null) {
            chunk = new WeakReference<Chunk>(storage.getChunkForVoxel(x, y, z, depth ));
        }
        return chunk.get();
    }
}
