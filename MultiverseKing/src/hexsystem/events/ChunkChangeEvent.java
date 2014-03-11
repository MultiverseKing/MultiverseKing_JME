package hexsystem.events;

import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class ChunkChangeEvent {

    private boolean purge = false;
    private Vector2Int chunkPos = null;

    public ChunkChangeEvent(Vector2Int chunkPos) {
        this.chunkPos = chunkPos;
    }

    public ChunkChangeEvent(boolean b) {
        this.purge = true;
    }
    
    public Vector2Int getChunkPos(){
        return chunkPos;
    }

    public boolean purge() {
        return purge;
    }
}
