package hexsystem.events;

import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class ChunkChangeEvent {

    private boolean purge = false;
    private Vector2Int chunkPos = null;

    /**
     *
     * @param chunkPos
     */
    public ChunkChangeEvent(Vector2Int chunkPos) {
        this.chunkPos = chunkPos;
    }

    /**
     *
     * @param b
     */
    public ChunkChangeEvent(boolean b) {
        this.purge = true;
    }

    /**
     *
     * @return
     */
    public Vector2Int getChunkPos() {
        return chunkPos;
    }

    /**
     *
     * @return
     */
    public boolean purge() {
        return purge;
    }
}
