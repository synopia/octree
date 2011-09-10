package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.renderer.BaseBufferedRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;

/**
 * @author synopia
 */
public class OctreeRenderer {
    private ChunkRenderer chunkRenderer;

    public OctreeRenderer(BaseBufferedRenderer renderer, OctreeNode root) {
        chunkRenderer = new ChunkRenderer(renderer, root);
    }

    public void render( OctreeNode node, Camera camera ) {
        render(node, camera, true);
    }

    public void render( OctreeNode node, Camera camera, boolean testChildren ) {
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
                    case INTERSECT:
/*
                        switch (camera.getFrustum().boxInFrustum(node.getBoundingBox()) ) {
                            case INSIDE :
                                testChildren = false;
                                break;
                            case OUTSIDE:
                                return;
                        }
*/

                }
           }
        }

        OctreeNode[] children = node.getChildren();
        for (OctreeNode child : children) {
            if( child==null ) {
                continue;
            }
            if (child.isLeaf()) {
                chunkRenderer.renderChunk((Chunk) child);
            } else {
                render(child, camera, testChildren);
            }
        }
    }
}
