package org.hexgridapi.core;

import com.jme3.asset.AssetManager;
import org.hexgridapi.events.TileChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.loader.HexGridMapLoader;
import org.hexgridapi.utility.HexCoordinate;

/**
 * This class holds the hex data of the map.
 *
 * @todo: refresh method, when the mapElement is change but the chunk isn't on
 * memory, the chunk when loaded should be refreshed to get the right element.
 * @author Eike Foede, Roah
 */
public final class MapData {

    private final AssetManager assetManager; //used for loading map
//    private final HashMap<Vector2Int, HexTile> tileData = new HashMap<Vector2Int, HexTile>();
    private final ChunkData chunkData = new ChunkData();
    private final ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private final HexGridMapLoader hexGridMapLoader;
    private String mapName = "Undefined";// = "Reset";
    /**
     * Key index :
     * -2 = and below used for non existant tile or ghost tile
     * -1 = used for areaTexture
     * 00 = used for EMPTY_TEXTURE_KEY (when specifiating no texture)
     * 01 = and above user for added texture (ordered the way the get added).
     */
    private ArrayList<String> textureKeys = new ArrayList<String>();

    public MapData(AssetManager assetManager) {
        this(null, assetManager);
    }

//    public MapData(Enum[] textureKeys, AssetManager assetManager) {
//        this(textureKeys, assetManager);
//    }
//
//    public MapData(String[] textureKeys, AssetManager assetManager) {
//        this(textureKeys, assetManager);
//    }
    
    public MapData(Object[] textureKeys, AssetManager assetManager){
        this.assetManager = assetManager;
        hexGridMapLoader = new HexGridMapLoader(assetManager);
        genTextureKeys(textureKeys);
    }

    private void genTextureKeys(Object[] userKey) {
        this.textureKeys.add("EMPTY_TEXTURE_KEY");
        if (userKey != null) {
            for (int i = 0; i < userKey.length; i++) {
                textureKeys.add(userKey[i].toString());
            }
        }
    }

    /**
     * Register a listener to respond to Tile Event.
     *
     * @param listener to register.
     */
    public void registerTileChangeListener(TileChangeListener listener) {
        tileListeners.add(listener);
    }

    /**
     * Remove listener from event on tile.
     *
     * @param listener
     */
    public void removeTileChangeListener(TileChangeListener listener) {
        tileListeners.remove(listener);
    }

    /**
     * @return current map name.
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * @return current map name.
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @return true if there is data.
     */
    public boolean containTilesData() {
//        return !tileData.isEmpty();
        return !chunkData.isEmpty();
    }

    /**
     * Get tile(s) properties. (one tile)
     *
     * @param tilePos Offset position of the tile.
     * @return can be null
     */
    public HexTile getTile(HexCoordinate tilePos) {
//        return tileData.get(tile.getAsOffset());
        return chunkData.getTile(tilePos);
    }

    /**
     * Get tile(s) properties. (multiple tile)
     *
     * @param tilePos Offset position of the tile.
     * @return can contain null
     */
    public HexTile[] getTile(HexCoordinate[] tilePos) {
        HexTile[] result = new HexTile[tilePos.length];
        for (int i = 0; i < tilePos.length; i++) {
            result[i] = chunkData.getTile(tilePos[i]);
//            result[i] = tileData.get(tilePos[i].getAsOffset());
        }
        return result;
    }

    /**
     * Change the designed tile(s) properties.
     *
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexCoordinate tilePos, HexTile tile) {
        setTile(new HexCoordinate[]{tilePos}, new HexTile[]{tile});
    }
    /**
     * Change the designed tile(s) properties.
     * if tile array size is == 1 the given properties will be apply on all
     * position,
     * else the two array size must match.
     *
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexCoordinate[] tilePos, HexTile[] tile) {
        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
        boolean arrayUpdate = false;
        if (tile.length > 1 && tile.length == tilePos.length) {
            arrayUpdate = true;
        } else if (tile.length > 1 && tile.length != tilePos.length) {
            throw new UnsupportedOperationException("Inserted param does not match the requiment tile.length != tilePos.length");
        }
        for (int i = 0; i < tilePos.length; i++) {
            tceList[i] = updateTileData(tilePos[i], arrayUpdate ? tile[i] : tile[0]);
        }
        updateTileListeners(tceList);
    }

//    public void setTilesHeight(byte[] height, HexCoordinate[] tilePos) {
//        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
//        for(int i = 0; i < tilePos.length; i++){
//            HexTile tile = tileData.get(tilePos[i].getAsOffset());
//            if (tile != null) {
//                tceList[i] = updateTileData(tilePos[i], tile.cloneChangedHeight(height));
//            } else {
//                tceList[i] = updateTileData(tilePos[i], new HexTile(height, defaultkeyTexture));
//            }
//        }
//        updateTileListeners(tceList);
//    }
//
//    public void setTilesTextureKey(String[] key, HexCoordinate... tilePos) {
//        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
//        for(int i = 0; i < tilePos.length; i++){
//            HexTile tile = tileData.get(tilePos[i].getAsOffset());
//            if (tile != null) {
//                tceList[i] = updateTileData(tilePos[i], tile.cloneChangedTextureKey(getTextureKey(key)));
//            } else {
//                tceList[i] = updateTileData(tilePos[i], new HexTile((byte) 0, getTextureKey(key)));
//            }
//        }
//        updateTileListeners(tceList);
//    }
    
    private TileChangeEvent updateTileData(HexCoordinate tilePos, HexTile tile) {
        HexTile oldTile;
        if (tile != null) {
            oldTile = chunkData.add(tilePos, tile);
//            oldTile = tileData.put(tilePos.getAsOffset(), tile);
        } else {
            oldTile = chunkData.remove(tilePos);
//            oldTile = tileData.remove(tilePos.getAsOffset());
        }
        return new TileChangeEvent(tilePos, oldTile, tile);
    }
    
    public boolean saveArea(String mapName) {
        this.mapName = mapName;
        return hexGridMapLoader.saveArea(mapName);
    }

    public boolean loadArea(String mapName) {
        return hexGridMapLoader.loadArea(mapName);
    }

    /**
     * The texture used when adding a tile with no texture.
     *
     * @param Key texture to use as default
     * @todo 
     */
//    public void setDefaultTexture(String Key) {
//        for (Vector2Int coord : tileData.keySet()) {
//            tileData.put(coord, tileData.get(coord).cloneChangedTextureKey(getTextureKey(Key)));
//        }
//        updateTileListeners(new TileChangeEvent(null, new HexTile(Integer.MIN_VALUE, getTextureKey(Key)), new HexTile(Integer.MIN_VALUE, Integer.MIN_VALUE)));
//    }

    /**
     * Get all tile around the defined position, return null for tile who
     * doesn't exist.
     * <li> HexTile[0] == right </li>
     * <li> HexTile[1] == top right </li>
     * <li> HexTile[2] == top left </li>
     * <li> HexTile[3] == left </li>
     * <li> HexTile[4] == bot left </li>
     * <li> HexTile[5] == bot right </li>
     *
     * @param position of the center tile.
     * @return All tile arround the needed tile.
     */
    public HexTile[] getNeightbors(HexCoordinate position) {
        HexCoordinate[] coords = position.getNeighbours();
        HexTile[] neighbours = new HexTile[coords.length];
        for (int i = 0; i < neighbours.length; i++) {
            neighbours[i] = chunkData.getTile(coords[i]);
//            neighbours[i] = tileData.get(coords[i].getAsOffset());
        }
        return neighbours;
    }

    /**
     * Call/Update all registered tile listener with the last event.
     *
     * @param tce Last tile event.
     */
    private void updateTileListeners(TileChangeEvent... tce) {
        for (TileChangeListener l : tileListeners) {
            l.onTileChange(tce);
        }
    }
    
    /**
     * Cleanup the current map.
     */
    public void Cleanup() {
        //Todo remove all file from the temps folder
//        chunkPos.clear();
        chunkData.clear();
//        tileData.clear();
//        updateTile(new TileChangeEvent(null, null, null));
    }

    /**
     * Convert a textureKey to is mapped value (name).
     *
     * @param textureKey
     * @return EMPTY_TEXTURE_KEY if not found
     */
    public String getTextureValue(int textureKey) {
        if (textureKey == -1) {
            return "SELECTION_TEXTURE";
        } else if (textureKey < -1) {
            return "NO_TILE";
        } else {
            try {
                return textureKeys.get(textureKey);
            } catch (IndexOutOfBoundsException e) {
                return "EMPTY_TEXTURE_KEY";
            }
        }
    }

    /**
     * Convert a texture value (name) to it's mapped textureKey.
     *
     * @param value texture name
     * @return "NO_TILE" if == null
     * @throws NoSuchFieldError if no mapping
     */
    public int getTextureKey(String value) throws NoSuchFieldError {
        if(value == null || value.equals("NO_TILE")){
            return -2;
        } else if (value.equals("SELECTION_TEXTURE")) {
            return -1;
        }
        int result = textureKeys.indexOf(value);
        if (result == -1) {
            throw new NoSuchFieldError(value + " is not in the registered key List.");
        } else {
            return result;
        }
    }

    /**
     *
     * @return all registered texture value, cannot be edited
     */
    public List<String> getTextureKeys() {
        return Collections.unmodifiableList(textureKeys);
    }
}