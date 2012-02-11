package de.funky_clan.octree;


import de.funky_clan.octree.data.Chunk;

/**
 * @author synopia
 */
public interface ChunkPopulator {
    public void populateChunk( Chunk chunk );
    public void releaseChunk( Chunk chunk );
}
