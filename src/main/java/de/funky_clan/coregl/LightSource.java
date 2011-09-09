package de.funky_clan.coregl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * @author synopia
 */
public class LightSource implements Comparable<LightSource> {
    private float pos[] = new float[4];
    private float diffuse[] = new float[4];
    private float ambient[] = new float[4];
    private float specular[] = new float[4];
    private float fullDiffuse[] = new float[4];
    private float fullAmbient[] = new float[4];
    private float fullSpecular[] = new float[4];
    private float constantAttenuation;
    private float linearAttenuation;
    private float quadraticAttenuation;
    private float liveTime;
    private float totalTime;

    private float brightness;

    private FloatBuffer buffer = BufferUtils.createFloatBuffer(4);

    public LightSource(float[] diffuse, float[] ambient, float[] specular, float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
        this.diffuse = Arrays.copyOf(diffuse, 4);
        this.ambient = Arrays.copyOf(ambient, 4);
        this.specular = Arrays.copyOf(specular, 4);
        this.fullDiffuse = Arrays.copyOf(diffuse, 4);
        this.fullAmbient = Arrays.copyOf(ambient, 4);
        this.fullSpecular = Arrays.copyOf(specular, 4);
        this.constantAttenuation = constantAttenuation;
        this.linearAttenuation = linearAttenuation;
        this.quadraticAttenuation = quadraticAttenuation;
    }

    public LightSource(float x, float y, float z,
                       float Dr, float Dg, float Db,
                       float Ar, float Ag, float Ab,
                       float Sr, float Sg, float Sb,
                       float cA, float lA, float qA ) {
        setPosition(x, y, z);

        diffuse[0] = Dr;
        diffuse[1] = Dg;
        diffuse[2] = Db;
        diffuse[3] = 1.f;

        ambient[0] = Ar;
        ambient[1] = Ag;
        ambient[2] = Ab;
        ambient[3] = 1.f;

        specular[0] = Sr;
        specular[1] = Sg;
        specular[2] = Sb;
        specular[3] = 1.f;

        constantAttenuation = cA;
        linearAttenuation   = lA;
        quadraticAttenuation = qA;
    }

    public void setPosition( float x, float y, float z ) {
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
        pos[3] = 1.f;
    }

    public void doLighting( Vector3f objPos ) {
        float dx = pos[0] - objPos.getX();
        float dy = pos[1] - objPos.getY();
        float dz = pos[2] - objPos.getZ();

        float quadraticAttenuation = dx*dx + dy*dy + dz*dz;
        float nx = 0;
        float ny = 0;
        float nz = 1;

        float distance = (float) Math.sqrt(quadraticAttenuation);
        dx = dx / distance;
        dy = dy / distance;
        dz = dz / distance;

        float diffRefl = nx*dx + ny*dy + nz*dz;
        brightness = diffRefl / quadraticAttenuation;
    }

    public boolean update( int delta ) {
        return true;
    }

    @Override
    public int compareTo(LightSource o) {
        float diff = o.brightness - this.brightness;
        if( diff<0 ) {
            return -1;
        }
        if( diff>0 ) {
            return 1;
        }
        return 0;
    }

    public void bind(int no) {
        int lightno = GL11.GL_LIGHT0 + no;
        GL11.glEnable(lightno);
        GL11.glLight(lightno, GL11.GL_POSITION, convert(pos) );
        GL11.glLight(lightno, GL11.GL_DIFFUSE, convert(diffuse) );
        GL11.glLight(lightno, GL11.GL_AMBIENT, convert(ambient));
        GL11.glLight(lightno, GL11.GL_SPECULAR, convert(specular));
        GL11.glLightf(lightno, GL11.GL_CONSTANT_ATTENUATION, constantAttenuation);
        GL11.glLightf(lightno, GL11.GL_LINEAR_ATTENUATION, linearAttenuation);
        GL11.glLightf(lightno, GL11.GL_QUADRATIC_ATTENUATION, quadraticAttenuation);
    }

    private FloatBuffer convert( float [] value ) {
        buffer.rewind();
        buffer.put(value);
        buffer.flip();
        return buffer;
    }

    @Override
    public String toString() {
        return pos[0]+", "+pos[1]+", "+pos[2];
    }

    public float getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(float liveTime) {
        this.liveTime = liveTime;
        this.totalTime = liveTime;
    }
}
