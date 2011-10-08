package de.funky_clan.octree.minecraft.blocks;

import java.util.Arrays;

/**
 * @author synopia
 */
public enum Block {
    AIR( 0, true ),
    DIRT( 1, 2, 0 ),
    GRASS( 2, 3, 0, 3, 0, 0, 0, 2, 0, 3, 0, 3, 0),
    STONE( 3, 1, 0),
    COBBLESTONE( 4, 0, 1),
    WOODEN_PLANK( 5, 4, 0),
    BEDROCK( 7, 1,1),
    WATER(8, true, 13, 12),
    STATIONARY_WATER(9, true, 14, 12),
    LAVA(10, 13, 14),
    STATIONART_LAVA(11, 14, 14),
    SAND( 12, 2, 1),
    GRAVEL(13, 8, 4),
    GOLD_ORE(14, 0, 2),
    IRON_ORE(15, 1, 2),
    COAL_ORE(16, 3, 2),
    WOOD(17, 4, 1, 4, 1, 5, 1, 5, 1, 4, 1, 4, 1),
    LEAVES(18, 4, 3),
    SNOW(78, 4, 4, 4, 4, 2, 4, 2, 0, 4, 4, 4, 4),
    ICE(79, 3, 4),

    UNKNOWN()
    ;

    private float[][] textures = new float[6][2];
    private int id;
    private boolean transparent;
    public final static Block[] MAP = new Block[257];

    Block() {
        this( 256, false, 0, 2);
    }
    Block( int id, float ... c ) {
        init(id, false, c);
    }
    Block( int id, boolean transparent, float ... c ) {
        init(id, transparent, c);
    }
    Block(int id, boolean transparent) {
        this(id, transparent, 0, 0);
    }

    static {
        for (int i = 0; i < MAP.length; i++) {
            MAP[i] = UNKNOWN;
        }
        for (Block block : Block.values()) {
            MAP[block.id] = block;
        }
    }

    private void init(int id, boolean transparent, float[] c) {
        this.id = id;
        this.transparent = transparent;
        if( c.length>2 ) {
            for (int i = 0; i < c.length/2; i++) {
                textures[i][0] = c[i*2]/16.f;
                textures[i][1] = c[i*2+1]/16.f;
            }
        } else if( c.length==2 ) {
            for (int i = 0; i < textures.length; i++) {
                textures[i][0] = c[0]/16.f;
                textures[i][1] = c[1]/16.f;
            }
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
