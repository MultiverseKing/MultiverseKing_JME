package gamemode.editor.map;

import hexsystem.events.HexMapInputListener;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import gamemode.editor.EditorMainGui;
import gamemode.editor.EditorMenu;
import hexsystem.HexMapMouseSystem;
import hexsystem.HexSystemAppState;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import kingofmultiverse.MultiverseMain;
import utility.ElementalAttribut;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 * Part of the Entity system to be able to implement assets/cards Object.
 *
 * @todo Room Editor mode (build up multiple Room to create an Area)
 * @todo Area Editor mode (build up area with object and stuff)
 * @todo Area should work with any element(fire area can be converted to ice
 * area)
 *
 * @author Eike Foede, Roah
 */
public class MapEditorSystem extends EntitySystemAppState implements TileChangeListener, HexMapInputListener {

    private MapData mapData;
    private EditorMenu currentGui = null;
    private boolean initializeArea = false;
    private MapEditorMode currentMode = null;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = ((MultiverseMain) app).getStateManager().getState(HexSystemAppState.class).getMapData();
        switchGui(MapEditorMode.NONE);

        return entityData.getEntities(PropsComponent.class);
    }

    // <editor-fold defaultstate="collapsed" desc="HexMap Mouse Action Result">
    public void leftMouseActionResult(HexMapInputEvent event) {
        switch (currentMode) {
            case ROOM:
                ((RoomEditorMenu) currentGui).setSelectedTile(event.getEventPosition(), 'L');
//            ((RoomEditorMenu) currentGui).closeWidgetMenu();
                break;
            case AREA:
                break;
            case WORLD:
                break;
            case NONE:
                break;
            default:
                System.err.println(currentMode + " isn't implemented.");
                break;
        }
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        switch (currentMode) {
            case ROOM:
                ((RoomEditorMenu) currentGui).setSelectedTile(event.getEventPosition(), 'R');
                break;
            case AREA:
                break;
            case WORLD:
                break;
            case NONE:
                break;
            default:
                System.err.println(currentMode + " isn't implemented.");
                break;
        }
    }
    // </editor-fold>

    public void tileChange(TileChangeEvent event) {
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
    // </editor-fold>

    void setMapElement(ElementalAttribut eAttribut) {
        mapData.setMapElement(eAttribut);
    }

    ElementalAttribut getMapElement() {
        return mapData.getMapElement();
    }

    @Override
    protected void updateSystem(float tpf) {
        currentGui.update(tpf);
    }

    @Override
    protected void addEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Change the current GUI_Mode to another. 0 == Map Editor root; 1 == World
     * Editor; 2 == Area Editor;
     *
     * @param currentMode
     */
    void switchGui(MapEditorMode newMode) {
        if (currentMode == null || newMode != currentMode) {
            if (currentGui != null
                    && ((MultiverseMain) app).getScreen().getElementById(currentGui.getUID()) != null) {
                ((MultiverseMain) app).getScreen().removeElement(currentGui);
            }
            switch (newMode) {
                case WORLD:
                    /**
                     * Init the World Editor
                     */
                    currentGui = new WorldEditorMenu(((MultiverseMain) app).getScreen(), this);
                    ((MultiverseMain) app).getScreen().addElement(currentGui);
                    break;
                case ROOM:
                    /**
                     * Init the Room Editor
                     */
                    currentGui = new RoomEditorMenu(((MultiverseMain) app).getScreen(), this);
                    ((MultiverseMain) app).getScreen().addElement(currentGui);
                    if (!initializeArea) {
                        initializeArea();
                    }
                    break;
                default:
                    /**
                     * Map Editor root
                     */
                    currentGui = new MapEditorMenu(((MultiverseMain) app).getScreen(), this);
                    ((MultiverseMain) app).getScreen().addElement(currentGui);
                    break;
            }
            currentMode = newMode;
        }
    }

    private void initializeArea() {
        ((MultiverseMain) app).getStateManager().getState(HexMapMouseSystem.class).registerTileInputListener(this);
        mapData.registerTileChangeListener(this);
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(Vector2Int.ZERO, null);
        }
        initializeArea = true;
    }

    private void clearArea() {
        mapData.removeTileChangeListener(this);
        ((MultiverseMain) app).getStateManager().getState(HexMapMouseSystem.class).removeTileInputListener(this);
        mapData.clearCurrent();
        initializeArea = false;
    }

    /**
     * Save Area or world following the current mode.
     *
     * @return
     */
    boolean saveMapForCurrent(String name) {
        if (currentGui instanceof RoomEditorMenu) {
            //Save the generated area.
            mapData.saveMap(name);
            return true;
        } else if (currentGui instanceof WorldEditorMenu) {
            //Save the generated World.
            return true;
        } else {
            throw new UnsupportedOperationException("Editor Mode not supported : " + currentGui.getClass().getClasses().toString());
        }
    }

    /**
     * Load Area or World following the current mode and the provided name.
     *
     * @return
     */
    boolean loadMapForCurrent(String name) {
        if (currentGui instanceof RoomEditorMenu) {
            //Load a generated area.
            mapData.loadMap(name);
            return true;
        } else if (currentGui instanceof WorldEditorMenu) {
            //Load a generated World.
            return true;
        } else {
            throw new UnsupportedOperationException("Editor Mode not supported : " + currentGui.getClass().getClasses().toString());
        }
    }

    @Override
    protected void cleanupSystem() {
        if (initializeArea) {
            clearArea();
        }
        ((MultiverseMain) app).getScreen().removeElement(currentGui);
        app.getStateManager().attach(new EditorMainGui());
    }

    enum MapEditorMode {

        ROOM,
        AREA,
        WORLD,
        NONE;
    }
}
