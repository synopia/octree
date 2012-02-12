package de.funky_clan.voxel;

import de.funky_clan.octree.Morton;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author synopia
 */
public class MortonTest {
    @Test
    public void testMorton() {
        Set<Long> set = new LinkedHashSet<Long>();
        int max = (1 << 20)-1;

        int rounds = 16;
        for( int i=0; i<rounds; i++ ) {

            set.add(Morton.mortonCode(0,0,0, i));
            set.add(Morton.mortonCode(0,0,1, i));
            set.add(Morton.mortonCode(0,1,0, i));
            set.add(Morton.mortonCode(0,1,1, i));
            set.add(Morton.mortonCode(1,0,0, i));
            set.add(Morton.mortonCode(1,0,1, i));
            set.add(Morton.mortonCode(1,1,0, i));
            set.add(Morton.mortonCode(1,1,1, i));
            set.add(Morton.mortonCode(1,1,1, i));
    
            set.add(Morton.mortonCode(0,0,0, i));
            set.add(Morton.mortonCode(0,0,max, i));
            set.add(Morton.mortonCode(0,max,0, i));
            set.add(Morton.mortonCode(0,max,max, i));
            set.add(Morton.mortonCode(max,0,0, i));
            set.add(Morton.mortonCode(max,0,max, i));
            set.add(Morton.mortonCode(max,max,0, i));
            set.add(Morton.mortonCode(max,max,max, i));
            set.add(Morton.mortonCode(max,max,max, i));
        }

        Assert.assertEquals(set.size(), rounds*15);
    }
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
