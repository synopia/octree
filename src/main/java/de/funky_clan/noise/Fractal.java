package de.funky_clan.noise;

/**
 * @author synopia
 */
public abstract class Fractal extends Noise {
    public static final int MAX_OCTAVES = 128;
    public static final float DELTA = 1e-6f;

    protected float h;
    protected float lacunarity;
    protected float exponent[];

    protected Fractal(int dimensions, int seed, float h, float lacunarity) {
        super(dimensions, seed);
        this.h = h;
        this.lacunarity = lacunarity;
        float f = 1;
        exponent = new float[MAX_OCTAVES];
        for (int i = 0; i < MAX_OCTAVES; i++) {
            exponent[i] = (float) Math.pow(f, -h);
            f *= lacunarity;
        }
    }

    public abstract float calculate( float [] f, float octaves );
}
