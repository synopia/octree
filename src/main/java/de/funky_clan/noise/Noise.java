package de.funky_clan.noise;

import java.util.Random;

/**
 * @author synopia
 */
public class Noise {
    protected int dimensions;
    protected short[] nMap;
    protected float[][] nBuffer;
    protected Random random;

    public Noise( int dimensions, int seed ) {
        random = new Random(seed);
        nMap = new short[256];
        nBuffer = new float[256][dimensions];
        this.dimensions = dimensions;
        for (int i = 0; i < 256; i++) {
            nMap[i] = (short) i;
            for (int j = 0; j < dimensions; j++) {
                nBuffer[i][j] = random.nextFloat()-0.5f;
            }
            normalize( nBuffer[i], dimensions);
        }
        for (int i = 255; i>=0; i--) {
            int j = random.nextInt(255);
            short t = nMap[i];
            nMap[i] = nMap[j];
            nMap[j] = t;
        }
    }
    public float noise( float[] f ) {
        int n[] = new int[dimensions];
        float r[] = new float[dimensions];
        float w[] = new float[dimensions];
        for (int i = 0; i < dimensions; i++) {
            n[i] = (int) Math.floor(f[i]);
            r[i] = f[i] - n[i];
            w[i] = cubic(r[i]);
        }

        float value = 0;
        switch (dimensions) {
            case 1:
                value = lerp( lattice(n[0], r[0]), lattice(n[0]+1, r[0]-1), w[0]);
                break;
            case 2:
                value = lerp(lerp(lattice(n[0], r[0], n[1], r[1]),
                        lattice(n[0]+1, r[0]-1, n[1], r[1]),
                        w[0]),
                        lerp(lattice(n[0], r[0], n[1]+1, r[1]-1),
                                lattice(n[0]+1, r[0]-1, n[1]+1, r[1]-1),
                                w[0]),
                        w[1]);
                break;
            case 3:
                value = lerp(lerp(lerp(lattice(n[0], r[0], n[1], r[1], n[2], r[2]),
                        lattice(n[0]+1, r[0]-1, n[1], r[1], n[2], r[2]),
                        w[0]),
                        lerp(lattice(n[0], r[0], n[1]+1, r[1]-1, n[2], r[2]),
                                lattice(n[0]+1, r[0]-1, n[1]+1, r[1]-1, n[2], r[2]),
                                w[0]),
                        w[1]),
                        lerp(lerp(lattice(n[0], r[0], n[1], r[1], n[2]+1, r[2]-1),
                                lattice(n[0]+1, r[0]-1, n[1], r[1], n[2]+1, r[2]-1),
                                w[0]),
                                lerp(lattice(n[0], r[0], n[1]+1, r[1]-1, n[2]+1, r[2]-1),
                                        lattice(n[0]+1, r[0]-1, n[1]+1, r[1]-1, n[2]+1, r[2]-1),
                                        w[0]),
                                w[1]),
                        w[2]);
                break;
        }
        return clamp( -0.99999f, 0.99999f, value);
    }

    protected float lattice( int ix, float fx) {
        return lattice(ix, fx, 0, 0, 0, 0, 0, 0);
    }
    protected float lattice( int ix, float fx, int iy, float fy ) {
        return lattice(ix, fx, iy, fy, 0, 0, 0, 0);
    }
    protected float lattice( int ix, float fx, int iy, float fy, int iz, float fz ) {
        return lattice(ix, fx, iy, fy, iz, fz, 0, 0);
    }
    protected float lattice( int ix, float fx, int iy, float fy, int iz, float fz, int iw, float fw ) {
        int n[] = new int[]{ix, iy, iz, iw};
        float f[] = new float[]{fx, fy, fz, fw};
        int index = 0;
        for( int i=0; i<dimensions; i++ ) {
            index = nMap[(index+n[i])&0xff];
        }
        float value = 0;
        for( int i=0; i<dimensions; i++ ) {
            value += nBuffer[index][i] + f[i];
        }
        return value;
    }

    public static void normalize( float[] f, int n ) {
        float magnitude = 0;
        for (int i = 0; i < n; i++) {
            magnitude += f[i]*f[i];
        }
        magnitude = 1.f/(float) Math.sqrt(magnitude);
        for (int i = 0; i < n; i++) {
            f[i] *= magnitude;
        }
    }

    public static float cubic( float a ) {
        return a*a*(3-2*a);
    }
    public static float lerp( float a, float b, float x) {
        return a + x * (b-a);
    }
    public static float clamp( float a, float b, float x ) {
        return x<a ? a : (x>b ? b : x);
    }
}
