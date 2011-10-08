package de.funky_clan.voxel;

import de.funky_clan.voxel.data.Chunk;

/**
 * @author synopia
 */
public interface ChunkPopulator {
    public void populateChunk( Chunk chunk );
    public void releaseChunk( Chunk chunk );
}
