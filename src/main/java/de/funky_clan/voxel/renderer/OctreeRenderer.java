package de.funky_clan.voxel.renderer;

import cern.colt.function.LongObjectProcedure;
import cern.colt.function.LongProcedure;
import cern.colt.function.ObjectProcedure;
import cern.colt.list.LongArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.voxel.ChunkPopulator;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.FastPriorityQueue;
import de.funky_clan.voxel.data.Octree;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author synopia
 */
public class OctreeRenderer {

    private static class Entry implements Comparable<Entry> {
        private Chunk chunk;
        private ChunkRenderer renderer;
        private int state;
        private float distanceToEye;
        private boolean inFrustum;

        private Entry(Chunk chunk) {
            this.chunk = chunk;
        }

        @Override
        public int compareTo(Entry entry) {
            return distanceToEye<entry.distanceToEye ? -1 : (distanceToEye>entry.distanceToEye ? 1 : 0);
        }
    }
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
                entry.renderer.render();
                return true;
            }
        });
    }

    private void updateNew(long frameStartTime) {
        while (!newChunks.isEmpty() && Sys.getTime() - frameStartTime < (1000/30.f) ) {
            Entry entry = newChunks.peek();
            if( entry.chunk.isPopulated() ) {
                entry.renderer.update();
                newChunks.poll();
            } else {
                tree.populate(entry.chunk, entry.distanceToEye);
            }
        }
    }

    private void removeOld() {
        oldChunks.forEach(new ObjectProcedure() {
            @Override
            public boolean apply(Object o) {
                Entry entry = (Entry) o;
                chunkEntries.removeKey(entry.chunk.toMorton());
                if (entry.renderer != null) {
                    freeRenderes.add(entry.renderer);
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
                Chunk chunk = entry.chunk;
                if( entry.state != currentState ) {
                    oldChunks.add( entry );
                } else {
                    ChunkRenderer renderer = entry.renderer;
                    if( renderer==null ) {
                        renderer = entry.renderer = findFreeChunkRenderer();
                        renderer.setChunk(chunk);
                    }
                    if( renderer.needsUpdate() ) {
                        newChunks.add(chunk.toMorton(), entry, entry.distanceToEye);
                    } else {
                        if( entry.inFrustum ) {
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
            OctreeNode child = node.getChild(i);
            if( child==null ) {
                continue;
            }
            if (child.isLeaf()) {
                Chunk chunk = (Chunk) child;
                long morton = chunk.toMorton();
                Entry entry;
                if( chunkEntries.containsKey(morton) ) {
                    entry = (Entry) chunkEntries.get(morton);
                } else {
                    entry = new Entry(chunk);
                    chunkEntries.put(morton, entry);
                }

                entry.state = currentState;
                float dist = camera.getFrustum().sphereInFrustum2(chunk.getBoundingSphere());
                if( dist > 0 ) {
                    entry.distanceToEye = dist;
                    entry.inFrustum = true;
                } else {
                    entry.distanceToEye = 250;
                }
            } else {
                render(child, testChildren);
            }
        }
    }

    private ChunkRenderer findFreeChunkRenderer() {
        return freeRenderes.remove(0);
    }

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(String.format("Free: %d", freeRenderes.size()));
        result.add(String.format("Chunks: %d (new=%d, visible=%d)", chunkEntries.size(), newChunks.size(), visibleChunks.size()));
        return result;
    }
}
