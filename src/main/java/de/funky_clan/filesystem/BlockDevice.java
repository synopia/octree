package de.funky_clan.filesystem;

import cern.colt.map.OpenLongObjectHashMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.funky_clan.chunks.Chunk;
import de.funky_clan.logger.InjectLogger;
import de.funky_clan.logger.InjectLoggerModule;
import de.funky_clan.octree.Morton;
import org.slf4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

/**
 * @author synopia
 */
@Singleton
public class BlockDevice {
    @InjectLogger
    private Logger logger;


    public static final class Entry {
        public static final int SIZE = 8+4+8;

        public long morton;
        public int  blocks;
        public long dataPosition;
        public long entryPosition;
        public void write(DataOutput out) throws IOException {
            out.writeLong(morton);
            out.writeInt(blocks);
            out.writeLong(dataPosition);
        }
        public void read(DataInput in ) throws IOException {
            morton       = in.readLong();
            blocks       = in.readInt();
            dataPosition = in.readLong();
        }
    }
    public static final class Job {
        public boolean write;
        public Chunk   chunk;

        public Job(boolean write, Chunk chunk) {
            this.write = write;
            this.chunk = chunk;
        }
    }

    private OpenLongObjectHashMap morton2Entry = new OpenLongObjectHashMap();
    private List<Entry> freeList = new LinkedList<Entry>();
    private long nextDataPosition  = 0;
    private int  nextEntryPosition = 0;
    private RandomAccessFile dataFile;
    private RandomAccessFile indexFile;

    private BlockingQueue<Job> queue = new ArrayBlockingQueue<Job>(1000);
    private boolean running = true;
    private final ExecutorService pool = Executors.newFixedThreadPool(1);

    public void start() {
        try {
            logger.info("Starting BlockDevice");
            dataFile  = new RandomAccessFile("data.bin", "rw");
            indexFile = new RandomAccessFile("index.bin", "rw");
            loadIndex();
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (running) {
                            try {
                                Job job = queue.take();
                                if( job.write ) {
                                    writeData(job.chunk);
                                } else {
                                    readData(job.chunk);
                                }
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    } finally {
                        try {
                            dataFile.close();
                            indexFile.close();
                        } catch (IOException e) {

                        }
                    }
                }
            };
            pool.execute(runner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void stop() {
        running = false;
        pool.shutdown();
    }

    public void write( Chunk chunk ) {
        queue.offer(new Job(true,chunk));
    }

    public boolean read( Chunk chunk ) {
        queue.offer(new Job(false, chunk));
        return get(chunk.getMorton()) != null;
    }

    protected Entry allocate( long morton, int blocks ) {
        Entry entry = get(morton);
        if( entry==null ) {
            entry = createEntry(morton, blocks);
        } else {
            if( entry.blocks<blocks ) {
                freeList.add(entry);
                entry = createEntry(morton, blocks);
            }
        }
        return entry;
    }

    protected Entry get( long morton ) {
        return (Entry) morton2Entry.get(morton);
    }

    protected void put( long morton, Entry entry ) {
        morton2Entry.put(morton, entry);
    }

    protected void readData( Chunk chunk ) throws IOException {
        Entry entry = get(chunk.getMorton());
        if( entry==null ) {
            return;
        }
        byte[] data = new byte[entry.blocks];

        dataFile.seek(entry.dataPosition);
        dataFile.readFully(data);

        try {
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            InflaterOutputStream out = new InflaterOutputStream(temp);
            out.write(data);
            out.finish();
            out.flush();

            ByteBuffer map = chunk.getMap();
            map.put(temp.toByteArray());
            chunk.setPopulated(true);
        } catch (IOException e) {
            logger.error("Error reading {}", chunk);
            e.printStackTrace();
        }
    }

    protected void writeData( Chunk chunk ) throws IOException {
        logger.info("Write Chunk {}", chunk);
        ByteBuffer map = chunk.getMap();
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        DeflaterOutputStream out = new DeflaterOutputStream(temp, new Deflater(Deflater.BEST_SPEED));
        out.write(map.array());
        out.finish();
        out.flush();
        int compressedSize = temp.toByteArray().length;

        Entry entry = allocate(chunk.getMorton(), compressedSize);
        dataFile.seek(entry.dataPosition);
        dataFile.write(temp.toByteArray());

        updateIndex(entry);
    }

    protected void updateIndex( Entry entry ) throws IOException {
        indexFile.seek(entry.entryPosition);
        entry.write(indexFile);
    }

    private Entry createEntry(long morton, int blocks) {
        Entry entry;
        entry = new Entry();
        entry.morton = morton;
        entry.blocks = blocks;
        entry.dataPosition  = nextDataPosition;
        entry.entryPosition = nextEntryPosition;
        put( morton, entry );

        nextEntryPosition += Entry.SIZE;
        nextDataPosition  += blocks;
        return entry;
    }

    private void loadIndex() {
        logger.info("Loading index: ");
        try {
            indexFile.seek(0);
            nextEntryPosition = 0;
            nextDataPosition  = 0;
            while (indexFile.getFilePointer()<indexFile.length() ) {
                Entry entry     = new Entry();
                entry.entryPosition  = nextEntryPosition;
                entry.read(indexFile);
                put(entry.morton, entry);

                nextEntryPosition += Entry.SIZE;
                nextDataPosition  = entry.dataPosition+entry.blocks;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(" index size="+morton2Entry.size());
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Injector injector = Guice.createInjector(new InjectLoggerModule());
        BlockDevice device = injector.getInstance(BlockDevice.class);

        device.start();

        Chunk chunk = new Chunk(0, 0, 0, Morton.MORTON_BITS);
        chunk.setPixel(0,0,0,3);
        chunk.setPixel(0,0,1,1);
        chunk.setPixel(0,0,4,1);
//        device.write(chunk);

        chunk = new Chunk(0, 0, 0, Morton.MORTON_BITS);
        device.read(chunk);
        while (!chunk.isPopulated()) {
            Thread.sleep(10);
        }
        System.out.println(chunk.getPixel(0, 0, 0));
        chunk.getPixel(0, 0, 1);
        chunk.getPixel(0,0,4);

        device.stop();
    }


}
