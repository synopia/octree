package de.funky_clan.coregl;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.io.IOException;

/**
 * @author paul.fritsche@objectfab.de
 */
public class ApplicationController {
    private int         width;
    private int         height;
    private Application application = null;
    private TextureLoader textureLoader;
    private int syncFps;

    public void createDisplay( String title, int width, int height, boolean fullscreen ) {
        this.width  = width;
        this.height = height;
        textureLoader = new TextureLoader();

        try {
            int currentBpp = Display.getDisplayMode().getBitsPerPixel();
            DisplayMode mode = findDisplayMode( width, height, currentBpp );
            if( mode==null ) {
                Sys.alert( "Error", "800x600x" + currentBpp + " display mode is not available");
            }

            Display.setTitle( title );
            Display.setDisplayMode( mode );
            Display.setFullscreen( fullscreen );
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Sys.alert( "Error", "Failed: " + e.getMessage() );
        }
    }

    public void start( Application application ) {
        this.application = application;
        initGl();
        application.init(this);
        loop();
    }

    protected void loop() {
        boolean running  = true;
        long    lastLoop = getTime();

        while( running ) {
            int delta = (int)( getTime() - lastLoop );
            lastLoop  = getTime();

            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );

            int remainder = delta % 10;
            int step      = delta / 10;
            for( int i=0; i<step; i++ ) {
                application.update(10);
            }
            if( remainder != 0) {
                application.update(remainder);
            }

            application.render();

            Display.update();

            if( syncFps>0 ) {
                Display.sync(syncFps);
            }


            if( Display.isCloseRequested() ) {
                running = false;
            }

        }
    }

    public void initGl() {
        GL11.glEnable(     GL11.GL_TEXTURE_2D );
        GL11.glEnable(     GL11.GL_CULL_FACE );
        GL11.glEnable(     GL11.GL_DEPTH_TEST );
        GL11.glEnable(     GL11.GL_NORMALIZE);
        GL11.glDepthFunc(  GL11.GL_LEQUAL );
        GL11.glShadeModel( GL11.GL_FLAT );
        GL11.glCullFace(   GL11.GL_BACK );

        GL11.glMatrixMode( GL11.GL_PROJECTION );
        GL11.glLoadIdentity();
        GLU.gluPerspective( 60.0f, ((float)width) / ((float)height), 0.1f, 2000.f );
        GL11.glMatrixMode( GL11.GL_MODELVIEW );
        GL11.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_FASTEST);
        GL11.glHint( GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST );

        Mouse.setGrabbed( true );
    }

    public void enterOrtho() {
        // store the current state of the renderer
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS );
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();

        // now enter orthographic projection
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, -1, 1);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public void leaveOrtho() {
        // restore the state of the renderer
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private DisplayMode findDisplayMode( int width, int height, int bpp ) throws LWJGLException {
        DisplayMode[] modes = Display.getAvailableDisplayModes();
        DisplayMode   mode  = null;

        for (DisplayMode displayMode : modes) {
            if ((displayMode.getBitsPerPixel() == bpp) && (displayMode.getWidth() == width) && (displayMode.getHeight() == height)) {
                mode = displayMode;
                break;
            }
        }
        return mode;
    }

    protected long getTime() {
        return Sys.getTime();
    }

    public Texture getTexture( String name ) {
        try {
            return textureLoader.getTexture(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSyncFps(int syncFps) {
        this.syncFps = syncFps;
    }
}
