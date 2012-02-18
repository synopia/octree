package de.funky_clan.chunks.generators;

import de.funky_clan.chunks.ChunkPopulator;
import de.funky_clan.chunks.ChunkStorage;
import de.funky_clan.chunks.NeigborPopulator;
import de.funky_clan.noise.Fractal;
import de.funky_clan.noise.FractalBrownianMotion;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.octree.data.OctreeNode;

/**
 * @author synopia
 */
public class NoisePopulator implements ChunkPopulator {
    public static final double SPREAD = 0.1;
    private int x;
    private int y;
    private int z;
    private int radius;
    private double radiusSq;
    private double innerRadiusSq;
    private double outerRadiusSq;
    private Fractal fractal;

    public NoisePopulator(int x, int y, int z, int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        radiusSq = radius*radius;
        innerRadiusSq = (radius-SPREAD*radius)*(radius-SPREAD*radius);
        outerRadiusSq = (radius+SPREAD*radius)*(radius+SPREAD*radius);

        fractal = new FractalBrownianMotion(3, 32, 0.9f, 2.0f);
    }

    @Override
    public void doPopulate(Chunk chunk) {
        int size = chunk.getSize();
        int minX = chunk.getX();
        int minY = chunk.getY();
        int minZ = chunk.getZ();

        double dists[] = new double[]{
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
        int inside = 0;

        for (int i = 0; i < dists.length; i++) {
            if( dists[i]<=innerRadiusSq ) {
                in ++;
            } else if( dists[i]>=outerRadiusSq ){
                out ++;
            } else {
                inside ++;
            }
        }
        if( inside>0 || (in>0 && out>0) ) {
            float scale = (float) size / OctreeNode.CHUNK_SIZE;
            for (int x = 0; x < OctreeNode.CHUNK_SIZE; x++) {
                for (int y = 0; y < OctreeNode.CHUNK_SIZE; y++) {
                    for (int z = 0; z < OctreeNode.CHUNK_SIZE; z++) {
                        populateBlock(chunk, minX, minY, minZ, scale, x, y, z);
                    }
                }
            }
        }
        chunk.setPopulated(true);
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk, int neighbor) {
        int size = chunk.getSize();
        int minX = chunk.getX();
        int minY = chunk.getY();
        int minZ = chunk.getZ();

        float scale = (float) size / OctreeNode.CHUNK_SIZE;
        for (int i = 0; i < OctreeNode.CHUNK_SIZE; i++) {
            for (int j = 0; j < OctreeNode.CHUNK_SIZE; j++) {
                int x,y,z;
                switch (neighbor) {
                    case 0: x = i; y=i; z=0; break;
                    case 1: x = i; y=i; z=OctreeNode.CHUNK_SIZE-1; break;
                    case 2: x = i; y=0; z=i; break;
                    case 3: x = i; y=OctreeNode.CHUNK_SIZE-1; z=i; break;
                    case 4: x = 0; y=i; z=i; break;
                    case 5: x = OctreeNode.CHUNK_SIZE-1; y=i; z=i; break;
                    default:
                        throw new IllegalStateException();
                }
                 populateBlock(chunk, minX, minY, minZ, scale, x, y, z);
            }
        }
    }

    private void populateBlock(Chunk chunk, int minX, int minY, int minZ, float scale, int x, int y, int z) {
        float rx = minX + (float) x * scale;
        float ry = minY + (float) y * scale;
        float rz = minZ + (float) z * scale;

        double distSq = dist( rx, ry, rz );
        float dist = (float) Math.sqrt(distSq);
        float nx = (this.x-rx)/dist;
        float ny = (this.y-ry)/dist;
        float nz = (this.z-rz)/dist;

        float noise = fractal.calculate(new float[]{nx, ny, nz}, 3);
        double totalRadius = radius+noise*SPREAD*radius;
        double sq = totalRadius*totalRadius;

        if( sq>=distSq ) {
            chunk.setPixel(x, y, z, 1);
        } else {
            chunk.setPixel(x, y, z, 0);
        }
    }

    private double dist(float x, float y, float z) {
        return (this.x -x)*(this.x -x) + (this.y -y)*(this.y -y) + (this.z -z)*(this.z -z);
    }
}
