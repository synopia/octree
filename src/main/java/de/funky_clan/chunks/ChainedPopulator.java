package de.funky_clan.chunks;

/**
 * @author synopia
 */
public class ChainedPopulator implements ChunkPopulator {
    private ChunkPopulator first;
    private ChunkPopulator second;

    public ChainedPopulator(ChunkPopulator first, ChunkPopulator second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public void doPopulate(Chunk chunk) {
        first.doPopulate(chunk);
        if( !chunk.isPopulated() ) {
            second.doPopulate(chunk);
        }
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk, int neighbor) {
        first.doPopulateForNeighbor(chunk, neighbor);
        if( !chunk.isPartialyPopulated(neighbor) &&  !chunk.isPopulated() ) {
            second.doPopulateForNeighbor(chunk, neighbor);
        }
    }
}
