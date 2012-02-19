package de.funky_clan.octree.data;

import com.google.inject.Inject;
import de.funky_clan.octree.Morton;

import javax.inject.Provider;

/**
 * @author synopia
 */
public class Octree {
    private OctreeNode root;
    private Provider<OctreeNode> nodeProvider;

    @Inject
    public Octree(Provider<OctreeNode> nodeProvider) {
        this.nodeProvider = nodeProvider;
        root = createNode( 0, 0, 0, Morton.MORTON_BITS );
    }

    public OctreeNode getRoot() {
        return root;
    }

    public OctreeNode createNode( int x, int y, int z, int depth) {
        OctreeNode node = nodeProvider.get();
        node.init( this, x, y, z, depth );
        return node;
    }
}
