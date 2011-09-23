package de.funky_clan.octree;

import de.funky_clan.octree.minecraft.RegionFileLoader;
import de.funky_clan.voxel.data.OctreeNode;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author synopia
 */
public class WorldLoader {
    private OctreeNode octree;
    private RegionFileLoader loader;

    public WorldLoader(OctreeNode octree) {
        this.octree = octree;
        loader = new RegionFileLoader();
    }

    public void load( Vector3f pos ) {
        int chunkX = ((int) pos.getX()) >> 4;
        int chunkZ = ((int) pos.getZ()) >> 4;

        for (int i = chunkX-10; i < chunkX+10; i++) {
            for (int j = chunkZ-10; j < chunkZ+10; j++) {
                loader.load(octree, i,j);
            }
        }
    }
}
