package de.funky_clan.chunks;

import de.funky_clan.octree.data.Octree;

import java.nio.ByteBuffer;

/**
 * @author synopia
 */
public class NeigborPopulator {
    protected static int[][] NEIGHBORS = new int[][]{
                {0,0,32}, {0,0,-32}, {0,32,0}, {0,-32,0}, {32,0,0}, {-32,0,0},
        };

    protected ChunkStorage chunkStorage;
    private ChunkPopulator populator;

    public NeigborPopulator(ChunkStorage chunkStorage, ChunkPopulator populator ) {
        this.chunkStorage = chunkStorage;
        this.populator    = populator;
    }

    public void populateChunk(Chunk chunk) {
        if( chunk.isNeighborsPopulated() ) {
            return;
        }

        if( !chunk.isPopulated() ) {
            populator.doPopulate(chunk);
        }

        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
        for (int i = 0, neighborsLength = NEIGHBORS.length; i < neighborsLength; i++) {
            int[] neighborCoords = NEIGHBORS[i];
            Chunk neighbor = chunkStorage.getChunkForVoxel(x + neighborCoords[0], y + neighborCoords[1], z + neighborCoords[2], chunk.getDepth());
            if (neighbor != null && !neighbor.isPopulated() && !neighbor.isPartialyPopulated(i) ) {
                populator.doPopulateForNeighbor(neighbor, i);
                neighbor.setPartialyPopulated(i);
            }
        }
        chunk.setNeighborsPopulated(true);
    }

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
//        chunk.setAllocated(false);
    }
}
