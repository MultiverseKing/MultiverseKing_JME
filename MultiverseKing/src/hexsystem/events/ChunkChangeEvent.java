package hexsystem.events;

import hexsystem.HexTile;
import utility.Vector2Int;

/**
 *
 * @author Eike Foede
 */
public class ChunkChangeEvent {

    private final Vector2Int chunkPos;
    private final HexTile[][] newTiles;

    public ChunkChangeEvent(Vector2Int chunkPos, HexTile[][] hexTiles) {
        this.chunkPos = chunkPos;
        this.newTiles = hexTiles;
    }

    public Vector2Int getChunkPos(){
        return chunkPos;
    }

    public HexTile[][] getNewTiles() {
        return newTiles;
    }
}
