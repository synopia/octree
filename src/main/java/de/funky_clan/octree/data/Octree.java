package de.funky_clan.octree.data;

/**
 * @author synopia
 */
public class Octree {
    private OctreeNode root;

    public Octree(int x, int y, int z, int depth) {
        root = new OctreeNode(this, x, y, z, depth);
    }

    public OctreeNode getRoot() {
        return root;
    }

    public OctreeNode createNode( int x, int y, int z, int depth) {
        return new OctreeNode(this, x, y, z, depth);
    }
}
