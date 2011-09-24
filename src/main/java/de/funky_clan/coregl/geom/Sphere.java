package de.funky_clan.coregl.geom;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public class Sphere {
    private Vector3f position;
    private float radius;

    public Sphere() {
    }

    public Sphere(Vector3f position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean containsPoint( Vector3f pt ) {
        float dist = (pt.getX()-position.getX())*(pt.getX()-position.getX()) + (pt.getY()-position.getY())*(pt.getY()-position.getY()) +(pt.getZ()-position.getZ())*(pt.getZ()-position.getZ());
        return dist < radius*radius;
    }
    @Override
    public String toString() {
        return position + ", r=" + radius;
    }

    public Halfspace sphereInSphere(Sphere other) {
        Vector3f diff = new Vector3f();
        Vector3f.sub(position, other.getPosition(), diff);
        float radiusSq = (radius + other.getRadius())*(radius + other.getRadius());
        float distSq = diff.lengthSquared();
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
}
