package editor.area;

import gui.LoadingPopup;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import editor.EditorSystem;
import editor.map.MapEditorSystem;
import entitysystem.field.position.HexPositionComponent;
import hexsystem.area.AreaEventSystem;
import hexsystem.area.AreaGridSystem;
import hexsystem.area.AreaMouseInputSystem;
import hexsystem.area.AreaPropsComponent;
import hexsystem.area.MapDataAppState;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.base.HexTile;
import org.hexgridapi.base.MapData;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.utility.ElementalAttribut;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public final class AreaEditorSystem extends MapEditorSystem implements TileChangeListener, MouseInputListener {

    private MapData mapData;
    private AreaTileWidget tileWidgetMenu;
    private TileEventMenu tileEventMenu;
    private LoadingPopup popup = null;

    public AreaEditorSystem(LoadingPopup popup) {
        this.popup = popup;
    }

    @Override
    protected EntitySet initialiseSystem() {
        mapData = ((MultiverseMain) app).getStateManager().getState(MapDataAppState.class).getMapData();
        app.getStateManager().attach(new AreaGridSystem(mapData));
        app.getStateManager().attach(new AreaEventRenderDebugSystem());
        app.getStateManager().attach(new AreaEventSystem());
        if (popup != null) {
            reloadSystem(popup);
        } else {
            generateEmptyArea();
        }
        app.getStateManager().getState(AreaMouseInputSystem.class).registerTileInputListener(this);
        return entityData.getEntities(AreaPropsComponent.class, HexPositionComponent.class);
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
        mapData.Cleanup();
        generateEmptyArea();
    }

    public void reloadSystem(LoadingPopup popup) {
        if (popup != null && popup.getInput() != null) {
            if (!mapData.loadArea(popup.getInput())) {
                popup.popupBox("    " + popup.getInput() + " couldn't be loaded.");
            } else {
                popup.removeAndClear();
            }
        } else {
            if (popup != null) {
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

    public void leftMouseActionResult(MouseInputEvent event) {
        closeTileMenu();
    }

    public void rightMouseActionResult(MouseInputEvent event) {
        openWidgetMenu(event.getEventPosition());
        if(tileEventMenu != null && !event.getEventPosition().equals(tileEventMenu.getInspectedTilePos())){
            closeTileEventMenu();
        }
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

    private void closeTileMenu() {
        closeTileWidgetMenu();
        closeTileEventMenu();
    }

    private void closeTileEventMenu() {
        if (tileEventMenu != null && tileEventMenu.isVisible()) {
            tileEventMenu.hide();
        }
    }

    private void closeTileWidgetMenu() {
        if (tileWidgetMenu != null && tileWidgetMenu.isVisible()) {
            tileWidgetMenu.hide();
        }
    }

    void openEventMenu(HexCoordinate inspectedTilePos) {
        if (tileEventMenu == null) {
            tileEventMenu = new TileEventMenu(app.getStateManager().getState(AreaEventSystem.class), ((MultiverseMain) app).getScreen(), app.getStateManager().getState(EditorSystem.class).getGUI(), inspectedTilePos);;
        } else if (tileEventMenu.getInspectedTilePos() != inspectedTilePos) {
            tileEventMenu.setInpectedTile(inspectedTilePos);
        }
        if (!tileEventMenu.isVisible()) {
            tileEventMenu.show();
        }
    }

    void openAssetMenu(HexCoordinate inspectedTilePos) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cleanupSystem() {
        mapData.Cleanup();
        app.getStateManager().getState(AreaMouseInputSystem.class).removeTileInputListener(this);
        app.getStateManager().detach(app.getStateManager().getState(AreaGridSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(AreaEventRenderDebugSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(AreaEventSystem.class));
        if (tileWidgetMenu != null) {
            tileWidgetMenu.removeFromScreen();
        }
    }
}
