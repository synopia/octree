package de.funky_clan.octree.minecraft;


import de.funky_clan.octree.WritableRaster;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author synopia
 */
public class SchematicLoader {
    public int getPOTSize( int value ) {
        if( (value & (value-1))!=0 ) {
            int log = 0;
            while( (value>>=1)!=0 ) {
                log ++;
            }
            value = 2 << log;
        }

        return value;
    }
    public void load(WritableRaster raster, String filename) {

        try {
            InputStream fileInput;
            fileInput = getClass().getResourceAsStream( "/" + filename );
            if( fileInput==null ) {
                fileInput = new FileInputStream(filename);
            }
            DataInputStream in = new DataInputStream( new GZIPInputStream( fileInput ));
            NBTInputStream nbt    = new NBTInputStream( in );
            CompoundTag root   = (CompoundTag) nbt.readTag();
            ShortTag width = (ShortTag) root.getValue().get("Width");
            ShortTag length = (ShortTag) root.getValue().get("Length");
            ShortTag height = (ShortTag) root.getValue().get("Height");
            int sizeX = width.getValue();
            int sizeZ = length.getValue();
            int sizeY = height.getValue();

            int max = getPOTSize(Math.max(Math.max(sizeX, sizeY), sizeZ));

            ByteArrayTag byteArrayTag = (ByteArrayTag) root.getValue().get("Blocks");
            byte[] blocks = byteArrayTag.getValue();
            for( int x = 0; x < sizeX; x++ ) {
                for( int y = 0; y < sizeY; y++ ) {
                    for( int z = 0; z < sizeZ; z++ ) {
                        int   i     = x + ( y*sizeZ+z ) * sizeX;
                        if( blocks[i]>0 ) {
                            raster.setPixel(x+1, y+1, z+1, blocks[i]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
