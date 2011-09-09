package de.funky_clan.octree.minecraft;

import de.funky_clan.octree.WritableRaster;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * @author synopia
 */
public class RegionFileLoader {
    public void load( WritableRaster raster, int chunkX, int chunkZ ) {
        DataInputStream inputStream = RegionFileCache.getChunkDataInputStream( new File( "d:/games/minecraft/world" ), chunkX, chunkZ );
        if( inputStream != null ) {
            try {
                NBTInputStream nbt    = new NBTInputStream( inputStream );
                CompoundTag    root   = (CompoundTag) nbt.readTag();
                CompoundTag    level  = (CompoundTag) root.getValue().get( "Level" );
                ByteArrayTag   blocks = (ByteArrayTag) level.getValue().get( "Blocks" );

                int sizeX = 1 << 4;
                int sizeY = 1 << 7;
                int sizeZ = 1 << 4;
                byte[] data = blocks.getValue();
                for( int x = 0; x < sizeX; x++ ) {
                    for( int y = 0; y < sizeY; y++ ) {
                        for( int z = 0; z < sizeZ; z++ ) {
                            int   i     = y + ( z * sizeY ) + x * sizeY * sizeZ;
                            if( data[i]!=0 ) {
                                raster.setPixel((chunkX<<4) + x+1, y+1, (chunkZ<<4) + z+1, data[i]);
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
