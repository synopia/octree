package de.funky_clan.filesystem;

import cern.colt.map.OpenLongObjectHashMap;
import com.google.inject.Singleton;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.chunks.ChunkPopulator;
import de.funky_clan.chunks.OctreeChunkNode;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author synopia
 */
@Singleton
public class FileStorage {
    private static class Entry {
        public long     morton;
        public int      state;
        public long     position;
        public int      size;
        public ByteBuffer byteBuffer;
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

    public void allocate(Chunk chunk) {
/*
        if( !chunk.isAllocated() ) {
            Entry entry = find(chunk.getMorton());
            if( entry==null ) {
                entry = allocate( chunk.getMorton() );
                chunk.allocate(getByteBuffer(entry));
            } else {
                chunk.allocate(getByteBuffer(entry));
                chunk.setPopulated(entry.state>0);
            }

        }
*/
    }

    public void store(Chunk chunk ) {
/*
        if( chunk==null || !chunk.isAllocated() ) {
            throw new IllegalArgumentException("Trying to store unallocated chunk "+chunk );
        }
*/
/*
        Entry entry = find(chunk.getMorton());
        if( entry!=null ) {
            if( !chunk.isPopulated() ) {
                if( chunk.isPartialyPopulated() ) {
                    entry.state = 0;
                }
            } else {
                entry.state = 1;
            }
            ((MappedByteBuffer)entry.byteBuffer).force();
            addToIndex(entry.morton, entry);
        }
*/
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

    protected ByteBuffer getByteBuffer( Entry entry ) {
        ByteBuffer map = entry.byteBuffer;
        if( map==null ) {
            try {
                map = dataChannel.map(FileChannel.MapMode.READ_WRITE, entry.position, entry.size);
            } catch (IOException e) {
                map = ByteBuffer.allocateDirect(entry.size);
            }
            entry.byteBuffer = map;
        }

        return map;
    }

    protected Entry find( long code ) {
        return (Entry) index.get(code);
    }

    private void loadIndex() {
        try {
            indexFile.seek(0);
            while (indexFile.getFilePointer()<indexFile.length() ) {
                Entry entry     = new Entry();
                entry.morton    = indexFile.readLong();
                entry.state     = indexFile.readInt();
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
            indexFile.writeInt(entry.state);
            indexFile.writeLong(entry.position);
            indexFile.writeInt(entry.size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
