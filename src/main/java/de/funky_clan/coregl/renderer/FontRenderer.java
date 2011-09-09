package de.funky_clan.coregl.renderer;

import de.funky_clan.coregl.GameWindow;
import de.funky_clan.coregl.Texture;
import org.lwjgl.opengl.GL11;

/**
 * @author synopia
 */
public class FontRenderer {
    private Texture fontTexture;
    private int fontListBase;

    public FontRenderer( GameWindow window ) {
        fontTexture = window.getTexture( "font.png" );
        int fontSizeX = 8;
        int fontSizeY = 12;

        float width  = fontTexture.getImageWidth() / fontTexture.getWidth();
        float height = fontTexture.getImageHeight() / fontTexture.getHeight();

        fontListBase = GL11.glGenLists( 96 );

        float unitX = (float)fontSizeX / width;
        float unitY = (float)fontSizeY / height;

        for( int i=0; i<96; i++ ) {
            int x = i%16 * fontSizeX;
            int y = i/16 * fontSizeY;

            float texU = (float)x / width;
            float texV = (float)y / height;

            GL11.glNewList( fontListBase + i, GL11.GL_COMPILE );
            {
                GL11.glBegin( GL11.GL_QUADS );
                {
                    GL11.glTexCoord2f( texU, texV );
                    GL11.glVertex2i( 0, 0 );

                    GL11.glTexCoord2f( texU, texV + unitY);
                    GL11.glVertex2i( 0, fontSizeY );

                    GL11.glTexCoord2f( texU + unitX, texV + unitY);
                    GL11.glVertex2i( fontSizeX, fontSizeY );

                    GL11.glTexCoord2f( texU + unitX, texV );
                    GL11.glVertex2i( fontSizeX, 0 );
                }
                GL11.glEnd();
                GL11.glTranslatef( fontSizeX, 0, 0 );
            }
            GL11.glEndList();
        }
    }

    public void print( GameWindow window, int x, int y, String text ) {
        window.enterOrtho();
        GL11.glEnable( GL11.GL_BLEND );
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        fontTexture.bind();
        GL11.glTranslatef(x, y, 0);
        for( int i=0; i<text.length(); i++ ) {
            GL11.glCallList( fontListBase + (text.charAt(i)-32));
        }
        window.leaveOrtho();
    }
}
