package de.funky_clan.octree.minecraft.blocks;

import java.util.Arrays;

/**
 * @author synopia
 */
public enum Block {
    AIR( 0, true ),
    DIRT( 1, 2, 0 ),
    SAND( 2, 2, 1),
    STONE( 3, 1, 0),
    WOOD( 4, 4, 0),
    TREE_TRUNK( 5, 4, 1),
    ;

    private float tx;
    private float ty;
    private int id;
    private boolean transparent;
    public final static Block[] MAP = new Block[256];

    Block(int id, boolean transparent) {
        this(id);
        this.transparent = transparent;
    }
    Block(int id) {
        this(id, 0, 0);
    }
    Block(int id, int x, int y) {
        tx = x/16.f;
        ty = y/16.f;
        this.id = id;        
    }

    static {
        for (int i = 0; i < MAP.length; i++) {
            MAP[i] = AIR;
        }
        for (Block block : Block.values()) {
            MAP[block.id] = block;
        }
    }

    public boolean isTransparent() {
        return transparent;
    }

    public float getTextureX() {
        return tx;        
    }
    public float getTextureY() {
        return ty;
    }
    
    public static Block getBlock( int id ) {
        Block block = MAP[id];
        return block !=null ? block : AIR;
    }
}
