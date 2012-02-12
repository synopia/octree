package de.funky_clan.octree;

/**
 * @author synopia
 */
public class Morton {
    public static long mortonCode(int x, int y, int z){
        return mortonCode(x,y,z,0);
    }
    public static long mortonCode(int x, int y, int z, int add){
        return combine(dilate(x), dilate(y), dilate(z)) | ((long)add<<60);
    }

    public static long dilate( int input ) {
        long v = input;
        // Coding for 3 20-bit values
        v = (v | (v << 32)) & 0x0fff00000000ffffL; // 0b0000111111111111000000000000000000000000000000001111111111111111
        v = (v | (v << 16)) & 0x00ff0000ff0000ffL; // 0b0000000011111111000000000000000011111111000000000000000011111111
        v = (v | (v <<  8)) & 0x000f00f00f00f00fL; // 0b0000000000001111000000001111000000001111000000001111000000001111
        v = (v | (v <<  4)) & 0x00c30c30c30c30c3L; // 0b0000000011000011000011000011000011000011000011000011000011000011
        v = (v | (v <<  2)) & 0x0249249249249249L; // 0b0000001001001001001001001001001001001001001001001001001001001001

        return v;
    }
    
    public static long combine( long x, long y, long z ) {
        return x | (y<<1) | (z<<2);
    }
    public static long extract( long v, int i ) {
        return v & (0x1249249249249249L<<i);
    }
}
