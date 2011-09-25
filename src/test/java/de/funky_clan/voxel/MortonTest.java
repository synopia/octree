package de.funky_clan.voxel;

import de.funky_clan.voxel.data.Octree;
import de.funky_clan.voxel.data.OctreeNode;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author synopia
 */
public class MortonTest {
    @Test
    public void test() {
        OctreeNode root = new Octree(0,0,0, 1<<21).getRoot();
        System.out.println(root.getChunk(0,0,0));
        System.out.println(root.getChunk(0,0,32));
        long[] morton = Morton.mortonCode(new long[]{1, 2, 3});
        System.out.println(Arrays.toString(morton));
        System.out.println(Arrays.toString(Morton.mortonCode(new long[]{0, 0, 0})));
        System.out.println(Arrays.toString(Morton.mortonCode(new long[]{0, 0, 1})));
        System.out.println(Arrays.toString(Morton.mortonCode(new long[]{0, 1, 0})));
        System.out.println(Arrays.toString(Morton.mortonCode(new long[]{1, 0, 0})));
        System.out.println(Arrays.toString(Morton.mortonCode(new long[]{1, 1, 2})));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{0,0, 1})[0]+"").toString(2));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{0,1, 0})[0]+"").toString(2));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{1,0, 0})[0]+"").toString(2));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{1,1, 0})[0]+"").toString(2));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{1,1, 16})[0]+"").toString(2));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{0,0, 5})[0]+"").toString(2));
        System.out.println(new BigInteger(Morton.mortonCode(new long[]{0, 0, 6})[0] + "").toString(2));
    }
}
