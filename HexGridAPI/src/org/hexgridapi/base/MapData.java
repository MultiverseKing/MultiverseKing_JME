package org.hexgridapi.base;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import org.hexgridapi.events.ChunkChangeEvent;
import org.hexgridapi.events.ChunkChangeListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.loader.ChunkDataLoader;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * This class holds the hex data of the map.
 *
 * @todo: refresh method, when the mapElement is change but the chunk isn't on
 * memory, the chunk when loaded should be refreshed to get the right element.
 * @todo move all converter to another class this will clean MapData.
 * @author Eike Foede, Roah
 */
public class MapData {

    protected final AssetManager assetManager;
    protected ChunkData chunkData;
    protected ArrayList<Vector2Int> chunkPos = new ArrayList<Vector2Int>();
    protected ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    protected ArrayList<ChunkChangeListener> chunkListeners = new ArrayList<ChunkChangeListener>();
    protected String mapName;// = "Reset";
    /**
     * key index :
     * -1 (inclusive) and below used for areaTexture;
     * 0 used for EMPTY_TEXTURE_KEY (when specifiating no texture);
     * 1 (inclusive) and above user for added texture (ordered the way the get added).
     */
    private final String[] textureKeys;

    public MapData(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.textureKeys = new String[]{"EMPTY_TEXTURE_KEY"};
        chunkData = new ChunkData();
    }
    
    public MapData(Enum[] textureKeys, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.textureKeys = genTextureKeys(textureKeys);
        chunkData = new ChunkData();
    }

    public MapData(String[] textureKeys, AssetManager assetManager) {
        this.assetManager = assetManager;
//        assetManager.registerLocator("HexGridRessource.zip", ZipLocator.class);
        this.textureKeys = genTextureKeys(textureKeys);
        chunkData = new ChunkData();
    }
    
    private String[] genTextureKeys(Object[] userKey){
        String[] keys = new String[userKey.length+1];
        keys[0] = "EMPTY_TEXTURE_KEY";
        for (int i = 0; i < userKey.length; i++) {
            keys[i+1] = userKey[i].toString();
        }
        return keys;
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
     * Register a listener to respond to chunk Event. Work outside the entity
     * system.
     *
     * @param listener to register.
     */
    public void registerChunkChangeListener(ChunkChangeListener listener) {
        chunkListeners.add(listener);
        for (Vector2Int vect : chunkPos) {
            listener.chunkUpdate(new ChunkChangeEvent(vect));
        }
    }

    /**
     * Remove listener from event on Chunk.
     *
     * @param listener
     */
    public void removeChunkChangeListener(ChunkChangeListener listener) {
        chunkListeners.remove(listener);
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
        return !chunkData.isEmpty();
    }

    /**
     * @param chunkPos
     * @return All tiles of the requested chunk.
     */
    public HexTile[][] getChunkTiles(Vector2Int chunkPos) {
        return chunkData.getChunkTiles(chunkPos);
    }

    public List<Vector2Int> getAllChunkPos() {
        return Collections.unmodifiableList(chunkPos);
    }

    /**
     * Add a specifiate chunk in mapData at choosen position. /!\ If the chunk
     * already exist it will be overrided.
     *
     * @param chunkPos position where to add the chunk.
     * @param tiles set to new to create new chunk.
     */
    public void addChunk(Vector2Int chunkPos, HexTile[][] tiles) {
        if (tiles == null) {
            tiles = new HexTile[HexSetting.CHUNK_SIZE][HexSetting.CHUNK_SIZE];
            for (int y = 0; y < HexSetting.CHUNK_SIZE; y++) {
                for (int x = 0; x < HexSetting.CHUNK_SIZE; x++) {
                    tiles[x][y] = new HexTile(HexSetting.GROUND_HEIGHT);
                }
            }
        }

        chunkData.add(chunkPos, tiles);
        this.chunkPos.add(chunkPos);
        chunkEvent(new ChunkChangeEvent(chunkPos));
    }

    /**
     * Get a tile properties.
     *
     * @todo see below.
     * @param tilePos Offset position of the tile.
     * @return null if the tile doesn't exist.
     */
    public HexTile getTile(HexCoordinate tilePos) {
        Vector2Int chunkPosition = getChunk(tilePos);
        if (chunkPosition != null) {
            tilePos = getTilePosInChunk(tilePos);
            for (Vector2Int pos : chunkPos) {
                if (pos.equals(chunkPosition)) {
                    HexTile tile = chunkData.getTile(chunkPosition, tilePos);
                    if (tile != null) {
                        return tile;
//                    } else if (false) { 
//                        //todo : Check for the file if the chunk exist, if not return null
//                        //Load the file and check for the tile.
//                        //If still null, the tile doesn't exist so return null
//                        System.err.println("Chunk data to load haven't been found or tile does not exist. Requested Tile : " + tilePos);
                    } else {
                        return null;
                    }
                }
            }
        }
        //Normal behavior when looking for Neightbors or hexRange
//        System.err.println("Can't found chunk for " + tilePos); 
        return null;
    }

    /**
     * Change the designed tile properties.
     *
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexCoordinate tilePos, HexTile tile) {
        Vector2Int chunkPosition = getChunk(tilePos);
        tilePos = getTilePosInChunk(tilePos);
        HexTile oldTile = chunkData.getTile(chunkPosition, tilePos);
        if (oldTile != null) {
            chunkData.setTile(getChunk(tilePos), tilePos, tile);
            TileChangeEvent tce = new TileChangeEvent(chunkPosition, tilePos, oldTile, tile);
            for (TileChangeListener l : tileListeners) {
                l.tileChange(tce);
            }
        }
    }

    public void setTileHeight(HexCoordinate tilePos, byte height) {
        setTile(tilePos, getTile(tilePos).cloneChangedHeight(height));
    }

    public void setTileTextureKey(HexCoordinate tilePos, String key) {
        setTile(tilePos, getTile(tilePos).cloneChangedTextureKey(getTextureKey(key)));
    }

    public void setMapTexture(String Key) {
        chunkData.setAllTile(null, getTextureKey(Key));
    }

    /**
     * Get all tile around the defined position, return null for tile who
     * doesn't exist.
     *
     * @param position of the center tile.
     * @return All tile arround the needed tile.
     */
    public HexTile[] getNeightbors(HexCoordinate position) {
        HexCoordinate[] coords = position.getNeighbours();
        HexTile[] neighbours = new HexTile[coords.length];
        for (int i = 0; i < neighbours.length; i++) {
            neighbours[i] = getTile(coords[i]);
        }
        return neighbours;
    }

    /**
     * call/Update all registered Chunk listener with the last chunk event.
     *
     * @param cce Last event.
     */
    private void chunkEvent(ChunkChangeEvent cce) {
        for (ChunkChangeListener l : chunkListeners) {
            l.chunkUpdate(cce);
        }
    }

    /**
     * Return the tile position inside his corresponding chunk.
     *
     * @param tilePos tile hexMap position to convert.
     * @return tile chunk position or null.
     */
    public HexCoordinate getTilePosInChunk(HexCoordinate tilePos) {
        Vector2Int chunk = getChunk(tilePos);
        if (chunk != null) {
            Vector2Int tileOffset = tilePos.getAsOffset();
            return new HexCoordinate(HexCoordinate.OFFSET,
                    (int) (FastMath.abs(tileOffset.x) - (FastMath.abs(chunk.x) * HexSetting.CHUNK_SIZE)),
                    (int) (FastMath.abs(tileOffset.y) - FastMath.abs(chunk.y) * HexSetting.CHUNK_SIZE));
        } else {
            //Normal behavior when looking for Neightbors or hexRange
//            System.err.println("Chunk does not exits in current context, even in Temp. Requested tile : "+tilePos);
            return null;
        }
    }

    /**
     * Return the chunk who hold the specifiated tile.
     *
     * @param tilePos hexMap coordinate of the tile.
     * @return Position of the chunk in mapData if exist, else null.
     */
    public Vector2Int getChunk(HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        int x = (int) (FastMath.abs(tileOffset.x) / HexSetting.CHUNK_SIZE);
        int y = (int) (FastMath.abs(tileOffset.y)) / HexSetting.CHUNK_SIZE;
        Vector2Int result = new Vector2Int(((tileOffset.x < 0) ? x * -1 : x), ((tileOffset.y < 0) ? y * -1 : y));
        if (chunkPos.contains(result)) {
            return result;
        } else {
            //Normal behavior when looking for Neightbors or hexRange
//            System.err.println("Chunk does not exist in the current context. Requested tile : "+tilePos+ ". Corresponding Chunk : " + result); 
            return null;
        }
    }

    /**
     * Convert chunk position in hexMap to world unit.
     *
     * @param position
     * @hint chunk world unit position is the same than the chunk node.
     * @return chunk world unit position.
     */
    public static Vector3f getChunkWorldPosition(Vector2Int position) {
        return new Vector3f((position.x * HexSetting.CHUNK_SIZE) * HexSetting.HEX_WIDTH, 0,
                (position.y * HexSetting.CHUNK_SIZE) * (float) (HexSetting.HEX_RADIUS * 1.5));
    }

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

                mdLoader.setChunkPos(chunkPos);

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
     * Save the selected chunk, two main use as : when saving the map, when
     * removing data from the memory.
     *
     * @param position the chunk to save.
     * @throws IOException
     */
    private boolean saveChunk(Vector2Int position) throws IOException {
        String userHome = System.getProperty("user.dir") + "/assets";

        BinaryExporter exporter = BinaryExporter.getInstance();
        ChunkDataLoader cdLoader = new ChunkDataLoader();

        if (position == null) {
            for (Vector2Int pos : chunkPos) {
                Path file = Paths.get(userHome + "/Data/MapData/" + mapName + "/" + pos.toString() + ".chk");
                HexTile[][] tiles = getChunkTiles(pos);
                if (tiles == null) {
                    Path f = Paths.get(userHome + "/Data/MapData/Temp/" + pos.toString() + ".chk");
                    if (f.toFile().exists() && !f.toFile().isDirectory()) {
                        CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES
                        };
                        Files.copy(f, file, options);
                    } else {
                        Logger.getLogger(MapData.class.getName()).log(Level.WARNING,
                                "userHome + \"/Data/MapData/\" + mapName + \"/\" + pos.toString() \n"
                                + "                                + \".chk\" + \" can't be saved, data missing.\"");
                        return false;
                    }
                } else {
                    cdLoader.setChunk(tiles);
                    exporter.save(cdLoader, file.toFile());
                }
            }
            return true;
        } else {
            File file = new File(userHome + "/Data/MapData/Temp/" + position.toString() + ".chk");
            cdLoader.setChunk(getChunkTiles(position));
            exporter.save(cdLoader, file);
            return true;
        }
    }

    /**
     * load a map and all the corresponding chunk and return true if done
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
        chunkPos = mdLoader.getChunkPos();
        for (byte i = 0; i < chunkPos.size(); i++) {
            loadChunk(chunkPos.get(i), mapName);
            chunkEvent(new ChunkChangeEvent(chunkPos.get(i)));
        }
        return true;
    }

    /**
     * load the chunk from a specifiate folder, internal use.
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
            chunkData.add(position, cdLoaded.getTiles());
        } else {
            System.err.println(chunkPath + " can't be load, missing data.");
        }
    }

    /**
     * Cleanup the current map.
     */
    public void Cleanup() {
        //Todo remove all file from the temps folder
        chunkPos.clear();
        chunkData.clear();
        chunkEvent(new ChunkChangeEvent(true));
    }

    public String getTextureValue(byte textureKey) {
        if (textureKey == (byte)-1) {
            return "AREA_TEXTURE";
        } else  if (textureKey < (byte)-1) {
            return "NO_TILES";
        }else {
            return textureKeys[textureKey];
        }
    }

    public byte getTextureKey(String value) {
        if(value.equals("AREA_TEXTURE")){
            return -1;
        } else if(value.equals("NO_TILES")){
            return -2;
        }
        for (byte i = 0; i < textureKeys.length; i++) {
            if (textureKeys[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
}