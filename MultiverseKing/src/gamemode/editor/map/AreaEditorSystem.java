package gamemode.editor.map;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import hexsystem.HexSystemAppState;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.AreaMouseSystem;
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
public final class AreaEditorSystem extends MapEditorSystem implements TileChangeListener, HexMapInputListener {

    private LoadingPopup dialCaller;
    private MapData mapData;
    private String editedAreaName = null;

    public AreaEditorSystem() {
    }

    public AreaEditorSystem(LoadingPopup dialCaller, String editedMapName) {
        this.editedAreaName = editedMapName;
        this.dialCaller = dialCaller;
    }

    @Override
    protected EntitySet initialiseSystem() {
        mapData = ((MultiverseMain) app).getStateManager().getState(HexSystemAppState.class).getMapData();
        if (editedAreaName == null) {
            initializeArea();
        } else if (!load(editedAreaName)) {
            /**
             * @todo : When the area isn't found.
             */
        }

        return entityData.getEntities(AreaPropsComponent.class);
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

    public void initializeArea() {
        app.getStateManager().attach(new AreaMouseSystem(this));
        mapData.registerTileChangeListener(this);
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(Vector2Int.ZERO, null);
        }
    }

    public void resetArea() {
        clearArea();
        initializeArea();
    }

    private void clearArea() {
        mapData.removeTileChangeListener(this);
        mapData.clearCurrent();
    }

    private boolean load() {
        return mapData.loadArea(editedAreaName);
    }

    public boolean load(String name) {
        editedAreaName = name;
        if (!load()) {
//            if(dialCaller != null){
            dialCaller.popupBox("    " + name + " not found.");
//            }
            editedAreaName = null;
            return false;
        }
        return true;
    }

    private boolean save() {
        return mapData.saveMap(editedAreaName);
    }

    public boolean save(String name) {
        editedAreaName = name;
        if (isEmpty() || !save()) {
            editedAreaName = null;
            return false;
        }
        return true;
    }

    /**
     * @return True if there is something who can be saved.
     */
    public boolean isEmpty() {
        return mapData.containTilesData();
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
