/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;
import utility.HexCoordinate.Offset;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
class ChunkData {
    private byte lastAddedID;
    private Vector2Int[] chunkKey = new Vector2Int[4]; //chunkKey[resultID] == chunkPos
    private HexTile[][][] chunkValue = new HexTile[4][][]; //chunkValue[chunkID][tileX][tileY]

    void add(Vector2Int chunkPos, HexTile[][] tiles) {
        byte slotID = getEmptySlotID();
        if(slotID != 4){ //4 == null
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
     * @param chunk chunkPos on mapGrid.
     * @param tilePos tilePos inside the chunk.
     * @return null if the tile doesn't exist.
     */
    HexTile getTile(Vector2Int chunk, Offset tilePos) {
        byte chunkID = getChunkID(chunk);
        if(chunkID != 4){
            try {
                return chunkValue[chunkID][tilePos.q][tilePos.r];
            } catch (ArrayIndexOutOfBoundsException e){
                System.err.println("Hex index out of bounds");
            }
        } else {
            System.err.println("Chunk doesn't Exist");
        }
        return null;
    }
    
    /**
     * Change a tile properties, return false if an error occurs.
     * @param chunk chunkPos on mapGrid.
     * @param tilePos tilePos inside the chunk.
     * @param t tile properties.
     * @return true if the value is set correctly, false otherwise.
     */
    boolean setTile(Vector2Int chunk, Offset tilePos, HexTile t) {
        byte chunkID = getChunkID(chunk);
        if(chunkID != 4){
            try {
                chunkValue[chunkID][tilePos.q][tilePos.r] = t;
                return true;
            } catch (ArrayIndexOutOfBoundsException e){
                System.err.println("Hex index out of bounds");
            }
        } else {
            System.err.println("Chunk Doesn't Exist");
        }
        return false;
    }
    
    private byte getEmptySlotID(){
        byte result;
        for(result = 0; result < 4; result++){
            if(chunkKey[result] == null){
                return result;
            }
        }
        return result = 4;
    }

    private byte removeChunk(Vector2Int chunkPos) {
        byte resultID;
        for(resultID = 0; resultID < 4; resultID++){
            if(resultID != lastAddedID){
                if(chunkKey[resultID] != new Vector2Int(chunkPos.x+1, chunkPos.y+1) && chunkKey[resultID] != new Vector2Int(chunkPos.x-1, chunkPos.y-1) &&
                     chunkKey[resultID] != new Vector2Int(chunkPos.x+1, chunkPos.y-1) && chunkKey[resultID] != new Vector2Int(chunkPos.x-1, chunkPos.y+1) &&
                       chunkKey[resultID] != new Vector2Int(chunkPos.x+1, chunkPos.y) && chunkKey[resultID] != new Vector2Int(chunkPos.x-1, chunkPos.y) &&
                         chunkKey[resultID] != new Vector2Int(chunkPos.x, chunkPos.y+1) && chunkKey[resultID] != new Vector2Int(chunkPos.x, chunkPos.y-1)){
                    chunkKey[resultID] = null;
                    chunkValue[resultID] = null;
                    return resultID;
                } 
            }
        }
        
        for(resultID = 0; resultID < 4; resultID++){
            if(resultID != lastAddedID){
                if (chunkKey[resultID] != new Vector2Int(chunkPos.x+1, chunkPos.y) && chunkKey[resultID] != new Vector2Int(chunkPos.x-1, chunkPos.y) &&
                     chunkKey[resultID] != new Vector2Int(chunkPos.x, chunkPos.y+1) && chunkKey[resultID] != new Vector2Int(chunkPos.x, chunkPos.y-1)){
                    chunkKey[resultID] = null;
                    chunkValue[resultID] = null;
                    return resultID;
                }
            }
        }

        do {
            resultID = (byte)FastMath.nextRandomInt(0, 3);
        } while (resultID == lastAddedID);
        
        return resultID;
    }
    
    private byte getChunkID(Vector2Int chunk){
        byte result;
        for(result = 0; result < 4; result++){
            if(chunkKey[result].x == chunk.x && chunkKey[result].y == chunk.y){
                return result;
            }
        }
        return 4;
    }
}
