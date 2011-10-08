package de.funky_clan.voxel;

import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.Octree;

/**
 * @author synopia
 */
public abstract class AbstractPopulator implements ChunkPopulator {
    protected static int[][] NEIGHBORS = new int[][]{
                {0,0,32}, {0,0,-32}, {0,32,0}, {0,-32,0}, {32,0,0}, {-32,0,0},
        };

    @Override
    public void populateChunk(Chunk chunk) {
        if( chunk.isFullyPopulated() ) {
            return;
        }
        Octree octree = chunk.getOctree();

        if( !chunk.isPopulated() ) {
            doPopulate(chunk);
        }

        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
        for (int[] neighborCoords : NEIGHBORS) {
            Chunk neighbor = octree.getChunk(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2]);
            if( neighbor!=null ) {
                if( !neighbor.isPopulated() ) {
                    doPopulate(neighbor);
                }
            }
        }
        chunk.setFullyPopulated(true);
    }

    @Override
    public void releaseChunk(Chunk chunk) {
        Octree octree = chunk.getOctree();
        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
        for (int[] neighborCoords : NEIGHBORS) {
            Chunk neighbor = octree.getChunk(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2]);
            if( neighbor!=null ) {
                neighbor.setFullyPopulated(false);
            }
        }
        chunk.setPopulated(false);
        chunk.setFullyPopulated(false);
    }

    protected abstract void doPopulate( Chunk chunk );


}
