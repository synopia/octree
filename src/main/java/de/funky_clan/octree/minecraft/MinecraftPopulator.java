package de.funky_clan.octree.minecraft;

import de.funky_clan.octree.AbstractPopulator;
import de.funky_clan.octree.data.Chunk;
import de.funky_clan.octree.data.OctreeNode;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author synopia
 */
public class MinecraftPopulator extends AbstractPopulator {
    private File file;
    public static final int SIZE_X = 1 << 4;
    public static final int SIZE_Y = 1 << 7;
    public static final int SIZE_Z = 1 << 4;
    private int shiftX;
    private int shiftZ;

    public MinecraftPopulator(int shiftX, int shiftZ) {
        this.shiftX = shiftX;
        this.shiftZ = shiftZ;
        file = new File( "d:/games/minecraft/world" );
    }

    @Override
    protected void doPopulate(Chunk chunk) {
        if( chunk.getY()>=0 && chunk.getY()<128 ) {
            populateMCChunk(chunk, 0, chunk.getY(), 0);
            populateMCChunk(chunk, 16,chunk.getY(),  0);
            populateMCChunk(chunk, 0, chunk.getY(), 16);
            populateMCChunk(chunk, 16, chunk.getY(), 16);
        }
        chunk.setPopulated(true);
    }

    private void populateMCChunk( Chunk chunk, int dx, int dy, int dz ) {
        if( chunk.isPopulated() ) {
            return;
        }
        int chunkX = (chunk.getX()-shiftX+dx)>>4;
        int chunkZ = (chunk.getZ()-shiftX+dz)>>4;

        DataInputStream inputStream = RegionFileCache.getChunkDataInputStream( file, chunkX, chunkZ );
        if( inputStream != null ) {
            try {
                NBTInputStream nbt    = new NBTInputStream( inputStream );
                CompoundTag root   = (CompoundTag) nbt.readTag();
                CompoundTag    level  = (CompoundTag) root.getValue().get( "Level" );
                ByteArrayTag blocks = (ByteArrayTag) level.getValue().get( "Blocks" );

                int maxY = dy+OctreeNode.CHUNK_SIZE;
                if( maxY>128 ) maxY = 128;
                byte[] data = blocks.getValue();
                for( int x = 0; x < SIZE_X; x++ ) {
                    for( int y = dy; y < maxY; y++ ) {
                        for( int z = 0; z < SIZE_Z; z++ ) {
                            int i = y + ( z * SIZE_Y) + x * SIZE_Y * SIZE_Z;
                            if( data[i]!=0 ) {
                                chunk.setPixel( chunk.getX() + dx + x, y, chunk.getZ() + dz + z, data[i]);
                            }
                        }
                    }
                }
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
