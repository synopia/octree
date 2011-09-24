package de.funky_clan.coregl.geom;

import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public final class Plane {
  private float a;
  private float b;
  private float c;
  private float d;

  public Plane() {
  }

  public Plane(float a, float b, float c, float d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public void normalize() {
    float mag = (float) Math.sqrt( a*a + b*b + c*c );
    a /= mag;
    b /= mag;
    c /= mag;
    d /= mag;
  }


  public float distanceToPoint( float x, float y, float z) {
    return a*x + b*y + c*z + d;
  }

  public Halfspace sideOfPoint( float x, float y, float z ) {
    float d = distanceToPoint(x, y, z);

    if( d<0 ) return Halfspace.OUTSIDE;
    if( d>0 ) return Halfspace.INSIDE;

    return Halfspace.ONSIDE;
  }

  public float getA() {
    return a;
  }

  public void setA(float a) {
    this.a = a;
  }

  public float getB() {
    return b;
  }

  public void setB(float b) {
    this.b = b;
  }

  public float getC() {
    return c;
  }

  public void setC(float c) {
    this.c = c;
  }

  public float getD() {
    return d;
  }

  public void setD(float d) {
    this.d = d;
  }
}
