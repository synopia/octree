package de.funky_clan.chunks;

import de.funky_clan.chunks.ChunkStorage;
import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;
import de.funky_clan.minecraft.blocks.Block;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

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
    private ByteBuffer map;
    private ChunkStorage storage;

    public ChunkRenderer(ChunkStorage storage, BufferedRenderer renderer) {
        this.storage = storage;
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
        int color = map.getShort(offs*2);
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
                    neighbor = Block.MAP[map.getShort(noffs*2) & 0x7f];
                } else {
                    neighbor = Block.AIR;
/*
                    int absX = chunk.getX()+nx;
                    int absY = chunk.getY()+ny;
                    int absZ = chunk.getZ()+nz;
                    int chunkX = absX >> OctreeNode.CHUNK_BITS;
                    int chunkY = absY >> OctreeNode.CHUNK_BITS;
                    int chunkZ = absZ >> OctreeNode.CHUNK_BITS;
                    int relX = absX-(chunkX<<OctreeNode.CHUNK_BITS);
                    int relY = absY-(chunkY<<OctreeNode.CHUNK_BITS);
                    int relZ = absZ-(chunkZ<<OctreeNode.CHUNK_BITS);
                    if( absX<0 || absY<0 || absZ<0 ) {
                        neighbor = Block.AIR;
                    } else {
                        Chunk n = storage.getChunkForVoxel(absX, absY, absZ, chunk.getDepth());
                        if( ! (n.isPopulated()||n.isPartialyPopulated()) ) {
                            throw new IllegalStateException("Chunk is not available "+n+" while rendering "+chunk);
                        }
                        neighbor = Block.MAP[n.getPixel(relX, relY, relZ) & 0x7f];
                    }
*/
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
