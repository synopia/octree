package de.funky_clan.octree.renderer;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.octree.data.Chunk;
import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;
import de.funky_clan.octree.minecraft.blocks.Block;
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
    private Octree octree;

    public ChunkRenderer(Octree octree, BufferedRenderer renderer) {
        this.octree = octree;
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
        boolean totallyEmpty = true;
        map = chunk.getMap();
        if( map !=null ) {
            GL11.glNewList(glListId, GL11.GL_COMPILE);
            renderer.begin();
            for( int x = 0; x < OctreeNode.CHUNK_SIZE; x++ ) {
                for( int y = 0; y < OctreeNode.CHUNK_SIZE; y++ ) {
                    for( int z = 0; z < OctreeNode.CHUNK_SIZE; z++ ) {
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
        int offs = x + ( y*OctreeNode.CHUNK_SIZE+z ) * OctreeNode.CHUNK_SIZE;
        int color = map[offs];
        Block block = Block.MAP[color & 0x7f];
        renderer.setScale(chunk.getSize()/OctreeNode.CHUNK_SIZE, chunk.getSize()/OctreeNode.CHUNK_SIZE, chunk.getSize()/OctreeNode.CHUNK_SIZE );
        if( block!=Block.AIR ) {
            for (int i = 0; i < 6; i++) {
                int nx = x + NEIGHBORS[i][0];
                int ny = y + NEIGHBORS[i][1];
                int nz = z + NEIGHBORS[i][2];

                boolean transparent;
                Block neighbor;
                if( nx>=0 && ny>=0 && nz>=0 && nx<32 && ny<32 && nz<32 ) {
                    int noffs = nx + ( ny*OctreeNode.CHUNK_SIZE+nz ) * OctreeNode.CHUNK_SIZE;
                    neighbor = Block.MAP[map[noffs] & 0x7f];
                } else {
                    int cx = ((chunk.getX()+nx)>>5)<<5;
                    int cy = ((chunk.getY()+ny)>>5)<<5;
                    int cz = ((chunk.getZ()+nz)>>5)<<5;
                    if( cx<0 || cy<0 || cz<0 ) {
                        neighbor = Block.AIR;
                    } else {
                        Chunk n = octree.getChunkForVoxel(cx, cy, cz, chunk.getDepth());
                        if( !n.isPopulated() ) {
                            throw new IllegalStateException("Chunk is not available "+n+" while rendering "+chunk);
                        }
//                        neighbor = Block.MAP[n.getPixel(chunk.getX()+nx, chunk.getY()+ny, chunk.getZ()+nz) & 0x7f];
                        neighbor = Block.AIR;
                    }
                }
                transparent = neighbor.isTransparent() && neighbor!=block;
                if(transparent) {
                    result = true;
                    float d = chunk.getSize()/OctreeNode.CHUNK_SIZE;
                    cubeRenderer.renderCubeFace(x*d + chunk.getX(), y*d + chunk.getY(), z*d + chunk.getZ(), block.getTextureX(i), block.getTextureY(i), color, i);
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
