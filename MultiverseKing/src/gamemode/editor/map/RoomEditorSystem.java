package gamemode.editor.map;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import hexsystem.HexSystemAppState;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.RoomMouseSystem;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import kingofmultiverse.MultiverseMain;
import utility.ElementalAttribut;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public final class RoomEditorSystem extends EntitySystemAppState implements TileChangeListener, HexMapInputListener{
    private MapData mapData;
    private String editedMapName = null;

    public RoomEditorSystem() {
    }

    public RoomEditorSystem(String editedMapName) {
        this.editedMapName = editedMapName;
    }
    
    @Override
    protected EntitySet initialiseSystem() {
        mapData = ((MultiverseMain) app).getStateManager().getState(HexSystemAppState.class).getMapData();
        if(editedMapName == null){
            initializeRoom();
        } else {
            mapData.loadMap(editedMapName);
        }

        return entityData.getEntities(RoomPropsComponent.class);
    }

    

    // <editor-fold defaultstate="collapsed" desc="Tile propertie Getters && Setters">
    void setTileProperties(HexCoordinate coord, int height) {
        mapData.setTileHeight(coord, (byte) (mapData.getTile(coord).getHeight() + height));
    }

    void setTileProperties(HexCoordinate coord, ElementalAttribut eAttribut) {
        mapData.setTileEAttribut(coord, eAttribut);
    }

    void setTileProperties(HexCoordinate coord, HexTile tile) {
        mapData.setTile(coord, tile);
    }

    ElementalAttribut getTileEAttribut(HexCoordinate coord) {
        return mapData.getTile(coord).getElement();
    }

    int getTileHeight(HexCoordinate coord) {
        return mapData.getTile(coord).getHeight();
    }

    void setMapElement(ElementalAttribut eAttribut) {
        mapData.setMapElement(eAttribut);
    }

    ElementalAttribut getMapElement() {
        return mapData.getMapElement();
    }
    // </editor-fold>
    
    public void initializeRoom() {
        app.getStateManager().attach(new RoomMouseSystem(this));
        mapData.registerTileChangeListener(this);
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(Vector2Int.ZERO, null);
        }
    }

    public void reloadRoom() {
        System.err.println("Not Supported yet.");
    }

    private void clearRoom() {
        mapData.removeTileChangeListener(this);
        mapData.clearCurrent();
    }

    /**
     * Save Area or world following the current mode.
     *
     * @return
     */
    boolean saveCurrent(String name) {
        mapData.saveMap(name);
        return true;
    }

    /**
     * Load Area or World following the current mode and the provided name.
     *
     * @return
     */
    boolean loadCurrent(String name) {
        mapData.loadMap(name);
        return true;
    }
    
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        System.err.println("Not Supported yet.");
    }

    @Override
    protected void updateEntity(Entity e) {
        System.err.println("Not Supported yet.");
    }

    @Override
    protected void removeEntity(Entity e) {
        System.err.println("Not Supported yet.");
    }

    @Override
    protected void cleanupSystem() {
        System.err.println("Not Supported yet.");
    }

    public void tileChange(TileChangeEvent event) {
        System.err.println("Not Supported yet.");
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        System.err.println("Not Supported yet.");
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        System.err.println("Not Supported yet.");
    }
}
