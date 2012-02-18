package de.funky_clan.chunks;


import de.funky_clan.chunks.Chunk;

/**
 * @author synopia
 */
public interface ChunkPopulator {
    void doPopulate( Chunk chunk );
    void doPopulateForNeighbor( Chunk chunk, int neighbor );
}
