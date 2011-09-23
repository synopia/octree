package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.geom.Sphere;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;

/**
 * @author synopia
 */
public class OctreeRenderer {
    private ChunkRenderer chunkRenderer;
    private BufferedRenderer renderer;

    public OctreeRenderer(BufferedRenderer renderer, OctreeNode root) {
        this.renderer = renderer;
        chunkRenderer = new ChunkRenderer(renderer, root);
    }

    public void render( OctreeNode node, Camera camera ) {
        chunkRenderer.begin();
        render(node, camera, true);
//        Sphere sphere = new Sphere(camera.getPosition(), 200);
//        render(node, sphere, true);
    }

    protected void render(OctreeNode node, Sphere boundingSphere, boolean testChildren) {
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
                chunkRenderer.renderChunk(chunk);
            } else {
                render(child, boundingSphere, true);
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
                chunkRenderer.renderChunk(chunk);
            } else {
                render(child, camera, testChildren);
            }
        }
    }
}
