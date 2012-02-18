package de.funky_clan.coregl;

import java.nio.FloatBuffer;
import java.util.Arrays;

import de.funky_clan.coregl.geom.Frustum;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

/**

 */
public final class Camera {
    private FloatBuffer transformMatrix;
    private float[] transform;
    private float[] transformRot;
    private FloatBuffer viewMatrix;

    private Frustum frustum;

    public Camera(float x, float y, float z) {

        transform     = new float[16];
        transformRot  = new float[16];
        transform[0]  =  1.0f;
        transform[5]  =  1.0f;
        transform[10] = -1.0f;
        transform[15] =  1.0f;
        transform[12] =  x;
        transform[13] =  y;
        transform[14] =  z;

        transformMatrix = BufferUtils.createFloatBuffer(16);
        viewMatrix      = BufferUtils.createFloatBuffer(16);

        frustum         = new Frustum();
    }

    public float getX() {
        return transform[12];
    }
    public float getY() {
        return transform[13];
    }
    public float getZ() {
        return transform[14];
    }

    public void setView() {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        viewMatrix.rewind();
        viewMatrix.put(new float[]{
            transform[0], transform[4], transform[8], 0,
            transform[1], transform[5], transform[9], 0,
            transform[2], transform[6], transform[10], 0,

            -(transform[0] * transform[12] +
                transform[1] * transform[13] +
                transform[2] * transform[14]),

            -(transform[4] * transform[12] +
                transform[5] * transform[13] +
                transform[6] * transform[14]),

            //add a - like above for non-inverted z-axis
            -(transform[8] * transform[12] +
                transform[9] * transform[13] +
                transform[10] * transform[14]), 1
        });
        viewMatrix.rewind();
        GL11.glLoadMatrix(viewMatrix);
        viewMatrix.rewind();
        frustum.extract(viewMatrix);
    }

    public void moveLoc( float x, float y, float z, float distance ) {
        float dx = x*transform[0] + y*transform[4] + z*transform[8];
        float dy = x*transform[1] + y*transform[5] + z*transform[9];
        float dz = x*transform[2] + y*transform[6] + z*transform[10];
        transform[12] += dx*distance;
        transform[13] += dy*distance;
        transform[14] += dz*distance;
    }

    public void moveGlob( float x, float y, float z, float distance ) {
        transform[12] += x*distance;
        transform[13] += y*distance;
        transform[14] += z*distance;
    }

    public void rotateLoc( float deg, float x, float y, float z ) {
        transformMatrix.rewind();
        transformMatrix.put(transform);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        transformMatrix.rewind();
        GL11.glLoadMatrix(transformMatrix);
        GL11.glRotatef(deg, x, y, z);
        transformMatrix.rewind();
        GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, transformMatrix);
        GL11.glPopMatrix();
        transformMatrix.get(transform);
    }

    public void rotateGlob( float deg, float x, float y, float z ) {
        float dx = x*transform[0] + y*transform[1] + z*transform[2];
        float dy = x*transform[4] + y*transform[5] + z*transform[6];
        float dz = x*transform[8] + y*transform[9] + z*transform[10];
        rotateLoc( deg, dx, dy, dz);
    }

    public Frustum getFrustum() {
        return frustum;
    }

    public void lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ ) {
        Vector3f eye    = new Vector3f(eyeX,    eyeY,    eyeZ);
        Vector3f lookAt = new Vector3f(centerX, centerY, centerZ);
        Vector3f up     = new Vector3f(upX,     upY,     upZ);

        Vector3f x = new Vector3f();
        Vector3f y = new Vector3f();
        Vector3f z = new Vector3f();
        Vector3f.sub(eye, lookAt, z); z = (Vector3f) z.normalise();
        Vector3f.cross(up, z, x);     x = (Vector3f) x.normalise();
        Vector3f.cross(z, x, y);      y = (Vector3f) y.normalise();

        transform[0] = x.getX();
        transform[1] = x.getY();
        transform[2] = x.getZ();
        transform[3] = 0;

        transform[4] = y.getX();
        transform[5] = y.getY();
        transform[6] = y.getZ();
        transform[7] = 0;

        transform[8]  = z.getX();
        transform[9]  = z.getY();
        transform[10] = z.getZ();
        transform[11] = 0;

        transform[12] = eyeX;
        transform[13] = eyeY;
        transform[14] = eyeZ;
    }

    public void project( float x, float y, float z ) {
        float forwardX = transform[8];
        float forwardY = transform[9];
        float forwardZ = transform[10];
        float ex = transform[12];
        float ey = transform[13];
        float ez = transform[14];
        float nx = x-ex;
        float ny = y-ey;
        float nz = z-ez;
        float dist = (float) Math.sqrt(nx*nx+ny*ny+nz*nz);
        nx /= dist;
        ny /= dist;
        nz /= dist;
        lookAt( ex, ey, ez, ex-forwardX, ey-forwardY, ez-forwardZ, -nx, -ny, -nz);
    }
}