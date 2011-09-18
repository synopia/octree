package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BaseBufferedRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author synopia
 */
public class OctreeRenderer {
    private ChunkRenderer chunkRenderer;
    private List<Chunk> activeChunks = new LinkedList<Chunk>();
    private BaseBufferedRenderer renderer;
    private int currentState = 0;

    public OctreeRenderer(BaseBufferedRenderer renderer, OctreeNode root) {
        this.renderer = renderer;
        chunkRenderer = new ChunkRenderer(renderer, root);
    }

    public void render( OctreeNode node, Camera camera ) {
        List<Chunk> old = new LinkedList<Chunk>(activeChunks);
        activeChunks.clear();
        currentState ++;
        render(node, camera, true);

        Iterator<Chunk> it = old.iterator();
        while (it.hasNext()) {
            Chunk next = it.next();
            if( next.getState()!=currentState ) {
                renderer.release(next);
            }
        }
    }

    protected void findChunks( OctreeNode node, Sphere boundingSphere, boolean testChildren ) {
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
                activeChunks.add((Chunk) child);
            } else {
                findChunks(child, boundingSphere, testChildren);
            }
        }
    }

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
                activeChunks.add(chunk);
                chunk.setState(currentState);
                chunkRenderer.renderChunk(chunk);
            } else {
                render(child, camera, testChildren);
            }
        }
    }
}
