package de.funky_clan.coregl;

import de.funky_clan.coregl.renderer.BufferedRenderer;
import de.funky_clan.coregl.renderer.FontRenderer;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
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
    protected long frameStartTime;

    private GameWindow window;
    private boolean fpsControl;
    private boolean showInfo;
    private long lastFps;
    private int  fps;
    private int  recordedFps;

    public void init(GameWindow window) {
        this.window = window;
        camera = new Camera(10,10,10);
        fontRenderer     = new FontRenderer(window);
        renderer = new BufferedRenderer( GL11.GL_FLOAT, GL11.GL_UNSIGNED_BYTE, GL11.GL_FLOAT);
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
        updateFps();

        lighting.doLighting(camera.getX(), camera.getY(), camera.getZ());
        camera.setView();
        renderer.prepare();
    }

    private void updateFps() {
        frameStartTime = Sys.getTime();

        if( frameStartTime - lastFps > 1000 ) {
            recordedFps = fps;
            lastFps     = frameStartTime;
            fps = 0;
        }
        fps++;
    }

    public void endRender( int delta ) {
        Display.sync(90);
        if( isShowInfo() ) {
            fontRenderer.print(window, 10, 10, String.format("FPS: %d", recordedFps));
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
        float mod = 1;
        if( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ) {
            mod = 10;
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_W) ) {
            camera.moveLoc(0,0,-1,delta/100.f*mod);
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_S) ) {
            camera.moveLoc(0,0,1,delta/100.f*mod);
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_A) ) {
            camera.moveLoc(-1,0,0,delta/100.f*mod);
        }
        if( Keyboard.isKeyDown(Keyboard.KEY_D) ) {
            camera.moveLoc(1,0,0,delta/100.f*mod);
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
