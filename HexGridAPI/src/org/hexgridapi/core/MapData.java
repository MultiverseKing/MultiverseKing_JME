package org.hexgridapi.core;

import com.jme3.asset.AssetManager;
import org.hexgridapi.events.TileChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hexgridapi.core.mapgenerator.ProceduralGenerator;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.loader.HexGridMapLoader;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * This class holds the hex data of the map.
 *
 * @todo: refresh method, when the mapElement is change but the chunk isn't on
 * memory, the chunk when loaded should be refreshed to get the right element.
 * @author Eike Foede, Roah
 */
public final class MapData {

    private final AssetManager assetManager; //used for save/loading map
    private final ChunkData chunkData = new ChunkData();
    private final ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private final HexGridMapLoader hexGridMapLoader;
    private GhostMode mode;
    private ProceduralGenerator generator;
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
        this(null, assetManager, GhostMode.PROCEDURAL);
    }
    
    public MapData(Object[] textureKeys, AssetManager assetManager, GhostMode mode){
        this.assetManager = assetManager;
        hexGridMapLoader = new HexGridMapLoader(assetManager);
        genTextureKeys(textureKeys);
        if(!mode.equals(GhostMode.NONE)){
            generator = new ProceduralGenerator(ProceduralGenerator.generateSeed(), this.textureKeys.size());
        }
        this.mode = mode;
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

    public GhostMode getMode() {
        return mode;
    }
    
    /**
     * Check if there is any data currently stored.
     * 
     * @return true if there is data.
     */
    public boolean containTilesData() {
        return !chunkData.isEmpty();
    }
    

    
    /**
     * Check if the specifiate chunk got any tile data stored.
     * 
     * @param chunkPos inspected chunk.
     * @return true the specifiate chunk got any tile stored.
     */
    public boolean contain(Vector2Int chunkPos) {
        return chunkData.contain(chunkPos);
    }

    /**
     * Get tile(s) properties. (one tile)
     *
     * @param tilePos Offset position of the tile.
     * @return can be null
     */
    public HexTile getTile(HexCoordinate tilePos) {
        HexTile t = chunkData.getTile(tilePos);
        if(t == null && !chunkData.contain(tilePos)
                && (mode.equals(GhostMode.GHOST_PROCEDURAL)
                    || mode.equals(GhostMode.PROCEDURAL))){
            t = generator.getTileValue(tilePos);
        }
        return t;
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
            if(result[i] == null && mode.equals(GhostMode.GHOST_PROCEDURAL)
                    || mode.equals(GhostMode.PROCEDURAL)){
                result[i] = generator.getTileValue(tilePos[i]);
            }
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
        } else {
            oldTile = chunkData.remove(tilePos);
            if(oldTile == null && mode.equals(GhostMode.GHOST_PROCEDURAL)){
                chunkData.add(tilePos, null);
            }
        }
        return new TileChangeEvent(tilePos, oldTile, tile);
    }
    
    public int getSeed() {
        return generator.getSeed();
    }
    /**
     * This lead to some issue if used on runtime as all generated data 
     * use the old seed and new data will use the new seed...
     * @param seed 
     */
    public void setSeed(int seed) {
        generator.setSeed(seed);
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
        chunkData.clear();
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
    
    /**
     * Used to define how the chunk will handle tiles if they does not exist.
     */
    public enum GhostMode {

        /**
         * null chunk will be ignored, 
         * null tiles will be ignored.
         */
        NONE,
        /**
         * null chunk will be generated as ghost,
         * null tiles will be generated as ghost.
         */
        GHOST,
        /**
         * null chunk will be generated proceduraly,
         * null tile will be ignored.
         */
        PROCEDURAL,
        /**
         * null chunk will be generated proceduraly, 
         * null tile will be generated as ghost.
         */
        GHOST_PROCEDURAL;
    }
}