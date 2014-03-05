package hexsystem.events;

import hexsystem.HexTile;
import utility.HexCoordinate.Offset;
import utility.Vector2Int;

/**
 *
 * @author Eike Foede
 */
public class TileChangeEvent {

    private Vector2Int chunkPos;
    private Offset tilePos;
    private HexTile oldTile;
    private HexTile newTile;

    public TileChangeEvent(Vector2Int chunkPos, Offset tilePos, HexTile oldTile, HexTile newTile) {
        this.tilePos = tilePos;
        this.oldTile = oldTile;
        this.newTile = newTile;
        this.chunkPos = chunkPos;
    }

    public Vector2Int getChunkPos(){
        return chunkPos;
    }
    
    /**
     * @return Odd-R Offset coordinate of the tile relative to mapGrid
     */
    public Offset getTilePos() {
        return tilePos;
    }

    public HexTile getOldTile() {
        return oldTile;
    }

    public HexTile getNewTile() {
        return newTile;
    }
}
