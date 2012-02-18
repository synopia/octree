package de.funky_clan.coregl;

import org.junit.Test;

/**
 * @author synopia
 */
public class CameraTest {
    @Test
    public void testCam() {
        Camera cam = new Camera(400,800,400);
        cam.lookAt(400, 2*400, 400, 1+400,2*400,400, 0,1,0);
        cam.project(400,400,400);
        cam.moveLoc(1,0,0,1);
        cam.project(400,400,400);

    }
}
