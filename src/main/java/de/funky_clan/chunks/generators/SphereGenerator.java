package de.funky_clan.chunks.generators;

import de.funky_clan.chunks.Generator;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.octree.data.OctreeNode;

/**
 * @author synopia
 */
public class SphereGenerator implements Generator {
    private int x;
    private int y;
    private int z;
    private int radius;
    private float radiusSq;

    public SphereGenerator(int x, int y, int z, int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        radiusSq = radius*radius;
    }

    @Override
    public void doPopulate(Chunk chunk) {
        if( chunk.isPopulated() ) {
            return;
        }
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
        if( in>0  ) {
            float scale = (float) size / OctreeNode.CHUNK_SIZE;
            for (int x = 0; x < OctreeNode.CHUNK_SIZE; x++) {
                for (int y = 0; y < OctreeNode.CHUNK_SIZE; y++) {
                    for (int z = 0; z < OctreeNode.CHUNK_SIZE; z++) {
                        float rx = minX + (float) x * scale;
                        float ry = minY + (float) y * scale;
                        float rz = minZ + (float) z * scale;
                        float dist = dist( rx, ry, rz );

                        if( radiusSq>=dist ) {
                            chunk.setPixel(x, y, z, 1);
                        } else {
                            chunk.setPixel(x, y, z, 0);
                        }
                    }
                }
            }
        }
        chunk.setPopulated(true);
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk) {
        doPopulate(chunk);
    }

    private float dist(float x, float y, float z) {
        return (this.x -x)*(this.x -x) + (this.y -y)*(this.y -y) + (this.z -z)*(this.z -z);
    }
}
