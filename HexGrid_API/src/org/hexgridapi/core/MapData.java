package org.hexgridapi.core;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import java.io.File;
import java.io.IOException;
import org.hexgridapi.events.TileChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.loader.ChunkDataLoader;
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

    private final AssetManager assetManager;
    private final HashMap<Vector2Int, HexTile> tileData = new HashMap<Vector2Int, HexTile>();
    private final ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private String mapName;// = "Reset";
    private byte defaultkeyTexture = 0;
    /**
     * Key index :
     * -2 = and below used for non existant tile or ghost tile
     * -1 = used for areaTexture
     * 00 = used for EMPTY_TEXTURE_KEY (when specifiating no texture)
     * 01 = and above user for added texture (ordered the way the get added).
     */
    private ArrayList<String> textureKeys = new ArrayList<String>();

    public MapData(AssetManager assetManager) {
        this.assetManager = assetManager;
        genTextureKeys(null);
    }

    public MapData(Enum[] textureKeys, AssetManager assetManager) {
        this.assetManager = assetManager;
        genTextureKeys(textureKeys);
    }

    public MapData(String[] textureKeys, AssetManager assetManager) {
        this.assetManager = assetManager;
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

    public AssetManager getAssetManager() {
        return assetManager;
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
        return !tileData.isEmpty();
    }

    /**
     * Get a tile properties.
     *
     * @todo see below.
     * @param tilePos Offset position of the tile.
     * @return null if the tile doesn't exist.
     */
    public HexTile getTile(HexCoordinate tilePos) {
        return tileData.get(tilePos.getAsOffset());
    }

    /**
     * Change the designed tile properties.
     *
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexTile tile, HexCoordinate... tilePos) {
        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
        for (int i = 0; i < tilePos.length; i++) {
            tceList[i] = updateTileData(tilePos[i], tile);
        }
        updateTileListeners(tceList);
    }

    public void setTileHeight(byte height, HexCoordinate... tilePos) {
        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
        for(int i = 0; i < tilePos.length; i++){
            HexTile tile = tileData.get(tilePos[i].getAsOffset());
            if (tile != null) {
                tceList[i] = updateTileData(tilePos[i], tile.cloneChangedHeight(height));
            } else {
                tceList[i] = updateTileData(tilePos[i], new HexTile(height, defaultkeyTexture));
            }
        }
        updateTileListeners(tceList);
    }

    public void setTileTextureKey(String key, HexCoordinate... tilePos) {
        TileChangeEvent[] tceList = new TileChangeEvent[tilePos.length];
        for(int i = 0; i < tilePos.length; i++){
            HexTile tile = tileData.get(tilePos[i].getAsOffset());
            if (tile != null) {
                tceList[i] = updateTileData(tilePos[i], tile.cloneChangedTextureKey(getTextureKey(key)));
            } else {
                tceList[i] = updateTileData(tilePos[i], new HexTile((byte) 0, getTextureKey(key)));
            }
        }
        updateTileListeners(tceList);
    }

    private TileChangeEvent updateTileData(HexCoordinate tilePos, HexTile tile) {
        HexTile oldTile;
        if (tile != null) {
            oldTile = tileData.put(tilePos.getAsOffset(), tile);
        } else {
            oldTile = tileData.remove(tilePos.getAsOffset());
        }
        return new TileChangeEvent(tilePos, oldTile, tile);
    }

    public void setDefaultTexture(String Key) {
        setDefaultTexture(Key, false);
    }

    public void setDefaultTexture(String Key, boolean update) {
        for (Vector2Int coord : tileData.keySet()) {
            tileData.put(coord, tileData.get(coord).cloneChangedTextureKey(getTextureKey(Key)));
        }
        if (update) {
            updateTileListeners(new TileChangeEvent(null, new HexTile(Byte.MIN_VALUE, getTextureKey(Key)), new HexTile(Byte.MIN_VALUE, Byte.MIN_VALUE)));
        }
    }

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
            neighbours[i] = tileData.get(coords[i].getAsOffset());
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
            l.tileChange(tce);
        }
    }

    /**
     * Call/Update all registered Chunk listener with the last chunk event.
     *
     * @param cce Last chunk event.
     */
//    private void updateChunk(ChunkChangeEvent cce) {
//        for (ChunkChangeListener l : chunkListeners) {
//            l.chunkUpdate(cce);
//        }
//    }
    /**
     * Save the current map in a folder of the same name of the map.
     *
     *
     * @param mapName "RESET" && "TEMP" cannot be used for a map name since they
     * are already be used internaly.
     */
    public boolean saveArea(String mapName) {
        if (mapName == null || mapName.toUpperCase(Locale.ENGLISH).equalsIgnoreCase("TEMP")) {
            Logger.getLogger(MapData.class.getName()).log(Level.WARNING, "Invalid Path name");
            return false;
        }
        this.mapName = mapName;
        try {
            if (saveChunk(null)) {
                String userHome = System.getProperty("user.dir") + "/assets";
                BinaryExporter exporter = BinaryExporter.getInstance();
                org.hexgridapi.loader.MapDataLoader mdLoader = new org.hexgridapi.loader.MapDataLoader();

//                mdLoader.setChunkPos(chunkPos);

                File file = new File(userHome + "/Data/MapData/" + mapName + "/" + mapName + ".map");
                exporter.save(mdLoader, file);
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(MapData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Save the selected chunk, two main use as :
     * - When saving the map
     * - When removing data from the memory.
     *
     * @param position the chunk to save.
     * @throws IOException
     */
    private boolean saveChunk(Vector2Int position) throws IOException {
        String userHome = System.getProperty("user.dir") + "/assets";

        BinaryExporter exporter = BinaryExporter.getInstance();
        ChunkDataLoader cdLoader = new ChunkDataLoader();

        if (position == null) {
//            for (Vector2Int pos : chunkPos) {
//                Path file = Paths.get(userHome + "/Data/MapData/" + mapName + "/" + pos.toString() + ".chk");
//                HexTile[][] tiles = getChunkTiles(pos);
//                if (tiles == null) {
//                    Path f = Paths.get(userHome + "/Data/MapData/Temp/" + pos.toString() + ".chk");
//                    if (f.toFile().exists() && !f.toFile().isDirectory()) {
//                        CopyOption[] options = new CopyOption[]{
//                            StandardCopyOption.REPLACE_EXISTING,
//                            StandardCopyOption.COPY_ATTRIBUTES
//                        };
//                        Files.copy(f, file, options);
//                    } else {
//                        Logger.getLogger(MapData.class.getName()).log(Level.WARNING,
//                                "userHome + \"/Data/MapData/\" + mapName + \"/\" + pos.toString() \n"
//                                + "                                + \".chk\" + \" can't be saved, data missing.\"");
//                        return false;
//                    }
//                } else {
//                    cdLoader.setChunk(tiles);
//                    exporter.save(cdLoader, file.toFile());
//                }
//            }
            return true;
        } else {
            File file = new File(userHome + "/Data/MapData/Temp/" + position.toString() + ".chk");
//            cdLoader.setChunk(getChunkTiles(position));
            exporter.save(cdLoader, file);
            return true;
        }
    }

    /**
     * Load a map and all the corresponding chunk and return true if done
     * properly, return false otherwise.
     *
     * @param name of the map to load.
     * @return false if not located
     */
    public boolean loadArea(String name) {
        File file = new File(System.getProperty("user.dir") + "/assets/Data/MapData/" + name + "/" + name + ".map");
        if (file.isDirectory() || !file.exists()) {
            Logger.getLogger(MapData.class.getName()).log(Level.WARNING, null, new IOException(name + " can't be found."));
            return false;
        }
        org.hexgridapi.loader.MapDataLoader mdLoader = (org.hexgridapi.loader.MapDataLoader) assetManager.loadAsset("/Data/MapData/" + name + "/" + name + ".map");
        Cleanup();
        mapName = name;
//        chunkPos = mdLoader.getChunkPos();
//        for (byte i = 0; i < chunkPos.size(); i++) {
//            loadChunk(chunkPos.get(i), mapName);
//            updateTile(new ChunkChangeEvent(chunkPos.get(i)));
//        }
        return true;
    }

    /**
     * load the chunk from a specifiate folder, internal use.
     *
     * @todo: All Tile data should only be loaded to create the map and not be
     * added into chunkData. (not keeping track of tile data in the memory)
     */
    private void loadChunk(Vector2Int position, String folder) {
        String chunkPath;
        if (folder == null) {
            chunkPath = "/Data/MapData/Temp/" + position.toString() + ".chk";
        } else {
            chunkPath = "/Data/MapData/" + folder + "/" + position.toString() + ".chk";
        }
        File file = new File(System.getProperty("user.dir") + "/assets" + chunkPath);
        if (file.exists() && !file.isDirectory()) {
            ChunkDataLoader cdLoaded = (ChunkDataLoader) assetManager.loadAsset(new AssetKey(chunkPath));
//            chunkData.add(position, cdLoaded.getTiles());
        } else {
            System.err.println(chunkPath + " can't be load, missing data.");
        }
    }

    /**
     * Cleanup the current map.
     */
    public void Cleanup() {
        //Todo remove all file from the temps folder
//        chunkPos.clear();
//        chunkData.clear();
        tileData.clear();
//        updateTile(new TileChangeEvent(null, null, null));
    }

    /**
     *
     * @param textureKey
     * @return null if no corresponding key
     */
    public String getTextureValue(byte textureKey) {
        if (textureKey == (byte) -1) {
            return "SELECTION_TEXTURE";
        } else if (textureKey < (byte) -1) {
            return "NO_TILE";
        } else {
            try {
                return textureKeys.get(textureKey);
            } catch (IndexOutOfBoundsException e) {
                return "EMPTY_TEXTURE_KEY";
            }
        }
    }

    public byte getTextureKey(String value) throws NoSuchFieldError {
        if (value.equals("SELECTION_TEXTURE")) {
            return -1;
        } else if (value.equals("NO_TILE")) {
            return -2;
        }
        byte result = (byte) textureKeys.indexOf(value);
        if (result == -1) {
            throw new NoSuchFieldError(value + " is not in the registered key List.");
        } else {
            return result;
        }
    }

    public List<String> getTextureKeys() {
        return Collections.unmodifiableList(textureKeys);
    }
}