package org.hexgridapi.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Contain all hex room data.
 * @author roah
 */
public class ChunkData {

    /**
     * Map holding all chunk on the current memory.
     */
    protected HashMap<Vector2Int, HexTile[][]> chunks = new HashMap<Vector2Int, HexTile[][]>();

    public void add(Vector2Int chunkPos, HexTile[][] tiles) {
        chunks.put(chunkPos, tiles);
    }

    /**
     * Return Hextile properties if it exist otherwise return null.
     *
     * @param chunk chunkPos on mapGrid.
     * @param tilePos tilePos inside the chunk.
     * @return null if the tile doesn't exist.
     */
    public HexTile getTile(Vector2Int chunk, HexCoordinate tilePos) {
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
    public boolean setTile(Vector2Int chunk, HexCoordinate tilePos, HexTile t) {
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

    public boolean exist(Vector2Int chunk, HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        return chunks.get(chunk)[tileOffset.x][tileOffset.y] == null ? false : true;
    }

    public HexTile[][] getChunkTiles(Vector2Int chunkPos) {
        return chunks.get(chunkPos);
    }
    
    public void setAllTile(Byte height, Byte textureKey){
        Set<Map.Entry<Vector2Int, HexTile[][]>> chunkValue = chunks.entrySet();
        for (Map.Entry<Vector2Int, HexTile[][]> chunk : chunkValue) {
            HexTile[][] tiles = chunk.getValue();
            for (int j = 0; j < tiles.length; j++) {
                for (int k = 0; k < tiles[j].length; k++) {
                    if(textureKey != null && height != null){
                        tiles[j][k] = new HexTile(height, textureKey);
                    } else if (height != null){
                        tiles[j][k] = tiles[j][k].cloneChangedHeight(height);
                    } else if (textureKey != null){
                        tiles[j][k] = tiles[j][k].cloneChangedTextureKey(textureKey);
                    }
                }
            }
        }
    }
    
    public void clear() {
        chunks.clear();
    }
    
    public boolean isEmpty(){
        return chunks.isEmpty();
    }
}
