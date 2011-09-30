package de.funky_clan.voxel.data;

import cern.colt.function.LongObjectProcedure;
import cern.colt.map.OpenLongObjectHashMap;
import de.funky_clan.octree.WritableRaster;

import java.util.HashMap;

/**
 * @author synopia
 */
public class Octree implements WritableRaster {
    private OpenLongObjectHashMap chunks = new OpenLongObjectHashMap();
    private OctreeNode root;

    public Octree(int x, int y, int z, int size) {
        root = new OctreeNode(this, x, y, z, size);
        add( root );
    }

    protected void add( OctreeNode node ) {
        chunks.put( node.toMorton(), node );
    }

    @Override
    public void setPixel(int x, int y, int z, int color) {

    }

    @Override
    public int getPixel(int x, int y, int z) {
        long cx = x/OctreeNode.CHUNK_SIZE;
        long cy = y/OctreeNode.CHUNK_SIZE;
        long cz = z/OctreeNode.CHUNK_SIZE;
        long morton = OctreeNode.toMorton(cx, cy, cz);
        if( chunks.containsKey(morton) ) {
            OctreeNode node = (OctreeNode) chunks.get(morton);
            if (node instanceof Chunk) {
                Chunk chunk = (Chunk) node;
                return chunk.getPixel(x, y, z);
            }
        }
        return 0;
    }

    public OctreeNode getRoot() {
        return root;
    }
}
