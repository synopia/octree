package de.funky_clan.octree;

import de.funky_clan.coregl.BaseEngine;
import de.funky_clan.coregl.GameWindow;
import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;
import de.funky_clan.octree.renderer.OctreeRenderer;
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
    private OctreeRenderer octreeRenderer;

    public VoxelEngine(int depth) {
        root = new Octree(0,0,0, depth);
    }

    public void setPopulator( ChunkPopulator populator ) {
        root.setPopulator(populator);
    }

    @Override
    public void init(GameWindow window) {
        super.init(window);
        octreeRenderer = new OctreeRenderer(root, getRenderer());
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

    public void render( int delta ) {
        beginRender(delta);
        octreeRenderer.render(root.getRoot(), getCamera(), frameStartTime);
        endRender(delta);

       root.doPopulation(frameStartTime);
    }

    public OctreeNode getRoot() {
        return root.getRoot();
    }

    @Override
    protected ArrayList<String> getDebugInfo() {
        ArrayList<String> info = super.getDebugInfo();
        info.addAll( octreeRenderer.getDebugInfo() );
        info.add(String.format(POSITION_TEXT, camera.getX(), camera.getY(), camera.getZ()));
        info.add(String.format(OCTREE_CHUNKS_TEXT, root.size(), root.populateSize()));
        info.add( String.format(MEM_TEXT,
            (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024, Runtime.getRuntime().totalMemory()/1024
            ));

        return info;
    }
}
