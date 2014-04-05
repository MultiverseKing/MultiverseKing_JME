/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
class ChunkData {

    HashMap<Vector2Int, HexTile[][]> chunks = new HashMap<Vector2Int, HexTile[][]>();
    private byte lastAddedID;

    ChunkData(byte limit) {
//        this.limit = limit;
//        this.lastAddedID = limit;
//        chunkKey = new Vector2Int[limit];
//        chunkValue = new HexTile[limit][][];
    }

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
                System.err.println("Hex index out of bounds");
            }
        } else {
            System.err.println("Chunk doesn't Exist in memory");
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
//
//    private byte removeChunk(Vector2Int chunkPos) {
//        byte resultID;
//        for (resultID = 0; resultID < limit; resultID++) {
//            if (resultID != lastAddedID) {
//                if (!chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y + 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y - 1))
//                        && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y - 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y + 1))
//                        && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y))
//                        && chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y + 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y - 1))) {
//                    chunkKey[resultID] = null;
//                    chunkValue[resultID] = null;
//                    return resultID;
//                }
//            }
//        }
//
//        for (resultID = 0; resultID < limit; resultID++) {
//            if (resultID != lastAddedID) {
//                if (!chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y))
//                        && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y + 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y - 1))) {
//                    chunkKey[resultID] = null;
//                    chunkValue[resultID] = null;
//                    return resultID;
//                }
//            }
//        }
//
//        do {
//            resultID = (byte) FastMath.nextRandomInt(0, limit - 1);
//        } while (resultID == lastAddedID);
//
//        return resultID;
//    }

    void setAllTile(ElementalAttribut eAttribut) {
        Set<Entry<Vector2Int, HexTile[][]>> chunks = getAllChunks();
        for(Entry<Vector2Int, HexTile[][]> chunk : chunks){
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
    public Set<Entry<Vector2Int, HexTile[][]>> getAllChunks(){
        return chunks.entrySet();
        
    }
}
