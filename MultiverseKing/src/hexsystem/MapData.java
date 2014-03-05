package hexsystem;

import hexsystem.events.TileChangeListener;
import hexsystem.events.TileChangeEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import hexsystem.events.ChunkChangeEvent;
import hexsystem.events.ChunkChangeListener;
import java.util.ArrayList;
import utility.HexCoordinate;
import utility.HexCoordinate.Axial;
import utility.HexCoordinate.Offset;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * This class holds the data of the map.
 * @author Eike Foede, Roah
 */
public final class MapData {
    private ChunkData chunkData;
    private HexSettings hexSettings;
    private ElementalAttribut mapElement;
    private ArrayList<TileChangeListener> tileListeners = new ArrayList<TileChangeListener>();
    private ArrayList<ChunkChangeListener> chunkListeners = new ArrayList<ChunkChangeListener>();

    /**
     * Global Settings for hex.
     * @return parameters.
     */
    public HexSettings getHexSettings() {
        return hexSettings;
    }
    
    /**
     * Base constructor.
     */
    public MapData(ElementalAttribut eAttribut) {
        this.hexSettings = new HexSettings();
        mapElement = eAttribut;
        chunkData = new ChunkData(hexSettings.getCHUNK_DATA_LIMIT());
    }
    
    /**
     * Add a specifiate chunk in mapData at choosen position.
     * @param chunkPos Where to put the chunk.
     */
    public void addEmptyChunk(Vector2Int chunkPos){
        HexTile[][] tiles = new HexTile[hexSettings.getCHUNK_SIZE()][hexSettings.getCHUNK_SIZE()];
        for(int x = 0; x < hexSettings.getCHUNK_SIZE(); x++){
            for(int y = 0; y < hexSettings.getCHUNK_SIZE(); y++){
                tiles[x][y] = new HexTile(mapElement, hexSettings.getGROUND_HEIGHT());
            }
        }
        chunkData.add(chunkPos, tiles);
        chunkEvent(new ChunkChangeEvent(chunkPos, null, tiles));
    }
    
    //todo: refresh method, when the mapElement is change but the chunk isn't on memory, the chunk when loaded should be refreshed to get the right element.
    public void setMapElement(ElementalAttribut eAttribut){
        Iterable<ChunkChangeEvent> cce = chunkData.setAllTile(eAttribut);
        do{
            chunkEvent(cce.iterator().next());
        }while (cce.iterator().hasNext());
    }
    
    private void chunkEvent(ChunkChangeEvent cce){
        for (ChunkChangeListener l : chunkListeners) {
            l.chunkChange(cce);
        }
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
     * Get chunk grid position of a tile.
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
            for (TileChangeListener l : tileListeners) {
                l.tileChange(tce);
            }
        }
    }
    
    /**
     * @todo
     */
    public void setTileHeight(Offset tilePos, byte height){
        setTile(tilePos, getTile(tilePos).cloneChangedHeight(height));
    }
    
    public void registerChunkChangeListener(ChunkChangeListener listener){
        chunkListeners.add(listener);
        if(listener instanceof TileChangeListener){
            registerTileChangeListener((TileChangeListener) listener);
        }
    }
    
    /**
     * @todo javadoc (Eike)
     */
    public void registerTileChangeListener(TileChangeListener listener) {
        tileListeners.add(listener);
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
    public Offset convertWorldToGridPosition(Vector3f pos) {
        float x = pos.x;
        float z = pos.z+hexSettings.getHEX_RADIUS();
        x = x / hexSettings.getHEX_WIDTH();

        float t1 = z / hexSettings.getHEX_RADIUS(), t2 = FastMath.floor(x + t1);
        float r = FastMath.floor((FastMath.floor(t1 - x) + t2) / 3);
        float q = FastMath.floor((FastMath.floor(2 * x + 1) + t2) / 3) - r;

        return new HexCoordinate().new Axial((int) q, (int) r).toOffset();
    }
    
    /**
     * Return null field for inexisting hex.
     * @todo not fully functional.
     * @param position
     * @param range
     * @return 
     */
    public HexTile[] getTileRange(Offset position, int range){
        Axial axial = position.toAxial();
        HexTile[] result = new HexTile[range*6];
        int i = 0;
        for(int x = -range; x <= range; x++) {
            for(int y = Math.max(-range, -x-range); y <= Math.min(range, range-y); y++){
                result[i] = getTile(new HexCoordinate().new Axial(x + axial.q, y+axial.r).toOffset());
                i++;
            }
        }
        return result;
    }
    
    public HexTile[] getNeightbors(Offset position){
       HexCoordinate coordinate = new HexCoordinate();
        return ((position.r&1) == 0 ? 
        new HexTile[] { 
            getTile(coordinate.new Offset(position.q, position.r - 1)), 
            getTile(coordinate.new Offset(position.q - 1, position.r - 1)),
            getTile(coordinate.new Offset(position.q + 1, position.r)), 
            getTile(coordinate.new Offset(position.q - 1, position.r)), 
            getTile(coordinate.new Offset(position.q - 1, position.r + 1)), 
            getTile(coordinate.new Offset(position.q, position.r + 1)) }
        : new HexTile[]{ 
            getTile(coordinate.new Offset(position.q + 1, position.r - 1)), 
            getTile(coordinate.new Offset(position.q, position.r - 1)),
            getTile(coordinate.new Offset(position.q + 1, position.r)), 
            getTile(coordinate.new Offset(position.q - 1, position.r)), 
            getTile(coordinate.new Offset(position.q, position.r + 1)), 
            getTile(coordinate.new Offset(position.q + 1, position.r + 1))});
    }

    public ElementalAttribut getMapElement() {
        return mapElement;
    }
}