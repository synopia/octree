package de.funky_clan.voxel;

/**
 * @author synopia
 */
public class Morton {
    public static long[] mortonCodeFull(long[] xyz){
        long[] ret = {0,0,0};
        long t;
        for(int i=0; i<3; i++){
            for(int j=0;j<3;j++){
                /*
                * Two notes here. We can only reasonably use the 63 most significant bits or the morton number would be 4 longs wide
                * This should be fine since getting to within 2x2x2 is pretty damn close.
                * Second, with a little trick math to get the number back into positive and then bits shifted around we can
                * sidestep a lot of problems introduced missing bitwise operations and 2's compliment signed integers
                * <p>
                * Here is how it works:
                * <br>
                * int/longs are stored in two's compliment. Wiki it then come back here.
                * <br>
                * Bitwise invert the numbers and don't think of them as signed numbers anymore, soon they won't be.
                * This leaves what was LONG_MIN (smallest negative number) as 0 and all the bits count naturally from 0000... to 11111.
                * <br>
                * Next we bitshift right (with >>> to shift in a zero not a 1). This is where they stop being "signed" numbers (even though the signed bit is still there, just inactive)
                * <p>
                * This means that all the bit shifting we do will produce a continuous progression of Morton Codes so we can later accurately estimate locality using just a sorted list
                * <p>
                * Also we can only eat 21 bits at a time from each number before we then scatter them to the return for the Morton code. Hence the inner loop.
                */
                t = (~xyz[i]) >>> (1+(21*j));
                /*
                * Another explanation, this one for the magic numbers below:
                * <br>
                * This moves the lower 21 bits so that they have two 0's between them, so we can interleave the other two numbers between them.
                * <br>
                * It works because at each step we OR the existing number with a bitshifted version of itself and then mask it so that only the correctly shifted bits
                * (and bits that should not have been shifted, that is why the | is there) survive.
                * <br>
                * Each magic number is crafted so that the bits that we have not intended to shift yet remain in their starting place, the bits we are shifting are caught in their new position, and the bits that were PREVIOUSLY shifted are caught in their new NEW (etc) position.
                * <br>
                * On a 64 bit number and a spacing of 2 this can only work on the lower 21 bits. Since we make the largest moves first down to the smallest in binary steps the smaller steps won't invade on the larger steps' moves
                * <br>
                * If that made no sense then just know that the brute force approach requires 20 bitshifts, 21 &'s, and 20 |'s or +'s
                */
                t = (t | t<<32) & 0x1f00000000ffffl;
                t = (t | t<<16) & 0x1f0000ff0000ffl;
                t = (t | t<<8) & 0x100f00f00f00f00fl;
                t = (t | t<<4) & 0x10c30c30c30c30c3l;
                t = (t | t<<2) & 0x1249249249249249l;
                ret[j] = ret[j] | (t << i); //the shift puts x into the "low" order interleaved bits, y in middle, and z at the top. so after all the loops the number's bits origins will be zyxzyxzyxzyx...
            }
        }
        return ret;
    }
    public static long mortonCode(long x, long y, long z){
        long ret = 0;
        long t;
        int j=0;
        int i=0;
        t = (~x) >>> (1+(21*j));
        t = (t | t<<32) & 0x1f00000000ffffl;
        t = (t | t<<16) & 0x1f0000ff0000ffl;
        t = (t | t<<8) & 0x100f00f00f00f00fl;
        t = (t | t<<4) & 0x10c30c30c30c30c3l;
        t = (t | t<<2) & 0x1249249249249249l;
        ret = ret | (t << i); //the shift puts x into the "low" order interleaved bits, y in middle, and z at the top. so after all the loops the number's bits origins will be zyxzyxzyxzyx...

        i++;
        t = (~y) >>> (1+(21*j));
        t = (t | t<<32) & 0x1f00000000ffffl;
        t = (t | t<<16) & 0x1f0000ff0000ffl;
        t = (t | t<<8) & 0x100f00f00f00f00fl;
        t = (t | t<<4) & 0x10c30c30c30c30c3l;
        t = (t | t<<2) & 0x1249249249249249l;
        ret = ret | (t << i); //the shift puts x into the "low" order interleaved bits, y in middle, and z at the top. so after all the loops the number's bits origins will be zyxzyxzyxzyx...

        i++;
        t = (~z) >>> (1+(21*j));
        t = (t | t<<32) & 0x1f00000000ffffl;
        t = (t | t<<16) & 0x1f0000ff0000ffl;
        t = (t | t<<8) & 0x100f00f00f00f00fl;
        t = (t | t<<4) & 0x10c30c30c30c30c3l;
        t = (t | t<<2) & 0x1249249249249249l;
        ret = ret | (t << i); //the shift puts x into the "low" order interleaved bits, y in middle, and z at the top. so after all the loops the number's bits origins will be zyxzyxzyxzyx...
        return ret;
    }
}
