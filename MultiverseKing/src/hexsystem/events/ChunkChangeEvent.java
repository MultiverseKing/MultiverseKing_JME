package hexsystem.events;

import hexsystem.HexTile;
import utility.Vector2Int;

/**
 *
 * @author Eike Foede
 */
public class ChunkChangeEvent {

    private Vector2Int chunkPos;
    private final HexTile[][] oldTiles;
    private final HexTile[][] newTiles;

    public ChunkChangeEvent(Vector2Int chunkPos, HexTile[][] oldTiles, HexTile[][] newTiles) {
        this.chunkPos = chunkPos;
        this.oldTiles = oldTiles;
        this.newTiles = newTiles;
    }

    public Vector2Int getChunkPos(){
        return chunkPos;
    }

    public HexTile[][] getOldTiles() {
        return oldTiles;
    }

    public HexTile[][] getNewTiles() {
        return newTiles;
    }

    public ChunkChangeEvent setNewTile(HexTile[][] hexTile) {
        return new ChunkChangeEvent(chunkPos, oldTiles, hexTile);
    }
}
