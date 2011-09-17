package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BaseBufferedRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author synopia
 */
public class OctreeRenderer {
    private ChunkRenderer chunkRenderer;
    private List<Chunk> activeChunks = new ArrayList<Chunk>();
    private BaseBufferedRenderer renderer;


    public OctreeRenderer(BaseBufferedRenderer renderer, OctreeNode root) {
        this.renderer = renderer;
        chunkRenderer = new ChunkRenderer(renderer, root);
    }

    public void render( OctreeNode node, Camera camera ) {
        List<Chunk> oldChunks = new ArrayList<Chunk>( activeChunks );
        activeChunks.clear();

        render(node, camera, true);

        oldChunks.removeAll(activeChunks);
        for (Chunk oldChunk : oldChunks) {
            renderer.release(oldChunk);
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
                activeChunks.add((Chunk) child);
                chunkRenderer.renderChunk((Chunk) child);
            } else {
                render(child, camera, testChildren);
            }
        }
    }
}
