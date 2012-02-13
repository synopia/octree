package de.funky_clan.octree.data;

import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.octree.ChunkPopulator;
import de.funky_clan.octree.Morton;
import org.lwjgl.Sys;

/**
 * @author synopia
 */
public class Octree {
    private OpenLongObjectHashMap chunks = new OpenLongObjectHashMap();
    private OctreeNode root;
    private ChunkPopulator populator;
    private FastPriorityQueue<Entry> queue = new FastPriorityQueue<Entry>();

    public Octree(int x, int y, int z, int depth) {
        root = new OctreeNode(this, x, y, z, depth);
    }

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
        long morton = Morton.mortonCode(x>>OctreeNode.CHUNK_BITS, y>>OctreeNode.CHUNK_BITS, z>>OctreeNode.CHUNK_BITS, depth);
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
     protected Chunk get( long morton ) {
         return (Chunk) chunks.get(morton);
     }
     protected void add( Chunk node ) {
         long morton = node.getMorton();
         chunks.put(morton, node );
    }
    
    public OctreeNode getRoot() {
        return root;
    }

    public OctreeNode createNode( int x, int y, int z, int depth) {
        OctreeNode node;
        
        if( depth < OctreeNode.CHUNK_BITS ) {
            node = new OctreeChunkNode(this, x, y, z, depth);
        } else {
            node = new OctreeNode(this, x, y, z, depth);
        }
        return node;
    }
    
    public int size() {
        return chunks.size();
    }
    public int populateSize() {
        return queue.size();
    }
}
