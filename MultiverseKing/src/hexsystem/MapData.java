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
import java.util.Map.Entry;
import java.util.Set;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * This class holds the hex data of the map.
 * @todo move all converter to another class this will clean MapData.
 * @author Eike Foede, Roah
 */
public final class MapData {

    private final AssetManager assetManager;
    private ChunkData chunkData;
    private HexSettings hexSettings;
    private ElementalAttribut mapElement;
    private ArrayList<Vector2Int> chunkPos = new ArrayList<Vector2Int>();
    private ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private ArrayList<ChunkChangeListener> chunkListeners = new ArrayList<ChunkChangeListener>();
    private String mapName = "Reset";

    /**
     * Base constructor.
     */
    public MapData(ElementalAttribut eAttribut, AssetManager assetManager) {
        this.hexSettings = new HexSettings();
        this.assetManager = assetManager;
        mapElement = eAttribut;
        chunkData = new ChunkData(hexSettings.getCHUNK_DATA_LIMIT());
    }

    /**
     * Global Settings for hex.
     * @return parameters.
     */
    public HexSettings getHexSettings() {
        return hexSettings;
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

    //todo: refresh method, when the mapElement is change but the chunk isn't on memory, the chunk when loaded should be refreshed to get the right element.
    public void setMapElement(ElementalAttribut eAttribut) {
        mapElement = eAttribut;
        chunkData.setAllTile(mapElement);
        chunkEvent(new ChunkChangeEvent(Vector2Int.INFINITY));
    }

    /**
     * Add a specifiate chunk in mapData at choosen position.
     * @param chunkPos Where to put the chunk.
     */
    public void addChunk(Vector2Int chunkPos, HexTile[][] tiles) {
        if (tiles == null) {
            tiles = new HexTile[hexSettings.getCHUNK_SIZE()][hexSettings.getCHUNK_SIZE()];
            for (int y = 0; y < hexSettings.getCHUNK_SIZE(); y++) {
                for (int x = 0; x < hexSettings.getCHUNK_SIZE(); x++) {
                    tiles[x][y] = new HexTile(mapElement, hexSettings.getGROUND_HEIGHT());
                }
            }
        }

        chunkData.add(chunkPos, tiles);
        this.chunkPos.add(chunkPos);
        ChunkChangeEvent cce = new ChunkChangeEvent(chunkPos);
        chunkEvent(cce);
    }

    /**
     * @return All tiles of the requested chunk.
     */
    public HexTile[][] getChunkTiles(Vector2Int chunkPos) {
        return chunkData.getChunkTiles(chunkPos);
    }

    /**
     * Get a tile properties.
     * @todo see below.
     * @param tilePos Offset position of the tile.
     * @return null if the tile doesn't exist.
     */
    public HexTile getTile(HexCoordinate tilePos) {
//        return chunkData.getTile(getChunkGridPos(tilePos), tilePos);
        Vector2Int chunkPosition = getChunkGridPos(tilePos);
        tilePos = getChunkTilePos(tilePos);
        for (Vector2Int pos : chunkPos) {
            if (pos.equals(chunkPosition)) {
                HexTile tile = chunkData.getTile(chunkPosition, tilePos);
                if (tile != null) {
                    return tile;
                } else {
                    //todo
                    //Load the chunk from the file and get the tile
                    //If still null the tile doesn't exist so return null
                }
            }
        }
        return null;
    }

    /**
     * Change the designed tile properties.
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(HexCoordinate tilePos, HexTile tile) {
        Vector2Int chunkPosition = getChunkGridPos(tilePos);
        tilePos = getChunkTilePos(tilePos);
        TileChangeEvent tce = new TileChangeEvent(chunkPosition, tilePos, chunkData.getTile(chunkPosition, tilePos), tile);
        if (tce.getOldTile() != null) {
            chunkData.setTile(getChunkGridPos(tilePos), tilePos, tile);
            for (TileChangeListener l : tileListeners) {
                l.tileChange(tce);
            }
        }
    }

    /**
     * @todo
     */
    public void setTileHeight(HexCoordinate tilePos, byte height) {
        setTile(tilePos, getTile(tilePos).cloneChangedHeight(height));
    }

    /**
     * Return null field for inexisting hex.
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
     * Register a listener to respond to chunk Event.
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
     * @param listener to register.
     */
    public void registerTileChangeListener(TileChangeListener listener) {
        tileListeners.add(listener);
    }

    /**
     * call/Update all registered Chunk listener with the last chunk event.
     * @param cce Last event.
     */
    private void chunkEvent(ChunkChangeEvent cce) {
        for (ChunkChangeListener l : chunkListeners) {
            l.chunkUpdate(cce);
        }
    }

    /**
     * Return the chunk who hold the specifiated tile.
     * @param tilePos hexMap coordinate of the tile.
     * @return Position of the chunk in mapData if exist, else null.
     */
    public Vector2Int getChunkGridPos(HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        int x = tileOffset.x / hexSettings.getCHUNK_SIZE();
        int y = tileOffset.y / hexSettings.getCHUNK_SIZE();
        Vector2Int result = new Vector2Int(((tileOffset.x < 0) ? x - 1 : x), ((tileOffset.y < 0) ? y - 1 : y));
        if(this.chunkPos.contains(result)){
            return result;
        } else {
            return null;
        }
    }

    /**
     * Convert tile hexMap position to chunk position, return null if the chunk didn't exist.
     * @param tilePos tile hexMap position to convert.
     * @return tile chunk position.
     */
    public HexCoordinate getChunkTilePos(HexCoordinate tilePos) {
        Vector2Int chunk = getChunkGridPos(tilePos);
        if(chunk != null) {
            Vector2Int tileOffset = tilePos.getAsOffset();
            return new HexCoordinate(HexCoordinate.OFFSET, tileOffset.x - chunk.x * hexSettings.getCHUNK_SIZE(), tileOffset.y - chunk.y * hexSettings.getCHUNK_SIZE());
        } else {
            return null;
        }
    }

    /**
     * Convert chunk position in hexMap to world unit.
     * @hint chunk world unit position is the same than the chunk node.
     * @return chunk world unit position.
     */
    public Vector3f getChunkWorldPosition(Vector2Int position) {
        return new Vector3f((position.x * hexSettings.getCHUNK_SIZE()) * hexSettings.getHEX_WIDTH(), 0,
                (position.y * hexSettings.getCHUNK_SIZE()) * (float) (hexSettings.getHEX_RADIUS() * 1.5));
    }

    /**
     * Convert Odd-R Offset tile hexMap position to world unit.
     * @param offsetPos position to convert.
     * @return tile world unit position.
     */
    public Vector3f getTileWorldPosition(HexCoordinate tilePos) {
        Vector2Int offsetPos = tilePos.getAsOffset();
        return new Vector3f((offsetPos.x) * hexSettings.getHEX_WIDTH() + ((offsetPos.y & 1) == 0 ? 0 : hexSettings.getHEX_WIDTH() / 2), 0.05f, offsetPos.y * hexSettings.getHEX_RADIUS() * 1.5f);
    }

    /**
     * Convert World Position to Odd-R Offset grid position.
     * @param pos position to convert.
     * @return converted grid position.
     */
    public HexCoordinate convertWorldToGridPosition(Vector3f pos) {
        float x = pos.x;
        float z = pos.z + hexSettings.getHEX_RADIUS();
        x = x / hexSettings.getHEX_WIDTH();

        float t1 = z / hexSettings.getHEX_RADIUS(), t2 = FastMath.floor(x + t1);
        float r = FastMath.floor((FastMath.floor(t1 - x) + t2) / 3);
        float q = FastMath.floor((FastMath.floor(2 * x + 1) + t2) / 3) - r;

        return new HexCoordinate(HexCoordinate.AXIAL, new Vector2Int((int) q, (int) r));
    }

    /**
     * Save the current map in a folder of the same name of the map.
     * @throws IOException 
     */
    public void saveMap() throws IOException {
        String userHome = System.getProperty("user.dir") + "/assets";
        BinaryExporter exporter = BinaryExporter.getInstance();
        MapDataLoader mdLoader = new MapDataLoader();

        mdLoader.setMapName(mapName);
        mdLoader.setMapElement(mapElement);
        mdLoader.setChunkPos(chunkPos);

        File file = new File(userHome + "/MapData/" + mapName + "/" + mapName + ".map");
        exporter.save(mdLoader, file);
        saveChunk(Vector2Int.INFINITY);
    }

    /**
     * @param name of the map to load.
     */
    public void loadMap(String name) {
        MapDataLoader mdLoader = (MapDataLoader) assetManager.loadAsset(new AssetKey("MapData/" + name + "/" + name + ".map"));
        mapName = mdLoader.getMapName();
        mapElement = mdLoader.getMapElement();
        chunkPos = mdLoader.getChunkPos();
        chunkData.clear();
        chunkEvent(new ChunkChangeEvent(true));
        for (byte i = 0; i < chunkPos.size(); i++) {
            loadChunk(chunkPos.get(i), mapName);
            chunkEvent(new ChunkChangeEvent(chunkPos.get(i)));
        }
    }

    /**
     * Save the selected chunk, two main use as : when saving the map, when removing data from the memory.
     * @param position the chunk to save.
     * @throws IOException
     */
    public void saveChunk(Vector2Int position) throws IOException {
        String userHome = System.getProperty("user.dir") + "/assets";
        BinaryExporter exporter = BinaryExporter.getInstance();
        ChunkDataLoader cdLoader = new ChunkDataLoader();

        if (position == Vector2Int.INFINITY) {
            for (Vector2Int pos : chunkPos) {
                Path file = Paths.get(userHome + "/MapData/" + mapName + "/" + pos.toString() + ".chk");
                HexTile[][] tiles = getChunkTiles(pos);
                if (tiles == null) {
                    Path f = Paths.get(userHome + "/MapData/Temp/" + pos.toString() + ".chk");
                    if (f.toFile().exists() && !f.toFile().isDirectory()) {
                        CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES
                        };
                        Files.copy(f, file, options);
                    } else {
                        System.err.println(userHome + "/MapData/Temp/" + pos.toString() + ".chk" + " can't be save, missing data.");
                    }
                } else {
                    cdLoader.setChunk(tiles);
                    exporter.save(cdLoader, file.toFile());
                }

            }
        } else {
            File file = new File(userHome + "/SavedZone/Temp/" + position.toString() + ".chk");
            cdLoader.setChunk(getChunkTiles(position));
            exporter.save(cdLoader, file);
        }
    }

    /**
     * load the chunk from speciate folder, internal use.
     */
    private void loadChunk(Vector2Int position, String folder) {
        String chunkPath;
        if (folder == null) {
            chunkPath = "/MapData/Temp/" + position.toString() + ".chk";
        } else {
            chunkPath = "/MapData/" + folder + "/" + position.toString() + ".chk";
        }
        File file = new File(System.getProperty("user.dir") + "/assets" + chunkPath);
        if (file.exists() && !file.isDirectory()) {
            ChunkDataLoader cdLoaded = (ChunkDataLoader) assetManager.loadAsset(new AssetKey(chunkPath));
            chunkData.add(position, cdLoaded.getTiles());
        } else {
            System.err.println(chunkPath + " can't be load, missing data.");
        }
    }
    public Set<Entry<Vector2Int, HexTile[][]>> getAllChunks(){
        return chunkData.getAllChunks();
    }
}