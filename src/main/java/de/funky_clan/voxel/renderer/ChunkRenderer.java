package de.funky_clan.voxel.renderer;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.octree.minecraft.blocks.Block;
import de.funky_clan.voxel.data.Chunk;
import org.lwjgl.opengl.GL11;

/**
 * @author synopia
 */
public class ChunkRenderer {
    private static int[][] NEIGHBORS = new int[][]{
            {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };
    private BufferedRenderer renderer;
    private CubeRenderer cubeRenderer;
    private int glListId;
    private boolean dirty;

    private Chunk chunk;
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
        Block block = Block.MAP[color & 0x7f];

        if( !block.isTransparent() ) {
            for (int i = 0; i < 6; i++) {
                int nx = x + NEIGHBORS[i][0];
                int ny = y + NEIGHBORS[i][1];
                int nz = z + NEIGHBORS[i][2];

                boolean transparent;
                if( nx>=0 && ny>=0 && nz>=0 && nx<32 && ny<32 && nz<32 ) {
                    int noffs = nx + ( ny*size+nz ) * size;
                    transparent = Block.MAP[map[noffs] & 0x7f].isTransparent();
                } else {
                    int cx = ((chunk.getX()+nx)>>5)<<5;
                    int cy = ((chunk.getY()+ny)>>5)<<5;
                    int cz = ((chunk.getZ()+nz)>>5)<<5;
                    if( cx<0 || cy<0 || cz<0 ) {
                        transparent = true;
                    } else {
                        Chunk n = chunk.getOctree().getChunk(cx, cy, cz);
                        if( !n.isPopulated() ) {
                            throw new IllegalStateException("Chunk is not available "+n+" while rendering "+chunk);
                        }
                        transparent = Block.MAP[n.getPixel(chunk.getX()+nx, chunk.getY()+ny, chunk.getZ()+nz) & 0x7f].isTransparent();
                    }
                }
                if(transparent) {
                    result = true;
                    cubeRenderer.renderCubeFace(x + chunk.getX(), y + chunk.getY(), z + chunk.getZ(), block.getTextureX(i), block.getTextureY(i), color, i);
                }
            }
        }
        return result;
    }


    public void setChunk(Chunk chunk) {
        dirty = true;
        this.chunk = chunk;
        if( chunk==null ) {
            this.map   = null;
        }
    }

    public Chunk getChunk() {
        return chunk;
    }

}
