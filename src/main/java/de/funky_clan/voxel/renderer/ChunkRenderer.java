package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

/**
 * @author synopia
 */
public class ChunkRenderer implements Comparable<ChunkRenderer>{
    private BufferedRenderer renderer;
    private CubeRenderer cubeRenderer;
    private int glListId;
    private boolean dirty;
    private Chunk chunk;
    private long lastRender;
    private int tick;

    private int[][] neighbors = new int[][]{
        {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };

    public ChunkRenderer(BufferedRenderer renderer, Chunk chunk) {
        this.renderer = renderer;
        cubeRenderer = new CubeRenderer(renderer);
        this.chunk = chunk;
        glListId = GL11.glGenLists(1);
        dirty    = true;
    }

    public void render() {
        lastRender = Sys.getTime();
        if( !chunk.isVisible() ) {
            return;
        }

        if( chunk.isDirty() || dirty ) {
            updateList();
        }

        GL11.glCallList(glListId);
    }

    private void updateList() {
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
        GL11.glEndList();
        dirty = false;
        chunk.setDirty(false);
        chunk.setVisible(!totallyEmpty);
    }

    @Override
    public int compareTo(ChunkRenderer o) {
        return ( this.lastRender>o.lastRender ? 1 : (this.lastRender < o.lastRender ? -1 : 0 ));
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void setChunk(Chunk chunk) {
        dirty = true;
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
