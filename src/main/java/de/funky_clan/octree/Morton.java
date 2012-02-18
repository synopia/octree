package de.funky_clan.octree;

/**
 * @author synopia
 */
public class Morton {
    public static final int MORTON_BITS = 19;

    public static long mortonCode(int x, int y, int z){
        return mortonCode(x,y,z,0);
    }
    public static long mortonCode(int x, int y, int z, int add){
        if( x<0 || x>=(1<<MORTON_BITS) ||
                y<0 || y>=(1<<MORTON_BITS) ||
                z<0 || z>=(1<<MORTON_BITS) ||
                add<0 || add>=(1<<(64-MORTON_BITS*3))
                ) {
            throw new IllegalStateException("Out of range "+x+", "+y+", "+z+", "+add);
        }
        return ((long)add<<(3*MORTON_BITS)) | combine(dilate(x), dilate(y), dilate(z));

    }

    public static long dilate( int input ) {
        long v = input;
        // Coding for 3 19-bit values
        v = (v | (v << 32)) & 0x01ff00000000ffffL; // 0b0000000111111111000000000000000000000000000000001111111111111111
        v = (v | (v << 16)) & 0x00ff0000ff0000ffL; // 0b0000000011111111000000000000000011111111000000000000000011111111
        v = (v | (v <<  8)) & 0x000f00f00f00f00fL; // 0b0000000000001111000000001111000000001111000000001111000000001111
        v = (v | (v <<  4)) & 0x00c30c30c30c30c3L; // 0b0000000011000011000011000011000011000011000011000011000011000011
        v = (v | (v <<  2)) & 0x0049249249249249L; // 0b0000000001001001001001001001001001001001001001001001001001001001

        return v;
    }
    
    public static long combine( long x, long y, long z ) {
        return x | (y<<1) | (z<<2);
    }
    public static long extract( long v, int i ) {
        return v & (0x1249249249249249L<<i);
    }
}
