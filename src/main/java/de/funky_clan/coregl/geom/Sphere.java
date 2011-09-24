package de.funky_clan.coregl.geom;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public final class Sphere {
    private float x;
    private float y;
    private float z;
    private float radius;

    public Sphere() {
    }

    public Sphere(float x, float y, float z, float radius) {
        this.x      = x;
        this.y      = y;
        this.z      = z;
        this.radius = radius;
    }

    public void setPosition(float x, float y, float z) {
        this.x      = x;
        this.y      = y;
        this.z      = z;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean containsPoint( float x, float y, float z) {
        float dist = (x-this.x)*(x-this.x) + (y-this.y)*(y-this.y) +(z-this.z)*(z-this.z);
        return dist < radius*radius;
    }
    @Override
    public String toString() {
        return "["+x+", "+y+", "+z + "], r=" + radius;
    }

    public Halfspace sphereInSphere(Sphere other) {
        float distSq = (other.x-this.x)*(other.x-this.x) + (other.y-this.y)*(other.y-this.y) +(other.z-this.z)*(other.z-this.z);
        float radiusSq = (radius + other.getRadius())*(radius + other.getRadius());
        if( distSq > radiusSq ) {
            return Halfspace.OUTSIDE;
        }
        if( radius>=other.radius ) {
            radiusSq = (radius-other.radius)*(radius-other.radius);
            if( distSq<=radiusSq ) {
                return Halfspace.INSIDE;
            }
        }

        return Halfspace.INTERSECT;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
