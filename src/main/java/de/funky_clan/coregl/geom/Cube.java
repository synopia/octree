package de.funky_clan.coregl.geom;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public class Cube {
    private Vector3f position;
    private float halfSide;

    public Cube(Vector3f position, float halfSide) {
        this.position = position;
        this.halfSide = halfSide;
    }

    public void getVertices( Vector3f[] corners ) {
        if( corners==null || corners.length<8 ) {
            throw new IllegalArgumentException("corners[] must not be null or short then 8!");
        }
        corners[0].set( position.getX() - halfSide, position.getY() - halfSide, position.getZ() - halfSide );
        corners[1].set( position.getX() + halfSide, position.getY() - halfSide, position.getZ() - halfSide );
        corners[2].set( position.getX() - halfSide, position.getY() + halfSide, position.getZ() - halfSide );
        corners[3].set( position.getX() + halfSide, position.getY() + halfSide, position.getZ() - halfSide );
        corners[4].set( position.getX() - halfSide, position.getY() - halfSide, position.getZ() + halfSide );
        corners[5].set( position.getX() + halfSide, position.getY() - halfSide, position.getZ() + halfSide );
        corners[6].set( position.getX() - halfSide, position.getY() + halfSide, position.getZ() + halfSide );
        corners[7].set( position.getX() + halfSide, position.getY() + halfSide, position.getZ() + halfSide );
    }

    public Vector3f getVertexP(Vector3f normal) {
        float nx = position.getX()-halfSide;
        float ny = position.getY()-halfSide;
        float nz = position.getZ()-halfSide;
        if( normal.getX()>=0 ) {
            nx = position.getX()+halfSide;
        }
        if( normal.getY()>=0 ) {
            ny = position.getY()+halfSide;
        }
        if( normal.getZ()>=0 ) {
            nz = position.getZ()+halfSide;
        }
        return new Vector3f(nx, ny, nz);
    }

    public Vector3f getVertexN(Vector3f normal) {
        float nx = position.getX()+halfSide;
        float ny = position.getY()+halfSide;
        float nz = position.getZ()+halfSide;
        if( normal.getX()>=0 ) {
            nx = position.getX()-halfSide;
        }
        if( normal.getY()>=0 ) {
            ny = position.getY()-halfSide;
        }
        if( normal.getZ()>=0 ) {
            nz = position.getZ()-halfSide;
        }
        return new Vector3f(nx, ny, nz);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getHalfSide() {
        return halfSide;
    }

    public void setHalfSide(float halfSide) {
        this.halfSide = halfSide;
    }

    public Halfspace containsPoint( Vector3f pt ) {
        Halfspace result = Halfspace.OUTSIDE;
        if( position.x-halfSide<=pt.getX() && position.y-halfSide<=pt.getY() && position.z-halfSide<=pt.z &&
            position.x+halfSide>=pt.getX() && position.y+halfSide>=pt.getY() && position.z+halfSide>=pt.z ) {
            result = Halfspace.INSIDE;
        }
        return result;
    }
}
