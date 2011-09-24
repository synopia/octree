package de.funky_clan.coregl;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.FontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

/**
 * @author synopia
 */
public abstract class BaseEngine {
    protected Camera camera;
    protected FontRenderer fontRenderer;
    protected BufferedRenderer renderer;
    protected Lighting lighting;

    private GameWindow window;
    private boolean fpsControl;
    private boolean showInfo;

    public void init(GameWindow window) {
        this.window = window;
        camera = new Camera(10,10,10);
        fontRenderer     = new FontRenderer(window);
        renderer = new BufferedRenderer(0x4000, GL11.GL_FLOAT, GL11.GL_UNSIGNED_BYTE, GL11.GL_FLOAT);
        lighting = new Lighting();
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void update( int delta ) {
        if( fpsControl ) {
            moveCamera(delta);
        }
        lighting.update(delta);
    }

    public void beginRender( int delta ) {
        lighting.doLighting(camera.getPosition());
        camera.setView();
        renderer.prepare();
    }

    public void endRender( int delta ) {
        if( isShowInfo() ) {
            float fps = 1000.f / delta;
            fontRenderer.print(window, 10, 10, String.format("FPS: %.2f", fps));
            ArrayList<String> infos = getDebugInfo();
            int y = 20;
            for (String info : infos) {
                fontRenderer.print(window, 10, y, info );
                y += 10;
            }
        }
    }

    protected ArrayList<String> getDebugInfo() {
        return renderer.getDebugInfos();
    }

    protected void moveCamera(int delta) {
        boolean changedPosition = false;
        float mod = 1;
        if( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ) {
            mod = 10;
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_W) ) {
            camera.moveLoc(0,0,-1,delta/100.f*mod);
            changedPosition = true;
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_S) ) {
            camera.moveLoc(0,0,1,delta/100.f*mod);
            changedPosition = true;
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_A) ) {
            camera.moveLoc(-1,0,0,delta/100.f*mod);
            changedPosition = true;
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_D) ) {
            camera.moveLoc(1,0,0,delta/100.f*mod);
            changedPosition = true;
        }
        if( Mouse.isButtonDown(0) ) {
        }
        if( Mouse.isButtonDown(1) ) {
        }
        int mouseDx = Mouse.getDX();
        int mouseDy = Mouse.getDY();
        if( mouseDx!=0 ) {
            camera.rotateGlob( -mouseDx/10.f,0,1,0 );
        }
        if( mouseDy!=0 ) {
            camera.rotateLoc( mouseDy/10.f,1,0,0 );
        }
        if( changedPosition ) {
            Vector3f position = camera.getPosition();
            System.out.format("%f, %f, %f\n",position.getX(), position.getY(), position.getZ());
        }
    }

    public boolean isFpsControl() {
        return fpsControl;
    }

    public void setFpsControl(boolean fpsControl) {
        this.fpsControl = fpsControl;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public Camera getCamera() {
        return camera;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    public BufferedRenderer getRenderer() {
        return renderer;
    }

    public Lighting getLighting() {
        return lighting;
    }
}
