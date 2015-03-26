package org.hexgridapi.core.mapgenerator;

import org.hexgridapi.core.HexTile;
import org.hexgridapi.utility.HexCoordinate;

/**
 * used as chunk Holder while generating procedural content.
 * @author roah
 */
public class ProceduralChunk {
    
    private int currentIndex = 0;
    private HexTile[] tileData;
    private HexCoordinate[] tilePos;

    ProceduralChunk(int dataSize) {
        tileData = new HexTile[dataSize];
        tilePos = new HexCoordinate[dataSize];
        
    }
    
    public HexTile[] getTileData(){
        return tileData;
    }
    
    public HexCoordinate[] getTilePos(){
        return tilePos;
    }

    void add(HexCoordinate tilePos, HexTile hexTile) {
        this.tileData[currentIndex] = hexTile;
        this.tilePos[currentIndex] = tilePos;
        currentIndex++;
    }
}
