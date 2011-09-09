package de.funky_clan.coregl;

/**
 * @author synopia
 */
public class Color {
    public static int rgba( int r, int g, int b, int a ) {
        return (r << 24) | (g << 16) | (b << 8) | a;
    }
    public static int rgba( int values[]) {
        return rgba( values[0], values[1], values[2], values[3] );
    }

    public static int red( int rgb ) {
        return (rgb >> 24) & 0xff;
    }
    public static int green( int rgb ) {
        return (rgb >> 16) & 0xff;
    }
    public static int blue( int rgb ) {
        return (rgb >> 8) & 0xff;
    }
    public static int alpha( int rgb ) {
        return rgb & 0xff;
    }

    public static int[] toInt(float []values) {
        return toInt( values[0], values[1], values[2], values[3] );
    }
    public static int[] toInt( float r, float g, float b, float a ) {
        return new int[]{  (int)(r*255.f), (int)(g*255.f), (int)(b*255.f), (int)(a*255.f) };
    }
    public static float[] toFloat( int rgba ) {
        return new float[]{ red(rgba)/255.f, green(rgba)/255.f, blue(rgba)/255.f, alpha(rgba)/255.f};
    }

    public static int rgba( float [] values ) {
        return rgba( values[0], values[1], values[2], values[3] );
    }
    public static int rgba( float r, float g, float b, float a ) {
        return rgba( (int)(r*255.f), (int)(g*255.f), (int)(b*255.f), (int)(a*255.f));
    }
}
