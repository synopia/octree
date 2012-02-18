package de.funky_clan.filesystem;

import cern.colt.map.OpenLongObjectHashMap;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.chunks.ChunkPopulator;
import de.funky_clan.chunks.OctreeChunkNode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Timer;
import java.util.TimerTask;

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
    private RandomAccessFile dataFile;
    private FileChannel dataChannel;
    private int nextPos;

    private RandomAccessFile indexFile;
    private FileChannel indexChannel;

    public FileStorage() throws FileNotFoundException {
        dataFile = new RandomAccessFile("data.bin", "rw");
        dataChannel = dataFile.getChannel();
        nextPos = 0;

        indexFile = new RandomAccessFile("index.bin", "rw");
        indexChannel = indexFile.getChannel();

        loadIndex();
    }

    protected ByteBuffer getByteBuffer( Entry entry ) {
        try {
            return dataChannel.map(FileChannel.MapMode.READ_WRITE, entry.position, entry.size);
        } catch (IOException e) {
            return ByteBuffer.allocateDirect(entry.size);
        }
    }

    protected Entry find( long code ) {
        return (Entry) index.get(code);
    }

    protected Entry allocate( long code ) {
        Entry entry  = new Entry();
        entry.morton = code;
        entry.size   = OctreeChunkNode.CHUNK_SIZE * OctreeChunkNode.CHUNK_SIZE * OctreeChunkNode.CHUNK_SIZE * 2;
        entry.position = nextPos;

        addToIndex(code, entry);

        nextPos += entry.size;
        return entry;
    }

    private void loadIndex() {
        try {
            indexFile.seek(0);
            while (indexFile.getFilePointer()<indexFile.length() ) {
                Entry entry     = new Entry();
                entry.morton    = indexFile.readLong();
                entry.position  = indexFile.readLong();
                entry.size      = indexFile.readInt();
                index.put(entry.morton, entry);
                nextPos        += entry.size;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToIndex(long code, Entry entry) {
        index.put(code, entry);
        try {
            indexFile.writeLong(entry.morton);
            indexFile.writeLong(entry.position);
            indexFile.writeInt(entry.size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPopulate(Chunk chunk) {
        allocate(chunk);
    }

    @Override
    public void doPopulateForNeighbor(Chunk chunk, int neighbor) {
        allocate(chunk);
    }

    private void allocate(Chunk chunk) {
        if( !chunk.isAllocated() ) {
            Entry entry = find(chunk.getMorton());
            if( entry==null ) {
                entry = allocate( chunk.getMorton() );
                chunk.allocate(getByteBuffer(entry));
            } else {
                chunk.allocate(getByteBuffer(entry));
                chunk.setPopulated(true);
            }

        }
    }
}
