package de.funky_clan.voxel.data;

import org.junit.Test;

/**
 * @author synopia
 */
public class OctreeNodeTest {
    @Test
    public void test() {
        OctreeNode root = new Octree(0,0,0, 1<<30).getRoot();
        System.out.println(Runtime.getRuntime().freeMemory());
        for (int i = 0; i < 100000; i++) {
            if( i%10000==0 ) System.out.println(i);
              root.setPixel(i * 32, 0, 0, 1);
        }
        for (int i = 100000-1; i >=0 ; i--) {
            if( i%10000==0 ) System.out.println(i);
              root.setPixel(i * 32, 0, 0, 1);
        }
        System.out.println(Runtime.getRuntime().freeMemory());
        System.out.println(Chunk.COUNT);
    }
}
