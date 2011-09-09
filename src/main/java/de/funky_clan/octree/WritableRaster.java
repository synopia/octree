package de.funky_clan.octree;

/**
 * @author synopia
 */
public interface WritableRaster {
    void setPixel( int x, int y, int z, int color );
    int getPixel( int x, int y, int z );
}
