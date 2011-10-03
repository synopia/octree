package de.funky_clan.voxel.renderer;

import cern.colt.function.LongObjectProcedure;
import cern.colt.function.ObjectProcedure;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.voxel.data.*;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.List;

/**
 * @author synopia
 */
public class OctreeRenderer {

    public static final int MAX_CHUNK_RENDERERS = 10000;

    private OpenLongObjectHashMap chunkEntries = new OpenLongObjectHashMap();
    private ObjectArrayList       oldChunks = new ObjectArrayList();
    private ObjectArrayList       visibleChunks = new ObjectArrayList();
    private FastPriorityQueue<Entry> newChunks = new FastPriorityQueue<Entry>();

    private List<ChunkRenderer> freeRenderes = new ArrayList<ChunkRenderer>();

    private int currentState = 0;
    private BufferedRenderer renderer;

    private Camera camera;
    private Sphere boundingSphere;
    private Octree tree;

    public OctreeRenderer(BufferedRenderer renderer) {
        this.renderer = renderer;
        for (int i = 0; i < MAX_CHUNK_RENDERERS; i++) {
            freeRenderes.add(new ChunkRenderer(renderer));
        }
    }

    public void render( OctreeNode node, Camera camera, long frameStartTime ) {
        tree = node.getOctree();
        currentState ++;
        boundingSphere = new Sphere(camera.getX(), camera.getY(), camera.getZ(), 200);
        this.camera    = camera;

        oldChunks.clear();
        visibleChunks.clear();

        render(node, true);
        resortEntries();
        removeOld();
        updateNew(frameStartTime);
        renderVisible();
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
            if( renderer!=null ) {
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
                chunkEntries.removeKey(entry.getChunk().toMorton());
                tree.remove(entry.getChunk());
                ChunkRenderer renderer = entry.getRenderer();
                if (renderer != null) {
                    renderer.setChunk(null);
                    freeRenderes.add(renderer);
                    entry.setChunk(null);
                    entry.setRenderer(null);
                }
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
                if( entry.getState() != currentState ) {
                    oldChunks.add( entry );
                } else {
                    ChunkRenderer renderer = entry.getRenderer();
                    if( renderer==null ) {
                        renderer = findFreeChunkRenderer();
                        entry.setRenderer(renderer);
                        renderer.setChunk(chunk);
                    }
                    if( renderer.needsUpdate() ) {
                        if( chunk.isPopulated() ) {
                            newChunks.add(entry);
                        } else {
                            tree.populate(entry);
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
        if( !node.isVisible() ) {
            return;
        }
        if( testChildren ) {
            switch (boundingSphere.sphereInSphere(node.getBoundingSphere()) ) {
                case OUTSIDE:
                    return;
                case INSIDE:
                    testChildren = false;
            }
        }
        for (int i = 0; i < 8; i++) {
            OctreeElement child = node.getChild(i);
            if( child==null ) {
                continue;
            }
            if (child.isLeaf()) {
                Chunk chunk = (Chunk) child;
                long morton = chunk.toMorton();
                Entry entry;
                entry = (Entry) chunkEntries.get(morton);
                if( entry==null ) {
                    entry = new Entry(chunk);
                    chunkEntries.put(morton, entry);
                }

                entry.setState( currentState );
                float dist = camera.getFrustum().sphereInFrustum2(chunk.getBoundingSphere());
                if( dist > 0 ) {
                    entry.setDistanceToEye( dist );
                    entry.setInFrustum( true );
                } else {
                    entry.setDistanceToEye( 250 );
                }
            } else {
                render((OctreeNode) child, testChildren);
            }
        }
    }

    private ChunkRenderer findFreeChunkRenderer() {
        return freeRenderes.remove(0);
    }

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(String.format("Free: %d", freeRenderes.size()));
        result.add(String.format("Chunks:%d %d (new=%d, visible=%d)", Chunk.COUNT, chunkEntries.size(), newChunks.size(), visibleChunks.size()));
        return result;
    }
}
