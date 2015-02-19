package org.hexgridapi.events;

import org.hexgridapi.utility.Vector2Int;

/**
 * Trigger when an event occur in mapData who affect the chunk.
 * 
 * @author roah
 */
public class ChunkChangeEvent {

    protected boolean delete = false;
    protected Vector2Int chunkPos = null;

    public ChunkChangeEvent(Vector2Int chunkPos) {
        this.chunkPos = chunkPos;
    }

    public ChunkChangeEvent(boolean delete) {
        this.delete = true;
    }

    /**
     * if chunkPos == null it mean this event affect the whole map.
     * @return 
     */
    public Vector2Int getChunkPos() {
        return chunkPos;
    }

    public boolean delete() {
        return delete;
    }
}
