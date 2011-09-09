package de.funky_clan.octree;

import de.funky_clan.coregl.GameWindow;
import de.funky_clan.coregl.State;
import de.funky_clan.voxel.VoxelEngine;
import de.funky_clan.voxel.data.OctreeNode;
import de.funky_clan.voxel.renderer.OctreeRenderer;
import de.funky_clan.octree.generators.SphereGenerator;
import de.funky_clan.octree.minecraft.SchematicLoader;
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

    private OctreeNode octree;
    private OctreeRenderer octreeRenderer;

    private float angle;

    public void init(GameWindow window) throws IOException {
        engine = new VoxelEngine();
        engine.setFpsControl(true);
        engine.setShowInfo(true);
        engine.init(window);

        engine.getCamera().lookAt(0, 1, 0, 1,1,1, 0, 1, 0);

//        SchematicLoader loader = new SchematicLoader();
//        tree = loader.load("new_skull_hollow.schematic");
        SphereGenerator s = new SphereGenerator();
        octree = new OctreeNode(0,0,0,1<<30);
        SchematicLoader loader = new SchematicLoader();
        loader.load(octree, "Destiny.schematic");
//        octree.setPixel(128,128,128,100);
        octree.setPixel(1,1,1,100);
        octree.setPixel(2,1,1,100);
        octree.setPixel(1,2,1,100);
        octree.setPixel(1,1,2,100);
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
        octreeRenderer   = new OctreeRenderer(engine.getRenderer(), octree);

        engine.getLighting().createLight(0,0,0, .4f, .4f, .4f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
        engine.getLighting().createLight(30,30,30, .9f, .9f, .9f, .4f, .4f, .4f, 1f,0.01F,0.00001f);
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
    }

    public void render(GameWindow window, int delta) {
        engine.beginRender(delta);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST );
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST );
//        dirt.bind();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

//        int white = Color.rgba(new float[]{1, 1, 1, 1});
//        for (int i = 0; i < 6; i++) {
//            cubeRenderer.addCubeFace(i,0,0, 1, white, i);
//
//        }
//        triRenderer.addTriangle(new float[][]{{0, 0, 0}, {1, 1, 0}, {0, 1, 0}}, new float[][]{{0, 0}, {1, 1}, {0, 1}}, white, new float[]{1, 1, 0});
//        triRenderer.addTriangle(new float[][]{{0,0,0}, {1,0,0}, {1,1,0}}, new float[][]{{0,0}, {1,1}, {0,1}}, Color.rgba(new float[]{1,0,0,1}), new float[]{1,1,1});

//        cubeRenderer.addCube(0, 0, 1, 1, white);
        octreeRenderer.render(octree, engine.getCamera());

        engine.endRender(delta);
    }
}
