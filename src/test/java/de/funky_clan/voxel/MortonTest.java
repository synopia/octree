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
    public void testDilate( ) {
        int max = (1 << 21) - 1;
        assertDilate(max, max, max);
        assertDilate(max, max-1, max-1);
        assertDilate(0, 0, 0);
        assertDilate(max, 0, 0);
        assertDilate(0, max, 0);
        assertDilate(0, 0, max);
    }

    private void assertDilate( int x, int y, int z ) {
        long dilX = Morton.dilate(x);
        long dilY = Morton.dilate(y);
        long dilZ = Morton.dilate(z);
        long morton = Morton.combine(dilX, dilY, dilZ);
        Assert.assertEquals( decBin(dilX), decBin(Morton.extract(morton, 0)));
        Assert.assertEquals( decBin(dilY<<1), decBin(Morton.extract(morton, 1)));
        Assert.assertEquals( decBin(dilZ<<2), decBin(Morton.extract(morton, 2)));
    }
    
    private String decBin( long v ) {
        return v+" => "+new BigInteger(v + "").toString(2);
    }
   
}
