package hexsystem;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.ElementalAttribut;

/**
 * Contain all hex room data.
 * @author roah
 */
class ChunkData {

    /**
     * Map holding all chunk on the current memory.
     */
    HashMap<Vector2Int, HexTile[][]> chunks = new HashMap<Vector2Int, HexTile[][]>();

    void add(Vector2Int chunkPos, HexTile[][] tiles) {
        chunks.put(chunkPos, tiles);
    }

    /**
     * Return Hextile properties if it exist otherwise return null.
     *
     * @param chunk chunkPos on mapGrid.
     * @param tilePos tilePos inside the chunk.
     * @return null if the tile doesn't exist.
     */
    HexTile getTile(Vector2Int chunk, HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        HexTile[][] tiles = chunks.get(chunk);
        if (tiles != null) {
            try {
                return tiles[tileOffset.x][tileOffset.y];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Hex index out of bounds. Requested tile : " + tilePos);
            }
        } else {
            System.err.println("Chunk doesn't Exist in memory. Requested Chunk : " + chunk);
        }
        return null;
    }

    /**
     * Change a tile properties, return false if an error occurs.
     *
     * @param chunk chunkPos on mapGrid.
     * @param tilePos tilePos inside the chunk.
     * @param t tile properties.
     * @return true if the value is set correctly, false otherwise.
     */
    boolean setTile(Vector2Int chunk, HexCoordinate tilePos, HexTile t) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        HexTile[][] tiles = chunks.get(chunk);
        if (tiles != null) {
            try {
                tiles[tileOffset.x][tileOffset.y] = t;
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Hex index out of bounds");
            }
        } else {
            System.err.println("Chunk Doesn't Exist in memory");
        }
        return false;
    }

    boolean exist(Vector2Int chunk, HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        return chunks.get(chunk)[tileOffset.x][tileOffset.y] == null ? false : true;
    }

    void setAllTile(ElementalAttribut eAttribut) {
        Set<Entry<Vector2Int, HexTile[][]>> chunkValue = getAllChunks();
        for (Entry<Vector2Int, HexTile[][]> chunk : chunkValue) {
            HexTile[][] tiles = chunk.getValue();
            for (int j = 0; j < tiles.length; j++) {
                for (int k = 0; k < tiles[j].length; k++) {
                    tiles[j][k] = tiles[j][k].cloneChangedElement(eAttribut);
                }
            }
        }
    }

    HexTile[][] getChunkTiles(Vector2Int chunkPos) {
        return chunks.get(chunkPos);
    }

    void clear() {
        chunks.clear();
    }

    public Set<Entry<Vector2Int, HexTile[][]>> getAllChunks() {
        return chunks.entrySet();

    }
}
