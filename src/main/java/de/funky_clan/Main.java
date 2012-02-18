package de.funky_clan;

import de.funky_clan.chunks.generators.NoisePopulator;
import de.funky_clan.coregl.Application;
import de.funky_clan.coregl.ApplicationController;
import de.funky_clan.coregl.Camera;
import de.funky_clan.coregl.Texture;
import de.funky_clan.coregl.renderer.MappedVertex;
import de.funky_clan.octree.Morton;
import de.funky_clan.octree.VoxelEngine;
import de.funky_clan.chunks.generators.SpherePopulator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.mapped.MappedObjectClassLoader;
import org.lwjgl.util.mapped.MappedObjectTransformer;

/**
 * @author synopia
 */
public class Main implements Application  {
    private final static int RADIUS = 400;

    private VoxelEngine engine;
    private Texture     texture;

    @Override
    public void init(ApplicationController ctrl) {
        engine = new VoxelEngine(Morton.MORTON_BITS-5);
        engine.setPopulator(new SpherePopulator(engine.getStorage(), RADIUS,RADIUS,RADIUS,RADIUS-1));
//        engine.setPopulator(new NoisePopulator(engine.getStorage(), RADIUS,RADIUS,RADIUS,RADIUS-1));

        engine.setFpsControl(true);
        engine.setShowInfo(true);
        engine.init(ctrl);
        texture = ctrl.getTexture("minecraft/terrain.png");

        engine.getLighting().createLight(0,0,0, .4f, .4f, .4f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
        engine.getLighting().createLight(30,30,30, .9f, .9f, .9f, .4f, .4f, .4f, 1f,0.01F,0.00001f);

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
        texture.bind();
        engine.render();
    }

    public static void main(String[] args) {
        MappedObjectTransformer.register(MappedVertex.class);
        if(MappedObjectClassLoader.fork(Main.class, args)) {
            return;
        }
        ApplicationController ctrl = new ApplicationController();
        ctrl.createDisplay("Octree", 800, 600, false);
        ctrl.setSyncFps(60);
        ctrl.start( new Main() );
    }
}
