package de.funky_clan.chunks;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.CubeRenderer;
import de.funky_clan.octree.data.OctreeNode;
import de.funky_clan.minecraft.blocks.Block;
import org.lwjgl.opengl.GL11;

/**
 * @author synopia
 */
public class DefaultChunkRenderer extends ChunkRenderer {
    private CubeRenderer cubeRenderer;

    public DefaultChunkRenderer(BufferedRenderer renderer, ChunkStorage storage) {
        super(renderer, storage);
        cubeRenderer = new CubeRenderer(renderer);
    }

    @Override
    protected boolean renderBlock( int x, int y, int z ) {
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


}
