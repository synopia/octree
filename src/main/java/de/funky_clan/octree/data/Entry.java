package de.funky_clan.octree.data;

import de.funky_clan.chunks.Chunk;
import de.funky_clan.chunks.ChunkRenderer;
import de.funky_clan.chunks.DefaultChunkRenderer;

/**
* @author synopia
*/
public final class Entry implements Comparable<Entry> {
    private Chunk chunk;
    private ChunkRenderer renderer;
    private int state;
    private float distanceToEye;
    private boolean inFrustum;

    public Entry(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int compareTo(Entry entry) {
        return distanceToEye<entry.distanceToEye ? -1 : (distanceToEye>entry.distanceToEye ? 1 : 0);
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public ChunkRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(ChunkRenderer renderer) {
        this.renderer = renderer;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getDistanceToEye() {
        return distanceToEye;
    }

    public void setDistanceToEye(float distanceToEye) {
        this.distanceToEye = distanceToEye;
    }

    public boolean isInFrustum() {
        return inFrustum;
    }

    public void setInFrustum(boolean inFrustum) {
        this.inFrustum = inFrustum;
    }
}
