package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.renderer.BaseBufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * @author synopia
 */
public class ChunkRenderer {
    private BaseBufferedRenderer renderer;
    private CubeRenderer cubeRenderer;
    private OctreeNode root;

    private int[][] neighbors = new int[][]{
        {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };

    public ChunkRenderer(BaseBufferedRenderer renderer, OctreeNode root) {
        this.renderer = renderer;
        cubeRenderer = new CubeRenderer(renderer);
        this.root = root;
    }

    public void addChunk( Chunk chunk ) {
        if( !chunk.isVisible() ) {
            return;
        }
        if( !renderer.begin(chunk) ) {
            if( !chunk.isDirty() ) {
                renderer.render();
                return;
            } else {
                renderer.begin(chunk, true);
            }
        }

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
                            if( nx>=0 && ny>=0 && nz>=0 && nx<size && ny<size && nz<size ) {
                                nx += chunk.getX();
                                ny += chunk.getY();
                                nz += chunk.getZ();
                                empty = chunk.getPixel(nx, ny, nz) == 0;
                            } else {
                                nx += chunk.getX();
                                ny += chunk.getY();
                                nz += chunk.getZ();
                                empty = root.getPixel(nx,ny,nz) == 0;
                            }
                            if(empty) {
                                totallyEmpty = false;
                                cubeRenderer.addCubeFace(x+chunk.getX(), y+chunk.getY(), z+chunk.getZ(), 1, color, i);
                            }
                        }
                    }
                }
            }
        }

        renderer.end();
        renderer.render();

        chunk.setDirty(false);
        chunk.setVisible(!totallyEmpty);
    }
}
