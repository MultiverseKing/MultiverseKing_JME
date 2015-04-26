package org.hexgridapi.core.mapgenerator;

import java.util.HashMap;
import org.hexgridapi.utility.HexCoordinate;

/**
 * used as chunk Holder while generating procedural content.
 * @author roah
 */
public class ProceduralChunkData {
    
    private HashMap<Integer, HashMap<HexCoordinate, Integer>> chunkData = new HashMap<Integer, HashMap<HexCoordinate, Integer>>();
    
    public int getPositionData(int param, HexCoordinate position){
        return chunkData.get(param).get(position);
    }

    public void add(int param, HexCoordinate tilePos, int value) {
        if(!chunkData.containsKey(param)) {
            chunkData.put(param, new HashMap<HexCoordinate, Integer>());
        } else {
            chunkData.get(param).put(tilePos, value);
        }
    }
}
