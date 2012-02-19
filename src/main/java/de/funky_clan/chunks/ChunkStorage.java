package de.funky_clan.chunks;

import cern.colt.map.OpenLongObjectHashMap;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.funky_clan.octree.Morton;
import de.funky_clan.octree.data.FastPriorityQueue;
import de.funky_clan.octree.data.OctreeNode;

import java.util.concurrent.*;

/**
 * @author synopia
 */
@Singleton
public class ChunkStorage {
    public static class Job implements Comparable<Job> {
        public Chunk chunk;
        public float dist;

        public Job(Chunk chunk, float dist) {
            this.chunk = chunk;
            this.dist = dist;
        }

        @Override
        public int compareTo(Job o) {
            return dist<o.dist ? -1 : (dist>o.dist ? 1 : 0);
        }
    }

    private NeigborPopulator populator;
    private OpenLongObjectHashMap chunks = new OpenLongObjectHashMap();
    private FastPriorityQueue<Job> processQueue = new FastPriorityQueue<Job>();
    private static final Object lock = new Object();

    public void setPopulator(NeigborPopulator populator) {
        this.populator = populator;

        ExecutorService pool = Executors.newFixedThreadPool(1);
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Job job = null;
                        synchronized (lock) {
                            if( !processQueue.isEmpty() ) {
                                job = processQueue.poll();
                            }
                        }
                        if( job!=null ) {
                            doPopulation(job);
                        }
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pool.execute(runner);
    }

    public void populate( Chunk chunk, float dist ) {
        if( populator==null ) {
            return;
        }

        synchronized (lock) {
            if( !chunk.isQueued() ) {
                Job job = new Job(chunk, dist);
                processQueue.add(job);
                chunk.setQueued();
            }
        }
    }

    protected void doPopulation(Job job ) throws InterruptedException {
        Chunk chunk = job.chunk;
        if( chunk!=null ) {
            populator.populateChunk(chunk);
        }
    }

    public Chunk getChunkForVoxel( int x, int y, int z, int depth ) {
        if( x<0 || y<0 || z<0 ) {
            return null;
        }
        int cx = x>>OctreeNode.CHUNK_BITS;
        int cy = y>>OctreeNode.CHUNK_BITS;
        int cz = z>>OctreeNode.CHUNK_BITS;
        long morton = Morton.mortonCode(cx, cy, cz, depth);
        Chunk chunk = get(morton);
        if( chunk==null ) {
            chunk = new Chunk(cx, cy, cz, depth);
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
        return processQueue.size();
    }

}
