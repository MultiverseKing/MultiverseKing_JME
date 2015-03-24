package org.hexgridapi.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Contain all hex data.
 *
 * @author roah
 */
public class ChunkData {

    /**
     * Map holding all chunk on the current memory.
     */
    private HashMap<Vector2Int, HashMap> chunks = new HashMap<Vector2Int, HashMap>();

    HexTile add(HexCoordinate tilePos, HexTile tile) {
        if (!chunks.containsKey(tilePos.getCorrespondingChunk())) {
            chunks.put(tilePos.getCorrespondingChunk(), new HashMap<HexCoordinate, HexTile>());
        }
        return (HexTile) chunks.get(tilePos.getCorrespondingChunk()).put(tilePos, tile);
    }
    
    /**
     * Remove a tile if it exist.
     * @param tilePos
     * @return old Contained tile.
     */
    HexTile remove(HexCoordinate tilePos){
        if (chunks.containsKey(tilePos.getCorrespondingChunk())) {
            return (HexTile) chunks.get(tilePos.getCorrespondingChunk()).remove(tilePos);
        }
        return null;
    }

    /**
     * Return Hextile properties if it exist otherwise return null.
     *
     * @param tilePos tilePos inside the chunk.
     * @return null if the tile doesn't exist.
     */
    HexTile getTile(HexCoordinate tilePos) {
        if (chunks.containsKey(tilePos.getCorrespondingChunk())) {
            return (HexTile) chunks.get(tilePos.getCorrespondingChunk()).get(tilePos);
        }
        return null;
    }
    
    boolean exist(Vector2Int chunk, HexCoordinate tilePos) {
        if (chunks.containsKey(chunk)) {
            if (chunks.get(chunk).containsKey(tilePos.getAsOffset())) {
                return true;
            }
        }
        return false;
    }

    Collection getChunkTiles(Vector2Int chunkPos) {
        return Collections.unmodifiableCollection(chunks.get(chunkPos).values());
    }

//    public void setAllTile(Byte height, Byte textureKey){
//        Set<Map.Entry<Vector2Int, HexTile[][]>> chunkValue = chunks.entrySet();
//        for (Map.Entry<Vector2Int, HexTile[][]> chunk : chunkValue) {
//            HexTile[][] tiles = chunk.getValue();
//            for (int j = 0; j < tiles.length; j++) {
//                for (int k = 0; k < tiles[j].length; k++) {
//                    if(textureKey != null && height != null){
//                        tiles[j][k] = new HexTile(height, textureKey);
//                    } else if (height != null){
//                        tiles[j][k] = tiles[j][k].cloneChangedHeight(height);
//                    } else if (textureKey != null){
//                        tiles[j][k] = tiles[j][k].cloneChangedTextureKey(textureKey);
//                    }
//                }
//            }
//        }
//    }


    /**
     * Check if the specifiate chunk is currently stored.
     * 
     * @param chunkPos inspected chunk.
     * @return true if stored.
     */
    boolean contain(Vector2Int chunkPos) {
        return chunks.containsKey(chunkPos);
    }
    
    /**
     * Check if the specifiate tile is currently stored.
     * 
     * @param tilePos inspected tile.
     * @return true if stored.
     */
    boolean contain(HexCoordinate tilePos) {
        if (contain(tilePos.getCorrespondingChunk())) {
            return chunks.get(tilePos.getCorrespondingChunk()).containsKey(tilePos);
        }
        return false;
    }
    
    void clear() {
        chunks.clear();
    }

    boolean isEmpty() {
        return chunks.isEmpty();
    }
}
