package de.funky_clan.chunks;

/**
 * @author synopia
 */
public class NeigborPopulator {
    protected static int[][] NEIGHBORS = new int[][]{
                {0,0,32}, {0,0,-32}, {0,32,0}, {0,-32,0}, {32,0,0}, {-32,0,0},
        };
    private static final int[][] MC_NEIGHBORS = new int[][]{
                {0,0,0}, {1,0,0}, {1,0,1}, {0,0,1}, {0,1,0}, {1,1,0}, {1,1,1}, {0,1,1}
        };

    protected ChunkStorage chunkStorage;
    private Generator populator;

    public NeigborPopulator(ChunkStorage chunkStorage, Generator populator ) {
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
//        System.out.println("Populating "+chunk);
        for (int i = 1, neighborsLength = MC_NEIGHBORS.length; i < neighborsLength; i++) {
            int[] neighborCoords = MC_NEIGHBORS[i];
            Chunk neighbor = chunkStorage.getChunkForVoxel(x + 32*neighborCoords[0], y + 32*neighborCoords[1], z + 32*neighborCoords[2], chunk.getDepth());

            if (neighbor != null && !neighbor.isPopulated() && !neighbor.isPartialyPopulated() ) {
                populator.doPopulateForNeighbor(neighbor);
//                System.out.println(" Populating neighbor "+neighbor);
            } else {
//                System.out.println(" Already populated "+neighbor);
            }
        }
        chunk.setNeighborsPopulated(true);
    }

    public void releaseChunk(Chunk chunk) {

        int x = chunk.getX();
        int y = chunk.getY();
        int z = chunk.getZ();
//        for (int[] neighborCoords : MC_NEIGHBORS) {
//            Chunk neighbor = chunkStorage.getChunkForVoxel(x + 32*neighborCoords[0], y + 32*neighborCoords[1], z + 32*neighborCoords[2], chunk.getDepth());
//            if( neighbor!=null ) {
//                neighbor.setNeighborsPopulated(false);
//                System.out.println(" releasing chunk "+neighbor);
//            }
//        }
//        chunk.setPopulated(false);
//        chunk.setNeighborsPopulated(false);
//        System.out.println("Releasing chunk "+chunk);
    }
}
