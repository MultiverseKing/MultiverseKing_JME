/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;
import java.lang.reflect.Array;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
class ChunkData {
    private byte lastAddedID;
    private Vector2Int[] chunkKey = new Vector2Int[4];
    private HexTile[][][] chunkValue = new HexTile[4][][];

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

    private byte getEmptySlotID(){
        for(byte i = 0; i < 4; i++){
            if(chunkKey[i] == null){
                return i;
            }
        }
        return 4;
    }

    private byte removeChunk(Vector2Int chunkPos) {
        byte ID;
        for(ID = 0; ID < 4; ID++){
            if(ID != lastAddedID){
                if(chunkKey[ID] != new Vector2Int(chunkPos.x+1, chunkPos.y+1) && chunkKey[ID] != new Vector2Int(chunkPos.x-1, chunkPos.y-1) &&
                     chunkKey[ID] != new Vector2Int(chunkPos.x+1, chunkPos.y-1) && chunkKey[ID] != new Vector2Int(chunkPos.x-1, chunkPos.y+1) &&
                       chunkKey[ID] != new Vector2Int(chunkPos.x+1, chunkPos.y) && chunkKey[ID] != new Vector2Int(chunkPos.x-1, chunkPos.y) &&
                         chunkKey[ID] != new Vector2Int(chunkPos.x, chunkPos.y+1) && chunkKey[ID] != new Vector2Int(chunkPos.x, chunkPos.y-1)){
                    chunkKey[ID] = null;
                    chunkValue[ID] = null;
                    return ID;
                } 
            }
        }
        
        for(ID = 0; ID < 4; ID++){
            if(ID != lastAddedID){
                if (chunkKey[ID] != new Vector2Int(chunkPos.x+1, chunkPos.y) && chunkKey[ID] != new Vector2Int(chunkPos.x-1, chunkPos.y) &&
                     chunkKey[ID] != new Vector2Int(chunkPos.x, chunkPos.y+1) && chunkKey[ID] != new Vector2Int(chunkPos.x, chunkPos.y-1)){
                    chunkKey[ID] = null;
                    chunkValue[ID] = null;
                    return ID;
                }
            }
        }

        do {
            ID = (byte)FastMath.nextRandomInt(0, 3);
        } while (ID == lastAddedID);
        
        return ID;
    }
}
