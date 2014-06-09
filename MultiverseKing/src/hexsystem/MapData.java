package hexsystem;

import hexsystem.loader.ChunkDataLoader;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import hexsystem.events.TileChangeListener;
import hexsystem.events.TileChangeEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import hexsystem.events.ChunkChangeEvent;
import hexsystem.events.ChunkChangeListener;
import hexsystem.loader.MapDataLoader;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.ElementalAttribut;

/**
 * This class holds the hex data of the map.
 *
 * @todo move all converter to another class this will clean MapData.
 * @author Eike Foede, Roah
 */
public final class MapData {

    private final AssetManager assetManager;
    private ChunkData chunkData;
    private ElementalAttribut mapElement;
    private ArrayList<Vector2Int> chunkPos = new ArrayList<Vector2Int>();
    private ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private ArrayList<ChunkChangeListener> chunkListeners = new ArrayList<ChunkChangeListener>();
    private String mapName;// = "Reset";

    /**
     * Base constructor.
     *
     * @param eAttribut
     * @param assetManager
     */
    public MapData(ElementalAttribut eAttribut, AssetManager assetManager) {
        this.assetManager = assetManager;
        mapElement = eAttribut;
        chunkData = new ChunkData();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * @return current map element.
     */
    public ElementalAttribut getMapElement() {
        return mapElement;
    }

    /**
     * @return current map name.
     */
    public String getMapName() {
        return this.mapName;
    }

    public ArrayList<Vector2Int> getAllChunkPos() {
        return chunkPos;
    }

    //todo: refresh method, when the mapElement is change but the chunk isn't on memory, the chunk when loaded should be refreshed to get the right element.
    /**
     *
     * @param eAttribut
     */
    public void setMapElement(ElementalAttribut eAttribut) {
        mapElement = eAttribut;
        chunkData.setAllTile(mapElement);
        chunkEvent(new ChunkChangeEvent(Vector2Int.INFINITY));
    }

    /**
     * Add a specifiate chunk in mapData at choosen position.
     *
     * @param chunkPos Where to put the chunk.
     * @param tiles
     */
    public void addChunk(Vector2Int chunkPos, HexTile[][] tiles) {
        if (tiles == null) {
            tiles = new HexTile[HexSettings.CHUNK_SIZE][HexSettings.CHUNK_SIZE];
            for (int y = 0; y < HexSettings.CHUNK_SIZE; y++) {
                for (int x = 0; x < HexSettings.CHUNK_SIZE; x++) {
                    tiles[x][y] = new HexTile(mapElement, HexSettings.GROUND_HEIGHT);
                }
            }
        }

        chunkData.add(chunkPos, tiles);
        this.chunkPos.add(chunkPos);
        ChunkChangeEvent cce = new ChunkChangeEvent(chunkPos);
        chunkEvent(cce);
    }

    /**
     * @param chunkPos
     * @return All tiles of the requested chunk.
     */
    public HexTile[][] getChunkTiles(Vector2Int chunkPos) {
        return chunkData.getChunkTiles(chunkPos);
    }

    /**
     * Get a tile properties.
     *
     * @todo see below.
     * @param tilePos Offset position of the tile.
     * @return null if the tile doesn't exist.
     */
    public HexTile getTile(HexCoordinate tilePos) {
        Vector2Int chunkPosition = getChunkGridPos(tilePos);
        if (chunkPosition != null) {
            tilePos = getChunkTilePos(tilePos);
            for (Vector2Int pos : chunkPos) {
                if (pos.equals(chunkPosition)) {
                    HexTile tile = chunkData.getTile(chunkPosition, tilePos);
                    if (tile != null) {
                        return tile;
                    } else {
                        //todo :
                        //Check for the file if the chunk exist, if not return null
                        //Load the file and check for the tile.
                        //If still null, the tile doesn't exist so return null
                        System.err.println("Chunk data to load haven't been found or tile does not exist. Requested Tile : " + tilePos);
                    }
                }
            }
        }
        //Normal behavior when looking for Neightbors or hexRange
//        System.err.println("Can't found chunk for " + tilePos); 
        return null;
    }

    /**
     * Check if a tile exist
     *
     * @param tilePos Offset position of the tile.
     * @return false if the tile doesn't exist.
     */
    public boolean tileExist(HexCoordinate tilePos) {
        Vector2Int chunkPosition = getChunkGridPos(tilePos);
        if (chunkPosition != null) {
            tilePos = getChunkTilePos(tilePos);
            for (Vector2Int pos : chunkPos) {
                if (pos.equals(chunkPosition) && chunkData.exist(chunkPosition, tilePos)) {
                    return true;
                } else if (pos.equals(chunkPosition) && !chunkData.exist(chunkPosition, tilePos)) {
                    //todo :
                    //Check for the file if the chunk exist, if not return false.
                    //Load the file and check for the tile, if no tile return false.
                    System.err.println("Chunk data to load haven't been found or tile does not exist. Requested Tile : " + tilePos);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Change the designed tile properties.
     *
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexCoordinate tilePos, HexTile tile) {
        Vector2Int chunkPosition = getChunkGridPos(tilePos);
        tilePos = getChunkTilePos(tilePos);
        HexTile oldTile = chunkData.getTile(chunkPosition, tilePos);
        if (oldTile != null) {
            chunkData.setTile(getChunkGridPos(tilePos), tilePos, tile);
            TileChangeEvent tce = new TileChangeEvent(chunkPosition, tilePos, oldTile, tile);
            for (TileChangeListener l : tileListeners) {
                l.tileChange(tce);
            }
        }
    }

    /**
     * @param tilePos
     * @param height
     * @todo
     */
    public void setTileHeight(HexCoordinate tilePos, byte height) {
        setTile(tilePos, getTile(tilePos).cloneChangedHeight(height));
    }

    /**
     * Return null field for inexisting hex.
     *
     * @todo not fully functional.
     * @param position
     * @param range
     * @return
     */
    public HexTile[] getTileRange(HexCoordinate position, int range) {
        Vector2Int axial = position.getAsAxial();
        HexTile[] result = new HexTile[range * 6];
        int i = 0;
        for (int x = -range; x <= range; x++) {
            for (int y = Math.max(-range, -x - range); y <= Math.min(range, range - y); y++) {
                result[i] = getTile(new HexCoordinate(HexCoordinate.AXIAL, new Vector2Int(x + axial.x, y + axial.y)));
                i++;
            }
        }
        return result;
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
     * Register a listener to respond to chunk Event. Work outside the entity
     * system.
     *
     * @param listener to register.
     */
    public void registerChunkChangeListener(ChunkChangeListener listener) {
        chunkListeners.add(listener);
        if (listener instanceof TileChangeListener) {
            registerTileChangeListener((TileChangeListener) listener);
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
     * Return the chunk who hold the specifiated tile.
     *
     * @param tilePos hexMap coordinate of the tile.
     * @return Position of the chunk in mapData if exist, else null.
     */
    public Vector2Int getChunkGridPos(HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        int x = (int) (FastMath.abs(tileOffset.x) / HexSettings.CHUNK_SIZE);
        int y = (int) (FastMath.abs(tileOffset.y)) / HexSettings.CHUNK_SIZE;
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
     * Convert tile hexMap position to chunk position, return null if the chunk
     * didn't exist.
     *
     * @param tilePos tile hexMap position to convert.
     * @return tile chunk position.
     */
    public HexCoordinate getChunkTilePos(HexCoordinate tilePos) {
        Vector2Int chunk = getChunkGridPos(tilePos);
        if (chunk != null) {
            Vector2Int tileOffset = tilePos.getAsOffset();
            return new HexCoordinate(HexCoordinate.OFFSET,
                    (int) (FastMath.abs(tileOffset.x) - (FastMath.abs(chunk.x) * HexSettings.CHUNK_SIZE)),
                    (int) (FastMath.abs(tileOffset.y) - FastMath.abs(chunk.y) * HexSettings.CHUNK_SIZE));
        } else {
            //Normal behavior when looking for Neightbors or hexRange
//            System.err.println("Chunk does not exits in current context, even in Temp. Requested tile : "+tilePos);
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
    public Vector3f getChunkWorldPosition(Vector2Int position) {
        return new Vector3f((position.x * HexSettings.CHUNK_SIZE) * HexSettings.HEX_WIDTH, 0,
                (position.y * HexSettings.CHUNK_SIZE) * (float) (HexSettings.HEX_RADIUS * 1.5));
    }

//    /**
//     * Convert Hex grid position to world position. Convertion work with Odd-R
//     * Offset grid type. (currently used grid type).
//     *
//     * @return tile world unit position.
//     */
//    public Vector3f convertHexCoordinateToWorldPosition(HexCoordinate tilePos) {
//        Vector2Int offsetPos = tilePos.getAsOffset();
//        return new Vector3f((offsetPos.x) * HexSettings.HEX_WIDTH
//                + ((offsetPos.y & 1) == 0 ? 0 : HexSettings.HEX_WIDTH / 2), 0.05f, offsetPos.y * HexSettings.HEX_RADIUS * 1.5f);
//    }

    /**
     * Convert Hex grid position to world position and check if the tile exist.
     * /!\ Return a value only if the tile exist. use getTileWorldPosition() if
     * only a value is needed. 
     * Convertion work with Odd-R Offset grid type.(currently used grid type).
     *
     * @return tile world unit position if exist.
     */
    public Vector3f convertTileToWorldPosition(HexCoordinate hexPos) {
        HexTile tile = getTile(hexPos);
        if (tileExist(hexPos)) {
            int height = tile.getHeight();
            Vector3f spat = hexPos.convertToWorldPosition();
            return new Vector3f(spat.x, height*HexSettings.FLOOR_OFFSET, spat.z);
        } else {
            System.err.println("There is no Tile on the position " + hexPos.toString());
            return null;
        }
    }

    /**
     * Convert World Position to Hex grid position. Vector3f to Odd-R Offset
     * grid position.
     *
     * @param pos position to convert.
     * @return converted grid position.
     */
    public HexCoordinate convertWorldToGridPosition(Vector3f pos) {
        float x = pos.x;
        float z = pos.z + HexSettings.HEX_RADIUS;
        x = x / HexSettings.HEX_WIDTH;

        float t1 = z / HexSettings.HEX_RADIUS, t2 = FastMath.floor(x + t1);
        float r = FastMath.floor((FastMath.floor(t1 - x) + t2) / 3);
        float q = FastMath.floor((FastMath.floor(2 * x + 1) + t2) / 3) - r;

        return new HexCoordinate(HexCoordinate.AXIAL, new Vector2Int((int) q, (int) r));
    }

    /**
     * load a map and all the corresponding chunk and return true if done
     * properly, return false otherwise.
     *
     * @param name of the map to load.
     * @return false if not located
     */
    public boolean loadMap(String name) {
        File file = new File(System.getProperty("user.dir") + "/assets/Data/MapData/" + name + "/" + name + ".map");
        if (file.isDirectory() || !file.exists()) {
            Logger.getLogger(MapData.class.getName()).log(Level.WARNING, null, new IOException(mapName + " can't be found."));
            return false;
        }
        MapDataLoader mdLoader = (MapDataLoader) assetManager.loadAsset("/Data/MapData/" + name + "/" + name + ".map");
        mapName = mdLoader.getMapName();
        mapElement = mdLoader.getMapElement();
        chunkPos = mdLoader.getChunkPos();
        chunkData.clear();
        chunkEvent(new ChunkChangeEvent(true));
        for (byte i = 0; i < chunkPos.size(); i++) {
            loadChunk(chunkPos.get(i), mapName);
            chunkEvent(new ChunkChangeEvent(chunkPos.get(i)));
        }
        return true;
    }

    /**
     * Save the current map in a folder of the same name of the map.
     *
     * @throws IOException
     */
    public boolean saveMap(String mapName) {
        if (mapName == null || mapName.toUpperCase(Locale.ENGLISH).equalsIgnoreCase("RESET") || mapName.toUpperCase(Locale.ENGLISH).equalsIgnoreCase("TEMP")) {
            Logger.getLogger(MapData.class.getName()).log(Level.WARNING, "Invalid Path name");
            return false;
        }
        try {
            if (saveChunk(Vector2Int.INFINITY)) {
                this.mapName = mapName;
                String userHome = System.getProperty("user.dir") + "/assets";
                BinaryExporter exporter = BinaryExporter.getInstance();
                MapDataLoader mdLoader = new MapDataLoader();

                mdLoader.setMapName(mapName);
                mdLoader.setMapElement(mapElement);
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

        if (position == Vector2Int.INFINITY) {
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
     * load the chunk from speciate folder, internal use.
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
     *
     * @return
     */
    public Set<Entry<Vector2Int, HexTile[][]>> getAllChunks() {
        return chunkData.getAllChunks();
    }

    /**
     *
     */
    public void Cleanup() {
        //Todo remove all file from the temps folder
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
     * Remove listener from event on Chunk.
     *
     * @param listener
     */
    public void removeChunkChangeListener(ChunkChangeListener listener) {
        chunkListeners.remove(listener);
    }
}