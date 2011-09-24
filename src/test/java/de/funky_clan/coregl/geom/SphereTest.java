package de.funky_clan.coregl.geom;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public class SphereTest {
    @Test
    public void testContainsSphere() {
        Sphere a = new Sphere(0,0,0, 1);

        assertEquals(Halfspace.INSIDE, a.sphereInSphere(new Sphere(0,0,0, 1)));
        assertEquals(Halfspace.INSIDE, new Sphere(1,0,0, 2).sphereInSphere(a));
        assertEquals(Halfspace.INSIDE, a.sphereInSphere(new Sphere(0.8f, 0, 0, 0.2f)));
        assertEquals(Halfspace.OUTSIDE, a.sphereInSphere(new Sphere(2.2f, 0, 0, 0.2f)));
        assertEquals(Halfspace.INTERSECT, new Sphere(0,0,0,0.5f ).sphereInSphere(new Sphere(1,0,0, 0.5f)));
        assertEquals(Halfspace.INTERSECT, a.sphereInSphere(new Sphere(1, 0, 0, 1)));
        assertEquals(Halfspace.INTERSECT, a.sphereInSphere(new Sphere(1, 0, 0, 1.1f)));
        assertEquals(Halfspace.INTERSECT, a.sphereInSphere(new Sphere(1,0,0, 2)));
        assertEquals(Halfspace.INSIDE, a.sphereInSphere(new Sphere(0.5f,0,0, 0.5f)));

    }
}
