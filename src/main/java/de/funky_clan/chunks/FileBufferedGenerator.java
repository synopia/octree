package de.funky_clan.chunks;

import com.google.inject.Inject;
import de.funky_clan.filesystem.BlockDevice;

/**
 * @author synopia
 */
public class FileBufferedGenerator implements Generator {
    @Inject
    private BlockDevice blockDevice;

    private Generator generator;

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void doPopulate(Chunk chunk) {
        if( !blockDevice.read(chunk) ) {
            generator.doPopulate(chunk);
            blockDevice.write(chunk);
        }
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk) {
        if( !chunk.isPopulated() && !chunk.isPartialyPopulated() ) {
            generator.doPopulateForNeighbor(chunk);
        }
    }
}
