package de.funky_clan.chunks;

import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.octree.Morton;
import de.funky_clan.octree.data.OctreeNode;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * @author synopia
 */
public class ChunkStorage {
    private NeigborPopulator populator;
    private OpenLongObjectHashMap chunks = new OpenLongObjectHashMap();
    private BlockingQueue<Chunk> queue = new ArrayBlockingQueue<Chunk>(1000);

    public void setPopulator(NeigborPopulator populator) {
        this.populator = populator;

        ExecutorService pool = Executors.newFixedThreadPool(1);
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        doPopulation();
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pool.execute(runner);
    }

    public void populate( Chunk chunk ) {
        if( populator==null ) {
            return;
        }

        if( !chunk.isQueued() ) {
            queue.offer(chunk);
            chunk.setQueued();
        }
    }

    protected void doPopulation() throws InterruptedException {
        while (!queue.isEmpty()  ) {
            Chunk chunk = queue.take();
            if( chunk!=null ) {
                populator.populateChunk(chunk);
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
        Reference r = (Reference) chunks.get(morton);
        return r!=null ? (Chunk) r.get() : null;
     }
     public void add( Chunk node ) {
         long morton = node.getMorton();
         chunks.put(morton, new WeakReference<Chunk>(node) );
    }

    public int size() {
        return chunks.size();
    }
    public int populateSize() {
        return 1000-queue.remainingCapacity();
    }

}
