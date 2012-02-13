package de.funky_clan.octree.generators;

import de.funky_clan.noise.Fractal;
import de.funky_clan.noise.FractalBrownianMotion;
import de.funky_clan.octree.AbstractPopulator;
import de.funky_clan.octree.data.Chunk;
import de.funky_clan.octree.data.Octree;

/**
 * @author synopia
 */
public class NoisePopulator extends AbstractPopulator {
    public static final double SPREAD = 0.1;
    private int x;
    private int y;
    private int z;
    private int radius;
    private float radiusSq;
    private Fractal fractal;

    public NoisePopulator(Octree octree, int x, int y, int z, int radius) {
        super(octree);
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        radiusSq = radius*radius;

        fractal = new FractalBrownianMotion(3, 32, 0.9f, 2.0f);
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
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    float distSq = (float) dist( x, y, z );
                    float dist = (float) Math.sqrt(distSq);
                    float rx = (this.x-x)/dist;
                    float ry = (this.y-y)/dist;
                    float rz = (this.z-z)/dist;

                    float noise = fractal.calculate(new float[]{rx, ry, rz}, 3);
                    double totalRadius = radius+noise*SPREAD*radius;
                    double sq = totalRadius*totalRadius;

                    if( sq>=distSq ) {
                        chunk.setPixel(x, y, z, 1);
                    } else {
                        chunk.setPixel(x, y, z, 0);
                    }
                }
            }
        }
        chunk.setPopulated(true);

    }

    private double dist(int x, int y, int z) {
        return (this.x -x)*(this.x -x) + (this.y -y)*(this.y -y) + (this.z -z)*(this.z -z);
    }
}
