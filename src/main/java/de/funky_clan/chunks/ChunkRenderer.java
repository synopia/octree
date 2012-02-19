package de.funky_clan.chunks;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.octree.data.OctreeNode;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * @author synopia
 */
public abstract class ChunkRenderer {
    protected static int[][] NEIGHBORS = new int[][]{
            {0,0,1}, {0,0,-1}, {0,1,0}, {0,-1,0}, {1,0,0}, {-1,0,0}
    };
    protected BufferedRenderer renderer;
    protected int glListId;
    protected boolean dirty;
    protected Chunk chunk;
    protected ByteBuffer map;
    protected ChunkStorage storage;

    public ChunkRenderer(BufferedRenderer renderer, ChunkStorage storage) {
        glListId = GL11.glGenLists(1);
        this.renderer = renderer;
        this.storage = storage;
        dirty    = true;
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

    protected abstract boolean renderBlock( int x, int y, int z );

    public boolean needsUpdate() {
        return chunk.isVisible() && (chunk.isDirty() || dirty);
    }

    public void render() {
        if( !chunk.isVisible() ) {
            return;
        }

        GL11.glCallList(glListId);
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
