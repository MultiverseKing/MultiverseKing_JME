/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
class ChunkDataOld {

    private final byte limit;
    private byte lastAddedID;
    private Vector2Int[] chunkKey;      //chunkKey[resultID] == chunkPos
    private HexTile[][][] chunkValue;   //chunkValue[chunkID][tileX][tileY]

    ChunkDataOld(byte limit) {
        this.limit = limit;
        this.lastAddedID = limit;
        chunkKey = new Vector2Int[limit];
        chunkValue = new HexTile[limit][][];
    }

    void add(Vector2Int chunkPos, HexTile[][] tiles) {
        byte slotID = getEmptySlotID();
        if (slotID != limit) { //limit == null
            chunkKey[slotID] = chunkPos;
            chunkValue[slotID] = tiles;
            lastAddedID = slotID;
        } else {
            slotID = removeChunk(chunkPos);
            chunkKey[slotID] = chunkPos;
            chunkValue[slotID] = tiles;
            lastAddedID = slotID;
        }
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
        byte chunkID = getChunkID(chunk);
        if (chunkID != limit) {
            try {
                return chunkValue[chunkID][tileOffset.x][tileOffset.y];
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
        byte chunkID = getChunkID(chunk);
        if (chunkID != limit) {
            try {
                chunkValue[chunkID][tileOffset.x][tileOffset.y] = t;
                return true;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Hex index out of bounds");
            }
        } else {
            System.err.println("Chunk Doesn't Exist in memory");
        }
        return false;
    }

    private byte getEmptySlotID() {
        byte result;
        for (result = 0; result < limit; result++) {
            if (chunkKey[result] == null) {
                return result;
            }
        }
        return limit;
    }

    private byte removeChunk(Vector2Int chunkPos) {
        byte resultID;
        for (resultID = 0; resultID < limit; resultID++) {
            if (resultID != lastAddedID) {
                if (!chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y + 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y - 1))
                        && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y - 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y + 1))
                        && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y))
                        && chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y + 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y - 1))) {
                    chunkKey[resultID] = null;
                    chunkValue[resultID] = null;
                    return resultID;
                }
            }
        }

        for (resultID = 0; resultID < limit; resultID++) {
            if (resultID != lastAddedID) {
                if (!chunkKey[resultID].equals(new Vector2Int(chunkPos.x + 1, chunkPos.y)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x - 1, chunkPos.y))
                        && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y + 1)) && !chunkKey[resultID].equals(new Vector2Int(chunkPos.x, chunkPos.y - 1))) {
                    chunkKey[resultID] = null;
                    chunkValue[resultID] = null;
                    return resultID;
                }
            }
        }

        do {
            resultID = (byte) FastMath.nextRandomInt(0, limit - 1);
        } while (resultID == lastAddedID);

        return resultID;
    }

    private byte getChunkID(Vector2Int chunk) {
        byte result;
        for (result = 0; result < limit; result++) {
            if (chunkKey[result] != null && chunkKey[result].equals(chunk)) {
                return result;
            }
        }
        return limit;
    }

    void setAllTile(ElementalAttribut eAttribut) {
        for (byte i = 0; i < chunkValue.length; i++) {
            if (chunkKey[i] != null && chunkValue[i] != null) {
                for (int j = 0; j < chunkValue[i].length; j++) {
                    for (int k = 0; k < chunkValue[i][j].length; k++) {
                        chunkValue[i][j][k] = new HexTile(eAttribut, (byte) chunkValue[i][j][k].getHeight());
                    }
                }
            }
        }
    }

    HexTile[][] getChunkTiles(Vector2Int chunkPos) {
        byte chunkID = getChunkID(chunkPos);
        if (chunkID != limit) {
            return this.chunkValue[chunkID];
        }
        return null;
    }

    void clear() {
        chunkKey = new Vector2Int[limit];
        chunkValue = new HexTile[limit][][];
        lastAddedID = limit;
    }
}
