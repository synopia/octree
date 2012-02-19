package de.funky_clan.chunks;

import com.google.inject.Inject;
import de.funky_clan.filesystem.FileStorage;

/**
 * @author synopia
 */
public class ChainedPopulator implements ChunkPopulator {
    @Inject
    private FileStorage fileStorage;
    @Inject
    private ChainedPopulator generator;

    @Override
    public void doPopulate(Chunk chunk) {
        fileStorage.allocate(chunk);

        if( !chunk.isPopulated() ) {
            generator.doPopulate(chunk);
            fileStorage.store(chunk);
        }
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk) {
        fileStorage.allocate(chunk);

        if( !chunk.isPopulated() && !chunk.isPartialyPopulated() ) {
            generator.doPopulateForNeighbor(chunk);
            fileStorage.store(chunk);
        }
    }
}
