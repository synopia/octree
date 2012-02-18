package de.funky_clan.chunks;

import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;

/**
 * @author synopia
 */
public class ChunkOctree extends Octree {
    private ChunkStorage storage;
    public ChunkOctree(ChunkStorage storage, int x, int y, int z, int depth) {
        super(x, y, z, depth);
        this.storage = storage;
    }

    @Override
    public OctreeNode createNode(int x, int y, int z, int depth) {
        OctreeNode node;

        node = new OctreeChunkNode(this, storage, x, y, z, depth);
        return node;

    }
}
