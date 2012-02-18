package de.funky_clan.filesystem;

import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.chunks.ChunkPopulator;
import org.lwjgl.BufferUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * @author synopia
 */
public class FileStorage implements ChunkPopulator  {
    private static class Entry {
        public long morton;
        public long position;
        public int  size;
    }
    private OpenLongObjectHashMap index = new OpenLongObjectHashMap();
    private RandomAccessFile file;
    private FileChannel channel;
    public FileStorage() throws FileNotFoundException {
        file = new RandomAccessFile("data.bin", "rw");
        channel = file.getChannel();
    }

    protected InputStream getStream( Entry entry ) throws IOException {
        FileChannel position = channel.position(entry.position);
        IntBuffer buffer = BufferUtils.createIntBuffer(entry.size / 4);
        return null;
    }

    protected Entry find( long code ) {
        return (Entry) index.get(code);
    }

    @Override
    public void doPopulate(Chunk chunk) {
        Entry entry = find(chunk.getMorton());
        if( entry!=null ) {

        }
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk, int neighbor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
