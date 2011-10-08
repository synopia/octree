package de.funky_clan.octree.minecraft.blocks;

import java.util.Arrays;

/**
 * @author synopia
 */
public enum Block {
    AIR( 0, true ),
//    DIRT( 1, 2, 0 ),
//    GRASS( 2, 3, 0, 3, 0, 0, 0, 2, 0, 3, 0, 3, 0),
//    STONE( 3, 1, 0),
//    COBBLESTONE( 4, 0, 1),
//    WOOD( 5, 4, 0),
//    WATER(8, 13, 12),
//    STATIONARY_WATER(9, 14, 12),
//
//    SAND( 12, 2, 1),
//    GRAVEL(13, 8, 4),

    UNKNOWN()
    ;

    private float[][] textures = new float[6][2];
    private int id;
    private boolean transparent;
    public final static Block[] MAP = new Block[257];

    Block() {
        this( 256, 0, 2);
    }
    Block(int id, float ... c) {
        this.id = id;
        if( c.length>2 ) {
            for (int i = 0; i < c.length/2; i++) {
                textures[i][0] = c[i*2]/16.f;
                textures[i][1] = c[i*2+1]/16.f;
            }
        } else {
            for (int i = 0; i < textures.length; i++) {
                textures[i][0] = c[0]/16.f;
                textures[i][1] = c[1]/16.f;
            }
        }
    }
    Block(int id, boolean transparent) {
        this(id);
        this.transparent = transparent;
    }
    Block(int id) {
        this(id, 0, 0);
    }

    static {
        for (int i = 0; i < MAP.length; i++) {
            MAP[i] = UNKNOWN;
        }
        for (Block block : Block.values()) {
            MAP[block.id] = block;
        }
    }

    public boolean isTransparent() {
        return transparent;
    }

    public float getTextureX(int side) {
        return textures[side][0];
    }
    public float getTextureY(int side) {
        return textures[side][1];
    }
}
