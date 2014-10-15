package gamemode.editor.map;

import gamemode.gui.LoadingPopup;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import hexsystem.HexSystemAppState;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.AreaMouseInputSystem;
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

    private MapData mapData;
    private AreaTileWidget tileWidgetMenu;
    private LoadingPopup popup = null;
    
    public AreaEditorSystem(LoadingPopup popup) {
        this.popup = popup;
    }

    public AreaEditorSystem() {
    }

    @Override
    protected EntitySet initialiseSystem() {
        mapData = ((MultiverseMain) app).getStateManager().getState(HexSystemAppState.class).getMapData();
        if(popup != null) {
            reloadSystem(popup);
        } else {
            generateEmptyArea();
        }
        app.getStateManager().getState(AreaMouseInputSystem.class).registerTileInputListener(this);
        return entityData.getEntities(AreaPropsComponent.class);
    }

    // <editor-fold defaultstate="collapsed" desc="Tile propertie Getters && Setters">
    /**
     *
     * @param coord
     * @param height how many to add
     */
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

    public void generateEmptyArea() {
        mapData.registerTileChangeListener(this);
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(new Vector2Int(), null);
        }
    }

    public void reloadSystem() {
        mapData.Clear();
        generateEmptyArea();
    }

    public void reloadSystem(LoadingPopup popup) {
        if(popup != null && popup.getInput() != null){
            if (!mapData.loadArea(popup.getInput())) {
                popup.popupBox("    " + popup.getInput() + " couldn't be loaded.");
            } else {
                popup.removeFromScreen();
            }
        } else {
            if(popup != null){
                popup.popupBox("    " + "There is nothing to load.");
            }
            reloadSystem();
        }
    }

    public void save(LoadingPopup popup) {
        if (isEmpty() || !mapData.saveArea(popup.getInput())) {
            popup.popupBox("    " + popup.getInput() + " couldn't be saved.");
        }
    }

    /**
     * @return True if there is something who can be saved.
     */
    public boolean isEmpty() {
        return mapData.containTilesData();
    }

    @Override
    protected void updateSystem(float tpf) {
        if (tileWidgetMenu != null) {
            tileWidgetMenu.update(tpf);
        }
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

    public void tileChange(TileChangeEvent event) {
        if (tileWidgetMenu.isVisible()) {
//            openWidgetMenu(event.getTilePos());
        }
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        openWidgetMenu(event.getEventPosition());
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        closeWidgetMenu();
    }

    /**
     * Window related to the selected hex.
     *
     * @param coord of the selected hex
     */
    private void openWidgetMenu(HexCoordinate tilePos) {
        if (tileWidgetMenu == null) {
            tileWidgetMenu = new AreaTileWidget(((MultiverseMain) app).getScreen(), app.getCamera(), this, tilePos);
        }
        tileWidgetMenu.show(tilePos, mapData.getTile(tilePos).getHeight());
    }

    private void closeWidgetMenu() {
        if (tileWidgetMenu != null && tileWidgetMenu.isVisible()) {
            tileWidgetMenu.hide();
        }
    }

    @Override
    protected void cleanupSystem() {
        mapData.Clear();
        app.getStateManager().getState(AreaMouseInputSystem.class).removeTileInputListener(this);
        if (tileWidgetMenu != null) {
            tileWidgetMenu.removeFromScreen();
        }
    }
}
