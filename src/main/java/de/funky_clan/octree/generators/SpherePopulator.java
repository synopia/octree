package de.funky_clan.octree.generators;

import de.funky_clan.voxel.AbstractPopulator;
import de.funky_clan.voxel.ChunkPopulator;
import de.funky_clan.voxel.data.Chunk;

/**
 * @author synopia
 */
public class SpherePopulator extends AbstractPopulator {
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
    protected void doPopulate(Chunk chunk) {
        int size = chunk.getSize();
        int minX = chunk.getX();
        int minY = chunk.getY();
        int minZ = chunk.getZ();
        int maxX = minX + size;
        int maxY = minY + size;
        int maxZ = minZ + size;
        
        float dists[] = new float[]{
                dist(minX, minY, minZ),
                dist(minX, minY, minZ+size),
                dist(minX, minY+size, minZ),
                dist(minX, minY+size, minZ+size),
                dist(minX+size, minY, minZ),
                dist(minX+size, minY, minZ+size),
                dist(minX+size, minY+size, minZ),
                dist(minX+size, minY+size, minZ+size)
        };
        int in = 0;
        int out= 0;
        for (int i = 0; i < dists.length; i++) {
            if( dists[i]<=radiusSq ) {
                in ++;
            } else {
                out ++;
            }            
        }
        if( in>0 && out>0 ) {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        float dist = dist( x, y, z );

                        if( radiusSq>=dist ) {
                            chunk.setPixel(x, y, z, 1);
                        }
                    }
                }
            }
        }
        chunk.setPopulated(true);
    }

    private float dist(int x, int y, int z) {
        return (this.x -x)*(this.x -x) + (this.y -y)*(this.y -y) + (this.z -z)*(this.z -z);
    }
}
