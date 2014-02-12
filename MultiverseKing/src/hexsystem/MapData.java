package hexsystem;

import hexsystem.events.TileChangeListener;
import hexsystem.events.TileChangeEvent;
import com.jme3.app.state.AbstractAppState;
import java.util.ArrayList;
import kingofmultiverse.MultiverseMain;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * This class holds the data of the map. It's implementation can be easily
 * replaced using other datastructures, so that you can handle dynamic map sizes
 *
 * @author Eike Foede, Roah
 */
public class MapData extends AbstractAppState {
    private ArrayList<ArrayList<HexTile>> tiles = new ArrayList<ArrayList<HexTile>>();
    private HexSettings hexSettings;
    private ElementalAttribut mapElement;
    private ArrayList<TileChangeListener> listeners = new ArrayList<TileChangeListener>();

    public HexSettings getHexSettings() {
        return hexSettings;
    }
    public ElementalAttribut getMapElement() {
        return mapElement;
    }
    
    
    public MapData(HexSettings hexSettings) {
        this.hexSettings = hexSettings;
    }

    public void setCurrentTileChunk(){
        
    }
    
    //TODO: Check if position is out of bounds
    public void setTile(int x, int y, HexTile t) {
        TileChangeEvent tce = new TileChangeEvent(x, y, hexTiles[x][y], t);
        hexTiles[x][y] = t;
        for (TileChangeListener l : listeners) {
            l.tileChange(tce);
        }
    }

    public boolean exist(Vector2Int pos){
        boolean result = false;
        try{
            if(hexTiles[pos.x][pos.y] != null){
                result = true;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Hex index out of bounds !");
        }
        return result;
    }
    
    public HexTile getTile(int x, int y) {
        return hexTiles[x][y];
    }    
    
    public HexTile getTile(Vector2Int pos) {
        return hexTiles[pos.x][pos.y];
    }

    public void registerTileChangeListener(TileChangeListener l) {
        listeners.add(l);
    }
    public void setHeight(int x, int y, int height){
        setTile(x,y,hexTiles[x][y].cloneChangedHeight(height));
    }

    public void setMapElement(ElementalAttribut eAttribut) {
        this.mapElement = eAttribut;
    }
}