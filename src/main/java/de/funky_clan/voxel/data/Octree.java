package de.funky_clan.voxel.data;

import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.octree.WritableRaster;
import de.funky_clan.voxel.ChunkPopulator;
import org.lwjgl.Sys;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author synopia
 */
public class Octree implements WritableRaster {
    private OpenLongObjectHashMap chunks = new OpenLongObjectHashMap();
    private OctreeNode root;
    private ChunkPopulator populator;
    private FastPriorityQueue<Entry> queue = new FastPriorityQueue<Entry>();

    public Octree(int x, int y, int z, int size) {
        root = new OctreeNode(this, x, y, z, size);
    }

    public void setPopulator(ChunkPopulator populator) {
        this.populator = populator;
    }
    
    public void populate( Entry entry ) {
        if( populator==null ) {
            return;
        }
        Chunk chunk = entry.getChunk();

        if( !chunk.isPopulated() ) {
            queue.add(entry);

//            Chunk chunk1 = (Chunk) createNode(chunk.getX() + OctreeNode.CHUNK_SIZE, chunk.getY() + OctreeNode.CHUNK_SIZE, chunk.getZ() + OctreeNode.CHUNK_SIZE, OctreeNode.CHUNK_SIZE);
//            queue.add(chunk1.toMorton(), chunk1, priority);
        }
    }

    public void doPopulation( long startTime ) {
        while (!queue.isEmpty()  ) {
            Entry entry = queue.poll();
            Chunk chunk = entry.getChunk();
            if( chunk!=null && chunks.containsKey(chunk.toMorton())) {
                populator.populateChunk(chunk);
            }
            if( startTime==0 || Sys.getTime()-startTime>(1000/60.f) ) {
                break;
            }
        }
    }

    public void remove( Chunk chunk ) {
        chunks.removeKey(chunk.toMorton());
        root.removeChunk(chunk.getX(), chunk.getY(), chunk.getZ());
    }

     private Chunk get( long morton ) {
         Reference<Chunk> ref = (WeakReference<Chunk>) chunks.get(morton);
         Chunk result = null;
         if( ref!=null ) {
             result = ref.get();
             if( result==null ) {
                 chunks.removeKey(morton);
             }
         }
         return result;
     }
     private void add( Chunk node ) {
         long morton = node.toMorton();
         chunks.put(morton, new WeakReference<Chunk>(node) );
    }
    
    public Chunk getChunk( int x, int y, int z ) {
        Chunk chunk = get(Chunk.toMorton(x, y, z));
        if( chunk==null ) {
            chunk = new Chunk(this, x, y, z, OctreeNode.CHUNK_SIZE);
            add(chunk);
        }
        return chunk;
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {

    }

    @Override
    public int getPixel(int x, int y, int z) {
        if( x<0 || y<0 || z<0 ) {
            return 0;
        }
        long morton = Chunk.toMorton(x, y, z);
        Chunk chunk= get(morton);
        if( chunk!=null ) {
            return chunk.getPixel(x, y, z);
        }
        return 0;
    }

    public OctreeNode getRoot() {
        return root;
    }

    public OctreeElement createNode( int x, int y, int z, int size) {
        OctreeElement node;
        if( size> OctreeNode.CHUNK_SIZE ) {
            node = new OctreeNode(this, x, y, z, size);
        } else {
            long morton = Chunk.toMorton(x, y, z);
            node = get(morton);
            if( node==null ) {
                node = new Chunk(this, x, y, z, size);
                add((Chunk) node);
            }
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
