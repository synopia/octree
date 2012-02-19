package de.funky_clan;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.funky_clan.chunks.*;
import de.funky_clan.chunks.generators.NoiseGenerator;
import de.funky_clan.chunks.generators.SphereGenerator;
import de.funky_clan.coregl.*;
import de.funky_clan.coregl.renderer.MappedVertex;
import de.funky_clan.filesystem.BlockDevice;
import de.funky_clan.octree.VoxelEngine;
import de.funky_clan.octree.data.Octree;
import de.funky_clan.octree.data.OctreeNode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.mapped.MappedObjectClassLoader;
import org.lwjgl.util.mapped.MappedObjectTransformer;

/**
 * @author synopia
 */
public class Main implements Application  {
    private final static int RADIUS = 4000;

    @Inject
    private VoxelEngine engine;
    @Inject
    private Octree      octree;

    @Inject
    private FileBufferedGenerator fileBufferedGenerator;

    @Inject
    private BlockDevice blockDevice;

    private Texture     texture;

    private boolean sphere;
    private boolean noise  = true;

    public void processArgs( String[] args ) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--sphere") || arg.equalsIgnoreCase("-s")) {
                noise = false;
                sphere = true;
            } else if (arg.equalsIgnoreCase("--noise") || arg.equalsIgnoreCase("-n")) {
                noise = true;
                sphere = false;
            }
        }
    }

    @Override
    public void init(ApplicationController ctrl) {
        blockDevice.start();
        Generator populator = null;
        if( sphere ) {
            populator = new SphereGenerator(RADIUS, RADIUS, RADIUS, RADIUS - 1);
        }
        if( noise ) {
            populator = new NoiseGenerator(RADIUS,RADIUS,RADIUS,RADIUS-1);
        }
        fileBufferedGenerator.setGenerator(populator);
        engine.setOctree(octree);
        engine.setPopulator(new NeigborPopulator(engine.getStorage(), fileBufferedGenerator));

        engine.setFpsControl(true);
        engine.setShowInfo(true);
        engine.init();
        texture = ctrl.getTexture("minecraft/terrain.png");

        engine.getLighting().createLight(0,0,0, .4f, .4f, .4f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
        engine.getLighting().createLight(30,30,30, .9f, .9f, .9f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
        engine.getLighting().createLight(RADIUS, 2.1f*RADIUS, RADIUS, .9f, .9f, .9f, .4f, .4f, .4f, 1f,0.01F,0.00001f);

        engine.getCamera().lookAt(RADIUS, 2*RADIUS, RADIUS, 1+RADIUS,2*RADIUS,RADIUS,0,1,0);

    }

    @Override
    public void update(int delta) {
        engine.update(delta);
        Camera cam = engine.getCamera();
        cam.project(RADIUS,RADIUS,RADIUS);
    }

    @Override
    public void render() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST );
//        texture.bind();
        engine.render();
    }

    public void stop() {
        blockDevice.stop();
    }

    public static void main(String[] args) {
        MappedObjectTransformer.register(MappedVertex.class);
        if(MappedObjectClassLoader.fork(Main.class, args)) {
            return;
        }
        Injector injector = Guice.createInjector(new CoreGlModule(), new AbstractModule() {
            @Override
            protected void configure() {
//                bind(ChunkRenderer.class).to(MarchingCubeRenderer.class);
                bind(ChunkRenderer.class).to(DefaultChunkRenderer.class);
                bind(OctreeNode.class).to(OctreeChunkNode.class);
            }
        });
        ApplicationController ctrl = injector.getInstance(ApplicationController.class);
        ctrl.createDisplay("Octree", 800, 600, false);
        ctrl.setSyncFps(60);
        Main app = injector.getInstance(Main.class);
        app.processArgs(args);

        ctrl.start(app);

        app.stop();
    }
}
