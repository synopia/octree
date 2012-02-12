package de.funky_clan.octree;

import com.yourkit.api.Controller;
import com.yourkit.api.ProfilingModes;
import de.funky_clan.coregl.GameWindow;
import de.funky_clan.coregl.State;
import de.funky_clan.coregl.Texture;
import de.funky_clan.octree.generators.NoisePopulator;
import de.funky_clan.octree.generators.SpherePopulator;
import de.funky_clan.octree.data.OctreeNode;
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
    private final static int RADIUS = 400;

    private VoxelEngine engine;

    private float angle;
    private Texture texture;
    private Controller ctrl;
    private long frames = 0;
    private long startTime;
    private long startFrame;
    private boolean memorySnapshop;

    public void init(GameWindow window) throws IOException {
//        engine = new VoxelEngine(1<<21, new MinecraftPopulator(2048, 2048));//SpherePopulator(500,500,500,499));
        engine = new VoxelEngine(1<<21, new SpherePopulator(RADIUS,RADIUS,RADIUS,RADIUS-1));
//        engine = new VoxelEngine(1<<21, new NoisePopulator(RADIUS,RADIUS,RADIUS,RADIUS-1));
        engine.setFpsControl(true);
        engine.setShowInfo(true);
        engine.init(window);

//        engine.getCamera().lookAt(RADIUS*1.1f, RADIUS*1.1f, 0, 1,80,1, 0, 1, 0);
//        engine.getCamera().lookAt(2048, 80, 2048, 1,80,1, 0, 1, 0);
//        engine.getCamera().lookAt(0, 0, 0, 1,1,1, 0, 1, 0);
        angle = (float) Math.toRadians(45);
        float x = (float) Math.cos( angle ) * (RADIUS+4f);
                float y = (float) Math.sin( angle ) * (RADIUS+4f);
                float tx = (float) Math.cos( angle+Math.toRadians(5) ) * (RADIUS+15f);
                float ty = (float) Math.sin( angle+Math.toRadians(5) ) * (RADIUS);
                engine.getCamera().lookAt(x+RADIUS, y+RADIUS, RADIUS, tx+RADIUS,ty+RADIUS,RADIUS,0,1,0);
        texture = window.getTexture("minecraft/terrain.png");

        SchematicLoader loader = new SchematicLoader();
//        loader.load(engine.getRoot(), "colloseum.schematic");
        SphereGenerator s = new SphereGenerator();
        OctreeNode octree = engine.getRoot();

//        SchematicLoader loader = new SchematicLoader();
//        loader.load(octree, "turm1.schematic");
//        octree.setPixel(128,128,128,100);
        octree.setPixel(1,80,1,78);
        octree.setPixel(2,80,1,78);
        octree.setPixel(1,81,1,78);
        octree.setPixel(1,80,2,78);
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

        angle += delta/(float) RADIUS/10f;
        float x = (float) Math.cos( angle ) * (RADIUS+4f);
        float y = (float) Math.sin( angle ) * (RADIUS+4f);
        float tx = (float) Math.cos( angle+Math.toRadians(5) ) * (RADIUS+15f);
        float ty = (float) Math.sin( angle+Math.toRadians(5) ) * (RADIUS);
//        engine.getCamera().lookAt(x+RADIUS, y+RADIUS, RADIUS, tx+RADIUS,ty+RADIUS,RADIUS,0,1,0);
        engine.update(delta);

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
