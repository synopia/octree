package de.funky_clan.octree;

import cern.colt.function.LongObjectProcedure;
import cern.colt.function.ObjectProcedure;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenLongObjectHashMap;
import com.google.inject.Inject;
import de.funky_clan.chunks.*;
import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.octree.data.*;
import org.lwjgl.Sys;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * @author synopia
 */
public class OctreeRenderer {
    public static final String CHUNKS_TEXT = "Chunks:%s rendering=%d (new=%d, visible=%d, skipped=%d) total=%d";

    private OpenLongObjectHashMap chunkEntries = new OpenLongObjectHashMap();
    private ObjectArrayList       oldChunks = new ObjectArrayList();
    private ObjectArrayList       visibleChunks = new ObjectArrayList();
    private FastPriorityQueue<Entry> newChunks = new FastPriorityQueue<Entry>();

    private List<ChunkRenderer> freeRenderes = new ArrayList<ChunkRenderer>();
    @Inject
    private Provider<ChunkRenderer> rendererProvider;
    private int totalRenderers;
    private int currentState = 0;
    private int skipped;

    @Inject
    private Camera camera;
    @Inject
    private Sphere boundingSphere;
    @Inject
    private ChunkStorage chunkStorage;

    public void render( OctreeNode node, Camera camera, long frameStartTime ) {
        skipped = 0;
        currentState ++;
        boundingSphere = new Sphere(camera.getX(), camera.getY(), camera.getZ(), 500);
        this.camera    = camera;

        oldChunks.clear();
        visibleChunks.clear();

        render(node, true);
        resortEntries();
        updateNew(frameStartTime);
        renderVisible();

        removeOld();
    }

    private void renderVisible() {
        visibleChunks.forEach(new ObjectProcedure() {
            @Override
            public boolean apply(Object o) {
                Entry entry = (Entry) o;
                entry.getRenderer().render();
                return true;
            }
        });
    }

    private void updateNew(long frameStartTime) {
        while (!newChunks.isEmpty() ) {
            Entry entry = newChunks.poll();
            ChunkRenderer renderer = entry.getRenderer();
            if( renderer!=null && entry.getChunk().isNeighborsPopulated() ) {
                renderer.update();
            }
            if( frameStartTime==0 || Sys.getTime() - frameStartTime > (1000/60.f) ) {
                break;
            }
        }
    }

    private void removeOld() {
        oldChunks.forEach(new ObjectProcedure() {
            @Override
            public boolean apply(Object o) {
                Entry entry = (Entry) o;
                chunkEntries.removeKey(entry.getChunk().getMorton());
                chunkStorage.remove(entry.getChunk());
                ChunkRenderer renderer = entry.getRenderer();
                if (renderer != null) {
                    renderer.setChunk(null);
                    freeRenderes.add(0, renderer);
                    entry.setRenderer(null);
                }
                entry.setChunk(null);
                entry.setDistanceToEye(0);
                return true;
            }
        });
    }

    private void resortEntries() {
        chunkEntries.forEachPair(new LongObjectProcedure() {
            @Override
            public boolean apply(long morton, Object o) {
                Entry entry = (Entry) o;
                Chunk chunk = entry.getChunk();
                if( currentState-entry.getState() > 40  ) {
                    oldChunks.add( entry );
                } else {
                    ChunkRenderer renderer = entry.getRenderer();
                    if( renderer==null ) {
                        renderer = findFreeChunkRenderer();
                        entry.setRenderer(renderer);
                        renderer.setChunk(chunk);
                    }
                    if( renderer.needsUpdate() ) {
                        if( chunk.isNeighborsPopulated() ) {
                            newChunks.add(entry);
                        } else {
                            chunkStorage.populate(chunk, entry.getDistanceToEye());
                        }
                    } else {
                        if( entry.isInFrustum() ) {
                            visibleChunks.add(entry);
                        }
                    }
                }
                return true;
            }
        });
    }

    protected void render(OctreeNode node, boolean testChildren) {
        if( testChildren ) {
            switch (boundingSphere.sphereInSphere(node.getBoundingSphere()) ) {
                case OUTSIDE:
                    return;
                case INSIDE:
                    testChildren = false;
            }
        }
        float dist = (float) Math.sqrt(boundingSphere.distanceSq(node.getBoundingSphere()));
        float factor = node.getSize() / dist;

        if( node instanceof OctreeChunkNode) {
            OctreeChunkNode chunkNode = (OctreeChunkNode) node;
            if( (factor<.7f || node.isLeaf()) ) {
                Chunk chunk = chunkNode.getChunk();
                long morton = chunk.getMorton();
                Entry entry;
                entry = (Entry) chunkEntries.get(morton);
                if( entry==null ) {
                    entry = new Entry(chunk);
                    chunkEntries.put(morton, entry);
                }

                entry.setState( currentState );
                dist = camera.getFrustum().sphereInFrustum2(node.getBoundingSphere());
                if( dist > 0 ) {
                    entry.setDistanceToEye( dist );
                    entry.setInFrustum( true );
                } else {
                    entry.setDistanceToEye( 500 );
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    OctreeNode child = (OctreeNode) node.getChild(i);
                    render(child, testChildren);
                }
            }
        } else {
            for (int i = 0; i < 8; i++) {
                OctreeNode child = (OctreeNode) node.getChild(i);
                render(child, testChildren);
            }
        }
    }

    private ChunkRenderer findFreeChunkRenderer() {
        if( freeRenderes.size()==0 ) {
            totalRenderers++;
            return rendererProvider.get();
        }
        return freeRenderes.remove(0);
    }

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> result = new ArrayList<String>();
        String res = "";
        for (int i = 0; i < 5; i++) {
            res+=i+"="+Chunk.COUNT[i]+" ";
        }
        result.add(String.format(CHUNKS_TEXT, res, chunkEntries.size(), newChunks.size(), visibleChunks.size(), skipped, totalRenderers));
        return result;
    }
}
