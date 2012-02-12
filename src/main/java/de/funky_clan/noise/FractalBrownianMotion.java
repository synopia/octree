package de.funky_clan.noise;

/**
 * @author synopia
 */
public class FractalBrownianMotion extends Fractal {
    public FractalBrownianMotion(int dimensions, int seed, float h, float lacunarity) {
        super(dimensions, seed, h, lacunarity);
    }

    @Override
    public float calculate(float[] f, float octaves) {
        float value = 0;
        float temp[] = new float[dimensions];
        System.arraycopy(f, 0, temp, 0, temp.length);
        int i;
        for (i = 0; i < octaves; i++) {
            value += noise(temp)*exponent[i];
            for (int j = 0; j < dimensions; j++) {
                temp[j] *= lacunarity;
            }
        }
        octaves -= (int)octaves;
        if( octaves>DELTA ) {
            value += octaves*noise(temp)*exponent[i];
        }
        return clamp(-0.99999f, 0.99999f, value);
    }
}
