package hexsystem;

import hexsystem.events.TileChangeListener;
import hexsystem.events.TileChangeEvent;
import com.jme3.app.state.AbstractAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import utility.Coordinate;
import utility.Coordinate.Offset;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * This class holds the data of the map.
 * @author Eike Foede, Roah
 */
public final class MapData extends AbstractAppState {
    private ChunkData chunkData = new ChunkData();
    private HexSettings hexSettings;
    private ElementalAttribut mapElement;
    private ArrayList<TileChangeListener> listeners = new ArrayList<TileChangeListener>();

    /**
     * Global Settings for hex.
     * @return parameters.
     */
    public HexSettings getHexSettings() {
        return hexSettings;
    }
    
    /**
     * Current Map main element attribut.
     * @return main element of the map.
     */
    public ElementalAttribut getMapElement() {
        return mapElement;
    }
    
    /**
     * Base constructor.
     * @param hexSettings Global properties.
     */
    public MapData(HexSettings hexSettings) {
        this.hexSettings = hexSettings;
    }
    
    /**
     * Add a specifiate chunk in mapData at choosen position.
     * @param chunkPos Where to put the chunk.
     * @todo Remove the chunkPos param ?
     */
    public void addEmptyChunk(Vector2Int chunkPos){
        HexTile[][] tiles = new HexTile[hexSettings.getCHUNK_SIZE()][hexSettings.getCHUNK_SIZE()];
        for(int x = 0; x < hexSettings.getCHUNK_SIZE(); x++){
            for(int y = 0; y < hexSettings.getCHUNK_SIZE(); y++){
                tiles[x][y] = new HexTile(mapElement);
            }
        }
        chunkData.add(chunkPos, tiles);
        System.out.println("ChunkAdded");
    }
    
    /**
     * Get a tile properties.
     * @param tilePos Offset position of the tile.
     * @return null if the tile doesn't exist.
     */
    public HexTile getTile(Offset tilePos) {
        return chunkData.getTile(getChunkGridPos(tilePos), tilePos);
    }
    
    /**
     * Get the selected tile chunk grid position in mapData.
     * @param tilePos Offset coordinate of the tile.
     * @return Position of the chunk in mapData.
     * @todo check if the chunk exist.
     */
    public Vector2Int getChunkGridPos(Offset tilePos) {
        return new Vector2Int(tilePos.q/hexSettings.getCHUNK_SIZE(), tilePos.r/hexSettings.getCHUNK_SIZE());
    }
    
    /**
     * Change the designed tile properties.
     * @param tilePos position of the tile to change.
     * @param tile tile to change.
     */
    public void setTile(Offset tilePos, HexTile tile) {
        Vector2Int chunkPos = getChunkGridPos(tilePos);
        TileChangeEvent tce = new TileChangeEvent(chunkPos, tilePos, chunkData.getTile(chunkPos, tilePos), tile);
        if(tce.getOldTile() != null){
        chunkData.setTile(getChunkGridPos(tilePos), tilePos, tile);
            for (TileChangeListener l : listeners) {
                l.tileChange(tce);
            }
        }
    }
    
    /**
     * @todo
     */
    public void setTileHeight(Offset tilePos, byte height){
        setTile(tilePos, hexTiles[x][y].cloneChangedHeight(height));
    }
    
    /**
     * @todo javadoc (Eike)
     */
    public void registerTileChangeListener(TileChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Change the map main element attribut, "Should not" be used outside of Editor Mode. 
     * @param eAttribut new Element.
     * @todo enable this only in Editor Mode.
     */
    public void setMapElement(ElementalAttribut eAttribut) {
        this.mapElement = eAttribut;
    }

    /**
     * Convert chunk grid position to world position, the position returned == getTileWorldPosition(getChunk(position).getTile(position)).
     * @return chunk world position.
     */
    public Vector3f getChunkWorldPosition(Vector2Int position) {
        return new Vector3f((position.x*hexSettings.getCHUNK_SIZE()) * hexSettings.getHEX_WIDTH() - (hexSettings.getHEX_WIDTH()/2), 0,
                (position.y*hexSettings.getCHUNK_SIZE()) * (float)(hexSettings.getHEX_RADIUS()*1.5));
    }

    /**
     * Convert Odd-R Offset tile grid position to world position.
     * @param offsetPos position to convert.
     * @return world position.
     */
    public Vector3f getTileWorldPosition(Offset offsetPos) {
        return new Vector3f ((offsetPos.q)*hexSettings.getHEX_WIDTH()+((offsetPos.r&1)==0 ? 0 : hexSettings.getHEX_WIDTH()/2), 0.05f, offsetPos.r*hexSettings.getHEX_RADIUS()*1.5f);
    }
    
    /**
     * Convert World Position to Odd-R Offset grid position.
     * @param pos position to convert.
     * @return converted grid position.
     */
    public Coordinate.Offset convertWorldToGridPosition(Vector3f pos) {
        float x = pos.x;
        float z = pos.z+hexSettings.getHEX_RADIUS();
        x = x / hexSettings.getHEX_WIDTH();

        float t1 = z / hexSettings.getHEX_RADIUS(), t2 = FastMath.floor(x + t1);
        float r = FastMath.floor((FastMath.floor(t1 - x) + t2) / 3);
        float q = FastMath.floor((FastMath.floor(2 * x + 1) + t2) / 3) - r;

        return new Coordinate().new Axial((int) q, (int) r).toOffset();
    }
}