package de.funky_clan.octree.generators;

import de.funky_clan.octree.data.Chunk;
import de.funky_clan.octree.data.Octree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author synopia
 */
public class SphereGenerator {
    private final Logger logger = LoggerFactory.getLogger(SphereGenerator.class);
    private SpherePopulator populator;

    public void generate( Octree octree, int mx, int my, int mz, int radius ) {
        logger.info("Start generating a sphere with radius {} cubes...", radius);
        populator = new SpherePopulator(octree, mx, my, mz, radius);

        int minX = (mx - radius)>>5;
        int minY = (my - radius)>>5;
        int minZ = (mz - radius)>>5;
        int maxX = (mx + radius)>>5;
        int maxY = (my + radius)>>5;
        int maxZ = (mz + radius)>>5;

        int totalCubes = 0;

        long start = System.nanoTime();
        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Chunk chunk = octree.getChunkForVoxel(x << 5, y << 5, z << 5, 0);
                    populator.doPopulate(chunk);
                }
            }
        }
        float time = (System.nanoTime() - start) / 1000000.f;
        logger.info("...done. {} cubes total, took {} ms", totalCubes, time);
    }
}
