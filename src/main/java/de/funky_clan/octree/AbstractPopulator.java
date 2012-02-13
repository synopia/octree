package de.funky_clan.octree;

import de.funky_clan.octree.data.Chunk;
import de.funky_clan.octree.data.Octree;

/**
 * @author synopia
 */
public abstract class AbstractPopulator implements ChunkPopulator {
    protected static int[][] NEIGHBORS = new int[][]{
                {0,0,32}, {0,0,-32}, {0,32,0}, {0,-32,0}, {32,0,0}, {-32,0,0},
        };

    protected Octree octree;

    protected AbstractPopulator(Octree octree) {
        this.octree = octree;
    }

    @Override
    public void populateChunk(Chunk chunk) {
        if( chunk.isNeighborsPopulated() ) {
            return;
        }

        if( !chunk.isPopulated() ) {
            doPopulate(chunk);
        }

        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
        for (int[] neighborCoords : NEIGHBORS) {
            Chunk neighbor = octree.getChunkForVoxel(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2], chunk.getDepth());
            if( neighbor!=null ) {
                if( !neighbor.isPopulated() ) {
                    doPopulate(neighbor);
                }
            }
        }
        chunk.setNeighborsPopulated(true);
    }

    @Override
    public void releaseChunk(Chunk chunk) {
        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
        for (int[] neighborCoords : NEIGHBORS) {
            Chunk neighbor = octree.getChunkForVoxel(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2], chunk.getDepth());
            if( neighbor!=null ) {
                neighbor.setNeighborsPopulated(false);
            }
        }
        chunk.setPopulated(false);
        chunk.setNeighborsPopulated(false);
    }

    protected abstract void doPopulate( Chunk chunk );


}
