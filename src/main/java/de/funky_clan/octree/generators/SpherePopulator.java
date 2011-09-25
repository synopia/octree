package de.funky_clan.octree.generators;

import de.funky_clan.voxel.ChunkPopulator;
import de.funky_clan.voxel.data.Chunk;

/**
 * @author synopia
 */
public class SpherePopulator implements ChunkPopulator {
    private int x;
    private int y;
    private int z;
    private int radius;
    private float radiusSq;

    public SpherePopulator(int x, int y, int z, int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        radiusSq = radius*radius;
    }

    @Override
    public void populateChunk(Chunk chunk) {
        int size = chunk.getSize();
        int midX = chunk.getX() + size/2;
        int midY = chunk.getY() + size/2;
        int midZ = chunk.getZ() + size/2;
        int minX = chunk.getX();
        int minY = chunk.getY();
        int minZ = chunk.getZ();
        int maxX = minX + size;
        int maxY = minY + size;
        int maxZ = minZ + size;

//        float dist1 = (midX-x)*(midX-x) + (midY-y)*(midY-y) + (midZ-z)*(midZ-z);
        float dist1 = (minX-x)*(minX-x) + (minY-y)*(minY-y) + (minZ-z)*(minZ-z);
        float dist2 = (maxX-x)*(maxX-x) + (maxY-y)*(maxY-y) + (maxZ-z)*(maxZ-z);

//        float minDist = Math.min( dist1, dist2 );
//        float maxDist = Math.max(dist1, dist2);
/*
        if( maxDist<=radiusSq ) {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        chunk.setPixel(x, y, z, 1);
                    }
                }
            }
        }
*/
        if( dist1<=radiusSq||dist2<=radiusSq ) {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        float dist = (x-this.x)*(x-this.x) + (y-this.y)*(y-this.y) + (z-this.z)*(z-this.z);

                        if( radiusSq>=dist ) {
                            chunk.setPixel(x, y, z, 1);
                        }
                    }
                }
            }
        }
    }
}
