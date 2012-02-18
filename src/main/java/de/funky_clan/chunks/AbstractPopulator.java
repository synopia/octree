package de.funky_clan.chunks;

import de.funky_clan.octree.data.Octree;

/**
 * @author synopia
 */
public abstract class AbstractPopulator implements ChunkPopulator {
    protected static int[][] NEIGHBORS = new int[][]{
                {0,0,32}, {0,0,-32}, {0,32,0}, {0,-32,0}, {32,0,0}, {-32,0,0},
        };

    protected ChunkStorage chunkStorage;

    protected AbstractPopulator(ChunkStorage chunkStorage) {
        this.chunkStorage = chunkStorage;
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
        for (int i = 0, neighborsLength = NEIGHBORS.length; i < neighborsLength; i++) {
            int[] neighborCoords = NEIGHBORS[i];
            Chunk neighbor = chunkStorage.getChunkForVoxel(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2], chunk.getDepth());
            if (neighbor != null) {
                if (! (neighbor.isPopulated()||neighbor.isPartialyPopulated())) {
                    doPopulateForNeighbor(neighbor, i);
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
            Chunk neighbor = chunkStorage.getChunkForVoxel(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2], chunk.getDepth());
            if( neighbor!=null ) {
                neighbor.setNeighborsPopulated(false);
            }
        }
        chunk.setPopulated(false);
        chunk.setNeighborsPopulated(false);
    }

    protected abstract void doPopulate( Chunk chunk );
    protected abstract void doPopulateForNeighbor( Chunk chunk, int neighbor );


}
