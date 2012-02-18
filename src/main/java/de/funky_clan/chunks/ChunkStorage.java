package de.funky_clan.chunks;

import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.octree.Morton;
import de.funky_clan.octree.data.Entry;
import de.funky_clan.octree.data.FastPriorityQueue;
import de.funky_clan.octree.data.OctreeNode;
import org.lwjgl.Sys;

/**
 * @author synopia
 */
public class ChunkStorage {
    private ChunkPopulator populator;
    private OpenLongObjectHashMap chunks = new OpenLongObjectHashMap();
    private FastPriorityQueue<Entry> queue = new FastPriorityQueue<Entry>();

    public void setPopulator(ChunkPopulator populator) {
        this.populator = populator;
    }

    public void populate( Entry entry ) {
        if( populator==null ) {
            return;
        }
        Chunk chunk = entry.getChunk();

        if( !chunk.isNeighborsPopulated() ) {
            queue.add(entry);
        }
    }

    public void doPopulation( long startTime ) {
        while (!queue.isEmpty()  ) {
            Entry entry = queue.poll();
            Chunk chunk = entry.getChunk();
            if( chunk!=null ) {
                populator.populateChunk(chunk);
            }
            if( startTime==0 || Sys.getTime()-startTime>(1000/60.f) ) {
                break;
            }
        }
    }

    public Chunk getChunkForVoxel( int x, int y, int z, int depth ) {
        if( x<0 || y<0 || z<0 ) {
            return null;
        }
        long morton = Morton.mortonCode(x >> OctreeNode.CHUNK_BITS, y >> OctreeNode.CHUNK_BITS, z >> OctreeNode.CHUNK_BITS, depth);
        Chunk chunk = get(morton);
        if( chunk==null ) {
            chunk = new Chunk(x, y, z, depth);
            add(chunk);
        }
        return chunk;
    }

    public void remove( Chunk chunk ) {
        chunks.removeKey(chunk.getMorton());
        populator.releaseChunk(chunk);
    }

    @SuppressWarnings("unchecked")
    public Chunk get( long morton ) {
         return (Chunk) chunks.get(morton);
     }
     public void add( Chunk node ) {
         long morton = node.getMorton();
         chunks.put(morton, node );
    }

    public int size() {
        return chunks.size();
    }
    public int populateSize() {
        return queue.size();
    }

}
