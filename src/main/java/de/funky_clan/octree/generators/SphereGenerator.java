package de.funky_clan.octree.generators;

import de.funky_clan.octree.WritableRaster;
import org.lwjgl.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author synopia
 */
public class SphereGenerator {
    private final Logger logger = LoggerFactory.getLogger(SphereGenerator.class);

    public void generate( WritableRaster raster, int mx, int my, int mz, int radius ) {
        logger.info("Start generating a sphere with radius {} cubes...", radius);
        int minX = mx - radius;
        int minY = my - radius;
        int minZ = mz - radius;
        int maxX = mx + radius;
        int maxY = my + radius;
        int maxZ = mz + radius;

        double radius2 = radius * radius;

        int totalCubes = 0;

        long start = Sys.getTime();
        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    double dist = (x-mx)*(x-mx) + (y-my)*(y-my) + (z-mz)*(z-mz);
                    if( dist< radius2) {
                        totalCubes ++;
                        raster.setPixel(x, y, z, 1);
                    }
                }
            }
        }
        float time = (Sys.getTime() - start) * 1000.f / Sys.getTimerResolution();
        logger.info("...done. {} cubes total, took {} ms", totalCubes, time);
    }
}
