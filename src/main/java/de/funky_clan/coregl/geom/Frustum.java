package de.funky_clan.coregl.geom;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * @author synopia
 */
public final class Frustum {
    private Plane planes[] = new Plane[6];
    private FloatBuffer modelViewMatrix;
    private FloatBuffer projectionMatrix;
    private FloatBuffer viewMatrix;

    public Frustum() {
        modelViewMatrix = BufferUtils.createFloatBuffer(16);
        projectionMatrix = BufferUtils.createFloatBuffer(16);
        viewMatrix = BufferUtils.createFloatBuffer(16);

        for (int i = 0; i < planes.length; i++) {
            planes[i] = new Plane();
        }
    }

    public Halfspace sphereInFrustum( Sphere sphere ) {
        float distance;
        Halfspace result = Halfspace.INSIDE;
        for (int i = 0, planesLength = planes.length; i < planesLength; i++) {
            Plane plane = planes[i];
            distance = plane.distanceToPoint(sphere.getX(), sphere.getY(), sphere.getZ());
            if (distance <= -sphere.getRadius()) {
                return Halfspace.OUTSIDE;
            } else if (distance < sphere.getRadius()) {
                result = Halfspace.INTERSECT;
            }
        }
        return result;
    }

    public float sphereInFrustum2( Sphere sphere ) {
        float distance = 0;
        for (int i = 0, planesLength = planes.length; i < planesLength; i++) {
            Plane plane = planes[i];
            distance = plane.distanceToPoint(sphere.getX(), sphere.getY(), sphere.getZ());
            if (distance <= -sphere.getRadius()) {
                return -1;
            }
        }
        return distance;
    }

/*
    public Halfspace boxInFrustum( Cube cube) {
        Halfspace result = Halfspace.INSIDE;
        for (Plane plane : planes) {
            if( plane.distanceToPoint(cube.getVertexP(plane.getNormal()))<0 ) {
                return Halfspace.OUTSIDE;
            } else if( plane.distanceToPoint(cube.getVertexN(plane.getNormal()))<0 ) {
                result = Halfspace.INTERSECT;
            }
        }
        return result;
    }
*/

    public void extract(FloatBuffer modelViewMatrix) {
        extractViewMatrix(modelViewMatrix);
        float[] matrix = new float[16];
        viewMatrix.get(matrix);

        // RIGHT PLANE
        planes[0].setA(matrix[ 3] - matrix[ 0]);
        planes[0].setB(matrix[ 7] - matrix[ 4]);
        planes[0].setC(matrix[11] - matrix[ 8]);
        planes[0].setD(matrix[15] - matrix[12]);

        // LEFT PLANE
        planes[1].setA(matrix[ 3] + matrix[ 0]);
        planes[1].setB(matrix[ 7] + matrix[ 4]);
        planes[1].setC(matrix[11] + matrix[ 8]);
        planes[1].setD(matrix[15] + matrix[12]);

        // BOTTOM PLANE
        planes[2].setA(matrix[ 3] + matrix[ 1]);
        planes[2].setB(matrix[ 7] + matrix[ 5]);
        planes[2].setC(matrix[11] + matrix[ 9]);
        planes[2].setD(matrix[15] + matrix[13]);

        // TOP PLANE
        planes[3].setA(matrix[ 3] - matrix[ 1]);
        planes[3].setB(matrix[ 7] - matrix[ 5]);
        planes[3].setC(matrix[11] - matrix[ 9]);
        planes[3].setD(matrix[15] - matrix[13]);

        // FAR PLANE
        planes[4].setA(matrix[ 3] - matrix[ 2]);
        planes[4].setB(matrix[ 7] - matrix[ 6]);
        planes[4].setC(matrix[11] - matrix[10]);
        planes[4].setD(matrix[15] - matrix[14]);

        // NEAR PLANE
        planes[5].setA(matrix[ 3] + matrix[ 2]);
        planes[5].setB(matrix[ 7] + matrix[ 6]);
        planes[5].setC(matrix[11] + matrix[10]);
        planes[5].setD(matrix[15] + matrix[14]);

        for (Plane plane : planes) {
            plane.normalize();
        }
    }

    private void extractViewMatrix(FloatBuffer modelViewMatrix) {
        projectionMatrix.rewind();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);

        projectionMatrix.rewind();
        viewMatrix.rewind();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadMatrix(projectionMatrix);
        GL11.glMultMatrix(modelViewMatrix);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, viewMatrix );
        GL11.glPopMatrix();

        viewMatrix.rewind();
    }
}
