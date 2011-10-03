package de.funky_clan.octree.minecraft;

import de.funky_clan.voxel.ChunkPopulator;
import de.funky_clan.voxel.data.Chunk;
import de.funky_clan.voxel.data.Octree;
import de.funky_clan.voxel.data.OctreeNode;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author synopia
 */
public class MinecraftPopulator implements ChunkPopulator {
    private File file;
    public static final int SIZE_X = 1 << 4;
    public static final int SIZE_Y = 1 << 7;
    public static final int SIZE_Z = 1 << 4;

    public MinecraftPopulator() {
        file = new File( "d:/games/minecraft/world" );
    }

    @Override
    public void populateChunk(Chunk chunk) {
        if( chunk.isPopulated() ) {
            return;
        }
        if( chunk.getY()>=0 && chunk.getY()<128 ) {
            populateMCChunk(chunk, 0, 0);
            populateMCChunk(chunk, 16, 0);
            populateMCChunk(chunk, 0, 16);
            populateMCChunk(chunk, 16, 16);
        }
        chunk.setPopulated(true);
    }
    
    private void populateMCChunk( Chunk chunk, int dx, int dz ) {
        int chunkX = (chunk.getX()+dx)>>4;
        int chunkZ = (chunk.getZ()+dz)>>4;
        Octree tree = chunk.getOctree();

        DataInputStream inputStream = RegionFileCache.getChunkDataInputStream( file, chunkX, chunkZ );
        if( inputStream != null ) {
            try {
                NBTInputStream nbt    = new NBTInputStream( inputStream );
                CompoundTag root   = (CompoundTag) nbt.readTag();
                CompoundTag    level  = (CompoundTag) root.getValue().get( "Level" );
                ByteArrayTag blocks = (ByteArrayTag) level.getValue().get( "Blocks" );

                for( int dy=0; dy<SIZE_Y; dy+=OctreeNode.CHUNK_SIZE) {
                    chunk = tree.getChunk(chunk.getX(), dy, chunk.getZ());
                    int maxY = dy+OctreeNode.CHUNK_SIZE;
                    if( maxY>=128 ) {
                        maxY = 128;
                    }

                    byte[] data = blocks.getValue();
                    for( int x = 0; x < SIZE_X; x++ ) {
                        for( int y = dy; y < maxY; y++ ) {
                            for( int z = 0; z < SIZE_Z; z++ ) {
                                int   i     = y + ( z * SIZE_Y) + x * SIZE_Y * SIZE_Z;
                                if( data[i]!=0 ) {
                                    chunk.setPixel((chunkX<<4) + x, y, (chunkZ<<4) + z, data[i]);
                                }
                            }
                        }
                    }
                    chunk.setPopulated(true);
                }
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
