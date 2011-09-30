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
    private ChunkPopulator populator;

    private int[][] neighbors = new int[][]{
        {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };

    public ChunkRenderer(BufferedRenderer renderer, ChunkPopulator populator) {
        this.populator = populator;
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
        if( populator!=null ) {
            populator.populateChunk(chunk);
        }
        GL11.glNewList(glListId, GL11.GL_COMPILE);
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
//        System.out.println(renderer.getTrianglesTotal()*3*renderer.getStrideSize());
        GL11.glEndList();
        dirty = false;
        chunk.setDirty(false);
        chunk.setVisible(!totallyEmpty);
    }


    public void setChunk(Chunk chunk) {
        dirty = true;
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public ChunkPopulator getPopulator() {
        return populator;
    }

    public void setPopulator(ChunkPopulator populator) {
        this.populator = populator;
    }
}
