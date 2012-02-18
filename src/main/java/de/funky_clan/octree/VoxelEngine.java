package de.funky_clan.octree;

import de.funky_clan.chunks.NeigborPopulator;
import de.funky_clan.chunks.ChunkOctree;
import de.funky_clan.chunks.ChunkStorage;
import de.funky_clan.coregl.ApplicationController;
import de.funky_clan.coregl.BaseEngine;
import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

/**
 * @author synopia
 */
public class VoxelEngine extends BaseEngine {
    public static final String OCTREE_CHUNKS_TEXT = "Octree Chunks: %d, to be populated: %d";
    public static final String POSITION_TEXT = "Position: %2f %2f %2f";
    public static final String MEM_TEXT = "Mem: %d/%d kB";
    private Octree root;
    private ChunkStorage storage;
    private OctreeRenderer octreeRenderer;

    public VoxelEngine(int depth) {
        storage = new ChunkStorage();
        root = new ChunkOctree(storage, 0,0,0, depth);
    }

    public void setPopulator( NeigborPopulator populator ) {
        storage.setPopulator(populator);
    }

    @Override
    public void init(ApplicationController ctrl) {
        super.init(ctrl);
        octreeRenderer = new OctreeRenderer(root, getRenderer(), storage);
    }

    @Override
    public void update(int delta) {
        if( Mouse.isButtonDown(0) ) {
            int x = (int) camera.getX();
            int y = (int) camera.getY();
            int z = (int) camera.getZ();
            if( x>0 && y>0 && z>0 ) {
                //setPixel(x, y, z, 1);
            }
        }
        super.update(delta);
    }

    public void render() {
        beginRender();
        octreeRenderer.render(root.getRoot(), getCamera(), frameStartTime);
        endRender();
    }

    public OctreeNode getRoot() {
        return root.getRoot();
    }

    @Override
    protected ArrayList<String> getDebugInfo() {
        ArrayList<String> info = super.getDebugInfo();
        info.addAll( octreeRenderer.getDebugInfo() );
        info.add(String.format(POSITION_TEXT, camera.getX(), camera.getY(), camera.getZ()));
        info.add(String.format(OCTREE_CHUNKS_TEXT, storage.size(), storage.populateSize()));
        info.add( String.format(MEM_TEXT,
            (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024, Runtime.getRuntime().totalMemory()/1024
            ));

        return info;
    }

    public ChunkStorage getStorage() {
        return storage;
    }
}
