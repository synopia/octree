package de.funky_clan.octree;

/**
 * @author synopia
 */
public class Morton {
    public static long mortonCode(int x, int y, int z){
        return combine(dilate(x), dilate(y), dilate(z));
    }

    public static long dilate( int input ) {
        long v = input;
        v = (v | (v << 32)) & 0x7fff00000000ffffL; // 0b0111111111111111000000000000000000000000000000001111111111111111
        v = (v | (v << 16)) & 0x00ff0000ff0000ffL; // 0b0000000011111111000000000000000011111111000000000000000011111111
        v = (v | (v <<  8)) & 0x700f00f00f00f00fL; // 0b0111000000001111000000001111000000001111000000001111000000001111
        v = (v | (v <<  4)) & 0x30c30c30c30c30c3L; // 0b0011000011000011000011000011000011000011000011000011000011000011
        v = (v | (v <<  2)) & 0x1249249249249249L; // 0b0001001001001001001001001001001001001001001001001001001001001001
        return v;
    }
    
    public static long combine( long x, long y, long z ) {
        return x | (y<<1) | (z<<2);
    }
    public static long extract( long v, int i ) {
        return v & (0x1249249249249249L<<i);
    }
}
