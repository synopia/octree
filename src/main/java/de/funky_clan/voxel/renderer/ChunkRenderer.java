package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.voxel.ChunkPopulator;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

/**
 * @author synopia
 */
public class ChunkRenderer {
    private BufferedRenderer renderer;
    private CubeRenderer cubeRenderer;
    private int glListId;
    private boolean dirty;
    private Chunk chunk;

    private int[][] neighbors = new int[][]{
            {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };
    private int[] map;

    public ChunkRenderer(BufferedRenderer renderer) {
        this.renderer = renderer;
        cubeRenderer = new CubeRenderer(renderer);
        glListId = GL11.glGenLists(1);
        dirty    = true;
    }

    public boolean needsUpdate() {
        return chunk.isVisible() && (chunk.isDirty() || dirty);
    }

    public void render() {
        if( !chunk.isVisible() ) {
            return;
        }

        GL11.glCallList(glListId);
    }

    public void update() {

        int size = chunk.getSize();
        boolean totallyEmpty = true;
        map = chunk.getMap();
        if( map !=null ) {
            GL11.glNewList(glListId, GL11.GL_COMPILE);
            renderer.begin();
            for( int x = 0; x < size; x++ ) {
                for( int y = 0; y < size; y++ ) {
                    for( int z = 0; z < size; z++ ) {
                        boolean rendered = renderBlock(x, y, z);
                        if( rendered ) {
                            totallyEmpty = false;
                        }
                    }
                }
            }
            renderer.render();
            GL11.glEndList();
        }

        dirty = false;
        chunk.setDirty(false);
        chunk.setVisible(!totallyEmpty);
    }

    private boolean renderBlock( int x, int y, int z ) {
        boolean result = false;
        int size = chunk.getSize();
        int offs = x + ( y*size+z ) * size;
        int color = map[offs];

        if( color!=0 ) {
            for (int i = 0; i < 6; i++) {
                int nx = x + neighbors[i][0];
                int ny = y + neighbors[i][1];
                int nz = z + neighbors[i][2];
                int noffs = nx + ( ny*size+nz ) * size;
                boolean empty;
                if( noffs>=0 && noffs<map.length ) {
                    empty = map[noffs]==0;
                } else {
                    empty = chunk.getPixel(nx, ny, nz)==0;
                }
                if(empty) {
                    result = true;
                    cubeRenderer.renderCubeFace(x + chunk.getX(), y + chunk.getY(), z + chunk.getZ(), 1/16.f, 0, color, i);
                }
            }
        }
        return result;
    }


    public void setChunk(Chunk chunk) {
        dirty = true;
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
