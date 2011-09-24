package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Halfspace;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author synopia
 */
public class OctreeRenderer {
    public static final int MAX_CHUNK_RENDERERS = 5000;

    private HashMap<Chunk, ChunkRenderer> chunkRenderers = new HashMap<Chunk, ChunkRenderer>();
    private List<ChunkRenderer> freeRenderes = new ArrayList<ChunkRenderer>();

    private List<ChunkRenderer> updates = new ArrayList<ChunkRenderer>();
    private List<Chunk> chunksInSphere = new ArrayList<Chunk>();
    private List<Chunk> chunks = new ArrayList<Chunk>();
    private List<Chunk> oldChunks= new ArrayList<Chunk>();
    private HashMap<Chunk, Integer> chunkStates = new HashMap<Chunk, Integer>();
    private HashMap<Chunk, Float> chunksInFrustum = new HashMap<Chunk, Float>();

    private int currentState = 0;
    private BufferedRenderer renderer;

    private OctreeNode root;
    private Camera camera;
    private Sphere boundingSphere;

    public OctreeRenderer(BufferedRenderer renderer, OctreeNode root) {
        this.root = root;
        this.renderer = renderer;
        for (int i = 0; i < MAX_CHUNK_RENDERERS; i++) {
              freeRenderes.add(new ChunkRenderer(renderer));
        }
    }

    public void render( OctreeNode node, Camera camera ) {
        currentState ++;
        boundingSphere = new Sphere(camera.getX(), camera.getY(), camera.getZ(), 200);
        this.camera    = camera;

        oldChunks.clear();
        chunksInSphere.clear();
        chunksInFrustum.clear();
        chunks.clear();

        render(node, true);
        renderUpdated();
        removeOld();
        updateNew();
    }

    private void updateNew() {
        Collections.sort(updates);
        Iterator<ChunkRenderer> it = updates.iterator();
        int maxUpdates = 2;
        while (it.hasNext() && maxUpdates>=0) {
            ChunkRenderer next = it.next();
            next.update();
            next.render();
            it.remove();
            maxUpdates--;
        }
    }

    private void removeOld() {
        for (Chunk chunk : oldChunks) {
            chunkStates.remove(chunk);
            ChunkRenderer renderer = chunkRenderers.remove(chunk);
            freeRenderes.add(renderer);
        }
    }

    private void renderUpdated() {
        for (Map.Entry<Chunk, Integer> entry : chunkStates.entrySet()) {
            Chunk chunk = entry.getKey();
            if( entry.getValue()!=currentState ) {
                oldChunks.add(chunk);
            } else {
                ChunkRenderer renderer;
                if( chunkRenderers.containsKey(chunk) ) {
                    renderer = chunkRenderers.get(chunk);
                } else {
                    renderer = findFreeChunkRenderer();
                    renderer.setChunk(chunk);
                    chunkRenderers.put(chunk, renderer);
                }
                if( !renderer.needsUpdate() ) {
                    renderer.render();
                } else {
                    if( chunksInFrustum.containsKey(chunk) ) {
                        renderer.setDistanceToEye( chunksInFrustum.get(chunk) );
                    }
                    if( !updates.contains(renderer) ) {
                        updates.add(renderer);
                    }
                }
            }
        }
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
        OctreeNode[] children = node.getChildren();
        for (OctreeNode child : children) {
            if( child==null ) {
                continue;
            }
            if (child.isLeaf()) {
                Chunk chunk = (Chunk) child;
                chunks.add(chunk);
                chunkStates.put(chunk, currentState);
                float dist = camera.getFrustum().sphereInFrustum2(chunk.getBoundingSphere());
                if( dist > 0 ) {
                    chunksInFrustum.put(chunk, dist);
                } else {
                    chunksInSphere.add(chunk);
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
        result.add(String.format("Chunks: %d (frustum=%d, sphere=%d, updates=%d)", chunks.size(), chunksInFrustum.size(), chunksInSphere.size(), updates.size()));
        return result;
    }
}
