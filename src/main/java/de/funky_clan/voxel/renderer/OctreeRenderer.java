package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Halfspace;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author synopia
 */
public class OctreeRenderer {
    public static final int MAX_CHUNK_RENDERERS = 5000;

    private HashMap<Chunk, ChunkRenderer> chunkRenderers = new HashMap<Chunk, ChunkRenderer>();
    private SortedSet<ChunkRenderer> freeRenderes = new TreeSet<ChunkRenderer>();

    private List<ChunkRenderer> renderers = new ArrayList<ChunkRenderer>();
    private List<Chunk> chunksInSphere = new ArrayList<Chunk>();
    private List<Chunk> chunksInFrustum = new ArrayList<Chunk>();
    private List<Chunk> chunks = new ArrayList<Chunk>();

    private BufferedRenderer renderer;
    private OctreeNode root;

    private Camera camera;
    private Sphere boundingSphere;

    public OctreeRenderer(BufferedRenderer renderer, OctreeNode root) {
        this.root = root;
        this.renderer = renderer;
    }

    public void render( OctreeNode node, Camera camera ) {
        boundingSphere = new Sphere(camera.getPosition(), 200);
        this.camera    = camera;

        chunksInSphere.clear();
        chunksInFrustum.clear();
        chunks.clear();
        render(node, true);

/*
        freeRenderes.clear();
        Iterator<Map.Entry<Chunk,ChunkRenderer>> iterator = chunkRenderers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Chunk, ChunkRenderer> next = iterator.next();
            ChunkRenderer chunkRenderer = next.getValue();
            if( chunkRenderer.getTick()!=currentTick ) {
                freeRenderes.add(chunkRenderer);
                iterator.remove();
            }

        }
*/
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
                if( camera.getFrustum().sphereInFrustum(chunk.getBoundingSphere())!= Halfspace.OUTSIDE ) {
                    chunksInFrustum.add(chunk);
                } else {
                    chunksInSphere.add(chunk);
                }
            } else {
                render(child, testChildren);
            }
        }
    }

/*
    protected void render( OctreeNode node, Camera camera, boolean testChildren ) {
        if( !node.isVisible() ) {
            return;
        }
        if( testChildren ) {
            if( !node.getBoundingSphere().containsPoint( camera.getPosition() ) ) {
                switch (camera.getFrustum().sphereInFrustum(node.getBoundingSphere())) {

                    case OUTSIDE:
                        return;
                    case INSIDE:
                        testChildren = false;
                        break;
                }
           }
        }

        OctreeNode[] children = node.getChildren();
        for (OctreeNode child : children) {
            if( child==null ) {
                continue;
            }
            if (child.isLeaf()) {
                Chunk chunk = (Chunk) child;

                ChunkRenderer r;
                if( chunkRenderers.containsKey(chunk) ) {
                    r = chunkRenderers.get(chunk);
                } else {
                    if( chunkRenderers.size()<MAX_CHUNK_RENDERERS ) {
                        r = new ChunkRenderer(renderer, chunk);
                    } else {
                        r = findFreeChunkRenderer();
                        r.setChunk(chunk);
                    }
                    chunkRenderers.put(chunk, r);
                }
                r.render();
            } else {
                render(child, camera, testChildren);
            }
        }
    }
*/

    private ChunkRenderer findFreeChunkRenderer() {
        ChunkRenderer oldest = freeRenderes.first();
        freeRenderes.remove(oldest);
        return oldest;
    }

    public ArrayList<String> getDebugInfo() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(String.format("Free: %d", freeRenderes.size()));
        result.add(String.format("Chunks: %d (frustum=%d, sphere=%d)", chunks.size(), chunksInFrustum.size(), chunksInSphere.size()));
        return result;
    }
}
