package hexsystem.events;

import hexsystem.HexTile;
import java.util.ArrayList;
import utility.Vector2Int;

/**
 *
 * @author Eike Foede
 */
public class ChunkChangeEvent {

    private Vector2Int chunkPos;

    public ChunkChangeEvent(Vector2Int chunkPos) {
        this.chunkPos = chunkPos;
    }
    
    public Vector2Int getChunkPos(){
        return chunkPos;
    }
}
