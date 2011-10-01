package de.funky_clan.voxel.data;

import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.octree.WritableRaster;
import de.funky_clan.voxel.ChunkPopulator;

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
        add( root );
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

    public void doPopulation( ) {
        while (!queue.isEmpty()) {
            Entry entry = queue.poll();
            Chunk chunk = entry.getChunk();
            populator.populateChunk(chunk);

        }
    }

     private OctreeNode get( long morton ) {
         Reference<OctreeNode> ref = (WeakReference<OctreeNode>) chunks.get(morton);
         OctreeNode result = null;
         if( ref!=null ) {
             result = ref.get();
             if( result==null ) {
                 chunks.removeKey(morton);
             }
         }
         return result;
     }
     private void add( OctreeNode node ) {
        chunks.put( node.toMorton(), new WeakReference<OctreeNode>(node) );
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {

    }

    @Override
    public int getPixel(int x, int y, int z) {
        long cx = x/OctreeNode.CHUNK_SIZE;
        long cy = y/OctreeNode.CHUNK_SIZE;
        long cz = z/OctreeNode.CHUNK_SIZE;
        long morton = OctreeNode.toMorton(cx, cy, cz);
        OctreeNode node = get(morton);
        if( node!=null ) {
            if (node instanceof Chunk) {
                Chunk chunk = (Chunk) node;
                return chunk.getPixel(x, y, z);
            }
        }
        return 0;
    }

    public OctreeNode getRoot() {
        return root;
    }

    public OctreeNode createNode( int x, int y, int z, int size) {
        OctreeNode node;
        if( size> OctreeNode.CHUNK_SIZE ) {
            node = new OctreeNode(this, x, y, z, size);
        } else {
            node = new Chunk(this, x, y, z, size);
        }
        add(node);
        return node;
    }
}
