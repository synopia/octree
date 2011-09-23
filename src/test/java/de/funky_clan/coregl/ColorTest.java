package de.funky_clan.coregl;

import de.funky_clan.coregl.Color;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 * @author synopia
 */
public class ColorTest {
    @Test
    public void testRGBA() {
        assertRGBA(0,0,0,0);
        assertRGBA(0,0,0,255);
        assertRGBA(0,0,255,0);
        assertRGBA(0,255,0,0);
        assertRGBA(255,0,0,0);
        assertRGBA(255,255,255,255);

        assertRGBA(0f,0f,0f,0f);
        assertRGBA(1f,1f,1f,1f);
    }

    private void assertRGBA( float r, float g, float b, float a ) {
        int rgba = Color.rgba(r, g, b, a);
        int[] ints = Color.toInt(r, g, b, a);
        assertEquals(ints[0], Color.red(rgba));
        assertEquals(ints[1], Color.green(rgba));
        assertEquals(ints[2], Color.blue(rgba));
        assertEquals(ints[3], Color.alpha(rgba));
    }
    private void assertRGBA( int r, int g, int b, int a ) {
        int rgba = Color.rgba(r, g, b, a);
        assertEquals( r, Color.red(rgba) );
        assertEquals( g, Color.green(rgba) );
        assertEquals( b, Color.blue(rgba) );
        assertEquals( a, Color.alpha(rgba) );
    }
}
