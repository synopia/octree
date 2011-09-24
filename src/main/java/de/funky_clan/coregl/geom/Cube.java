package de.funky_clan.coregl.geom;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public final class Cube {
    private float x;
    private float y;
    private float z;
    private float halfSide;

    public Cube(float x, float y, float z, float halfSide) {
        this.x      = x;
        this.y      = y;
        this.z      = z;
        this.halfSide = halfSide;
    }

/*
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
*/

/*
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
*/

    public void setPosition(float x, float y, float z) {
        this.x      = x;
        this.y      = y;
        this.z      = z;
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

    public float getHalfSide() {
        return halfSide;
    }

    public void setHalfSide(float halfSide) {
        this.halfSide = halfSide;
    }

    public Halfspace containsPoint( float x, float y, float z) {
        Halfspace result = Halfspace.OUTSIDE;
        if( this.x-halfSide<=x && this.y-halfSide<=y && this.z-halfSide<=z &&
            this.x+halfSide>=x && this.y+halfSide>=y && this.z+halfSide>=z ) {
            result = Halfspace.INSIDE;
        }
        return result;
    }
}
