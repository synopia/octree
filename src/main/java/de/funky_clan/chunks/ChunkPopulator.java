package de.funky_clan.chunks;


import de.funky_clan.chunks.Chunk;

/**
 * @author synopia
 */
public interface ChunkPopulator {
    public void populateChunk( Chunk chunk );
    public void releaseChunk( Chunk chunk );
}
