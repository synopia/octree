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
    public void doPopulateForNeighbor(Chunk chunk) {
        first.doPopulateForNeighbor(chunk);
        if( ! (chunk.isPartialyPopulated() || chunk.isPopulated()) ) {
            second.doPopulateForNeighbor(chunk);
        }
    }
}
