package de.funky_clan.octree;

import com.yourkit.api.Controller;
import com.yourkit.api.ProfilingModes;
import de.funky_clan.coregl.BaseEngine;
import de.funky_clan.coregl.GameWindow;
import de.funky_clan.coregl.State;
import de.funky_clan.coregl.Texture;
import de.funky_clan.octree.generators.SpherePopulator;
import de.funky_clan.octree.minecraft.MinecraftPopulator;
import de.funky_clan.octree.minecraft.RegionFileLoader;
import de.funky_clan.voxel.VoxelEngine;
import de.funky_clan.voxel.data.OctreeNode;
import de.funky_clan.voxel.renderer.OctreeRenderer;
import de.funky_clan.octree.generators.SphereGenerator;
import de.funky_clan.octree.minecraft.SchematicLoader;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author paul.fritsche@objectfab.de
 */
public class TestState implements State {
    private final Logger logger = LoggerFactory.getLogger(TestState.class);

    private VoxelEngine engine;

    private float angle;
    private Texture texture;
    private Controller ctrl;
    private long frames = 0;
    private long startTime;
    private long startFrame;
    private boolean memorySnapshop;

    public void init(GameWindow window) throws IOException {
        engine = new VoxelEngine(1<<30, new MinecraftPopulator());//SpherePopulator(500,500,500,499));
//        engine = new VoxelEngine(1<<30, new SpherePopulator(500,500,500,499));
        engine.setFpsControl(true);
        engine.setShowInfo(true);
        engine.init(window);

//        engine.getCamera().lookAt(500, 500, 0, 1,80,1, 0, 1, 0);
        engine.getCamera().lookAt(0, 80, 0, 1,80,1, 0, 1, 0);
//        engine.getCamera().lookAt(0, 0, 0, 1,1,1, 0, 1, 0);
        texture = window.getTexture("minecraft/terrain.png");

        SchematicLoader loader = new SchematicLoader();
//        loader.load(engine.getRoot(), "colloseum.schematic");
        SphereGenerator s = new SphereGenerator();
        OctreeNode octree = engine.getRoot();

        MinecraftPopulator pop = new MinecraftPopulator();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int h = 0; h < 4; h++) {
                    pop.populateChunk( engine.getRoot().getChunk(32*i,32*h,32*j));
                }
            }
        }
//        SchematicLoader loader = new SchematicLoader();
//        loader.load(octree, "turm1.schematic");
//        octree.setPixel(128,128,128,100);
        octree.setPixel(1,80,1,100);
        octree.setPixel(2,80,1,100);
        octree.setPixel(1,81,1,100);
        octree.setPixel(1,80,2,100);
//        s.generate(octree, 128, 128, 128, 110);
//        s.generate(octree, 256, 128, 128, 110);
//        s.generate(octree, 256, 256, 128, 110);
//        s.generate(octree, 256, 256, 256, 110);
//        for (int z = 1; z < 100; z++) {
//            for (int y = 1; y < 100; y++) {
//                for (int x = 1; x < 100; x++) {
//                    octree.setPixel(x+128,y+128,z+128,Color.rgba(1,1,1,1));
//
//                }
//
//            }
//
//        }
//        int totalBufferSize = engine.getRenderer().getStrideSize() * optimizer.getTotalTriangles();
//        logger.info("total buffer size: {}", totalBufferSize);
        engine.getLighting().createLight(0,0,0, .4f, .4f, .4f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
        engine.getLighting().createLight(30,30,30, .9f, .9f, .9f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
        try {
            ctrl = new Controller();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getName() {
        return "in_game";
    }

    public void enter(GameWindow window) {
    }

    public void leave(GameWindow window) {
    }

    public void update(GameWindow window, int delta) {
        engine.update(delta);

        angle += delta/5000.f;
        float x = (float) Math.cos( angle ) * 128*2;
        float z = (float) Math.sin( angle ) * 128*2;

//        engine.getCamera().lookAt(x+128, 128, z+128, 128,128,128,0,1,0);

        if( Keyboard.isKeyDown(Keyboard.KEY_P) && !engine.isProfileMode() ) {
            try {
                engine.setProfileMode(true);
                startTime = Sys.getTime();
                startFrame = frames;
                ctrl.startCPUProfiling(ProfilingModes.CPU_TRACING, "");
                ctrl.startAllocationRecording(true, 1, false, 0);
                System.out.println("profiling started");
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_M)) {
            memorySnapshop = true;
        }
    }

    public void render(GameWindow window, int delta) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST );
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST );
        texture.bind();
        engine.render(delta);
        frames++;
        if( frames-startFrame==250 && engine.isProfileMode() ) {
            try {
                engine.setProfileMode(false);
                startTime = 0;
                startFrame = 0;
                if( memorySnapshop ) {
                    ctrl.captureMemorySnapshot();
                    memorySnapshop = false;
                }
                ctrl.captureSnapshot(ProfilingModes.SNAPSHOT_WITHOUT_HEAP);
                ctrl.stopCPUProfiling();
                ctrl.stopAllocationRecording();

                System.out.println("done profiling");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
