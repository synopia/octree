package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.opengl.GL11;

/**
 * @author synopia
 */
public class ChunkRenderer {
    private BufferedRenderer renderer;
    private CubeRenderer cubeRenderer;
    private OctreeNode root;
    private boolean generated = false;

    private int[][] neighbors = new int[][]{
        {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };

    public ChunkRenderer(BufferedRenderer renderer, OctreeNode root) {
        this.renderer = renderer;
        cubeRenderer = new CubeRenderer(renderer);
        this.root = root;
    }

    public void begin() {
        generated = false;
    }

    public void renderChunk(Chunk chunk) {
        if( !chunk.isVisible() ) {
            return;
        }

        if( chunk.isDirty() && !generated ) {
            generated = true;
            int currentId = chunk.getGlListId();
            if( currentId==0 ) {
                currentId = GL11.glGenLists(1);
                chunk.setGlListId(currentId);
            }
            GL11.glNewList(currentId, GL11.GL_COMPILE);
            renderer.begin();

            int size = chunk.getSize();
            boolean totallyEmpty = true;
            for( int x = 0; x < size; x++ ) {
                for( int y = 0; y < size; y++ ) {
                    for( int z = 0; z < size; z++ ) {
                        int color = chunk.getPixel(x+chunk.getX(), y+chunk.getY(), z+chunk.getZ());

                        if( color!=0 ) {
                            for (int i = 0; i < 6; i++) {
                                int nx = x + neighbors[i][0];
                                int ny = y + neighbors[i][1];
                                int nz = z + neighbors[i][2];

                                boolean empty;
                                nx += chunk.getX();
                                ny += chunk.getY();
                                nz += chunk.getZ();
                                empty = chunk.getPixel(nx, ny, nz) == 0;
                                if(empty) {
                                    totallyEmpty = false;
                                    cubeRenderer.renderCubeFace(x + chunk.getX(), y + chunk.getY(), z + chunk.getZ(), 1/16.f, 0, color, i);
                                }
                            }
                        }
                    }
                }
            }

            renderer.render();
            GL11.glEndList();
            chunk.setDirty(false);
            chunk.setVisible(!totallyEmpty);
        }

        int id = chunk.getGlListId();
        if( id>0 ) {
            GL11.glCallList(id);
        }
    }
}
