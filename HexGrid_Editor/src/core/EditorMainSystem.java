package core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import core.control.GhostControl;
import core.control.TileSelectionControl;
import core.gui.EditorMainGUI;
import gui.FileManagerPopup;
import gui.control.TileWidgetMenu;
import java.util.List;
import org.hexgridapi.core.appstate.MouseControlAppState;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.AbstractHexGridAppState;
import org.hexgridapi.core.control.ChunkControl;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputListener;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public final class EditorMainSystem extends AbstractHexGridAppState implements MouseInputListener {

    private Screen screen;
    private SimpleApplication app;
    private Element rootElement;
    private MouseControlAppState mouseControl;
    private GhostControl ghostControl;
    private TileSelectionControl tileSelectionControl;
    private boolean selectionGroup = false;

    /* todo */
    private TileWidgetMenu tileWidgetMenu;
    private boolean initMenu = true;
    private FileManagerPopup popup = null;
//    private EditorTileProperties gui;
    private EditorMainGUI editorMainGUI;

    public EditorMainSystem(MapData mapData) {
        this(mapData, null, null);
    }

    public EditorMainSystem(MapData mapData, Screen screen, Element rootMenu) {
        super(mapData, true);
        this.screen = screen;
        this.rootElement = rootMenu;
    }

    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        this.app = (SimpleApplication) app;

        // <editor-fold defaultstate="collapsed" desc="initialise needed AppState">
        mouseControl = app.getStateManager().getState(MouseControlAppState.class);
        if (mouseControl == null) {
            mouseControl = new MouseControlAppState();
            app.getStateManager().attach(mouseControl);
        }
        mouseControl.registerTileInputListener(this);
        // </editor-fold>

        if (screen == null) {
            this.screen = new Screen(app);
            this.app.getGuiNode().addControl(screen);
        }
        editorMainGUI = new EditorMainGUI(screen, rootElement, this);
//        gui = new EditorTileProperties(screen, editorRoot.getWindow(), this);
        initialiseGhostGrid((SimpleApplication) app);
        /**
         * Activate the input to interact with the grid.
         */
        screen.getApplication().getInputManager().addMapping("help", new KeyTrigger(KeyInput.KEY_F1));
        app.getInputManager().addMapping("selectionGrp", new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_RSHIFT));
        app.getInputManager().addListener(keyListener, new String[]{"help", "selectionGrp"});
        app.getInputManager().addListener(mouseListener, new String[]{"Cancel", "Confirm"});
    }

    protected void initialiseGhostGrid(SimpleApplication app) {
        Node node = new Node("GhostNode");
        ghostControl = new GhostControl(app, meshParam, new Vector2Int(), this);
        node.addControl(ghostControl);
        gridNode.attachChild(node);
        node = new Node("tileSelectionNode");
        tileSelectionControl = new TileSelectionControl(assetManager, mapData);
        node.addControl(tileSelectionControl);
        gridNode.attachChild(node);
    }

    // <editor-fold defaultstate="collapsed" desc="Properties Getters && Setters">
    public void enableGhostUpdate(boolean enable) {
        ghostControl.setEnabled(true);
    }

    public void setSelectionGroup(boolean enable) {
        selectionGroup = enable;
    }

    public void removeTile(HexCoordinate coord) {
        if(!tileSelectionControl.getList().isEmpty()){
            mapData.setTile(null, tileSelectionControl.getList().toArray(new HexCoordinate[tileSelectionControl.getList().size()]));
        } else {
            mapData.setTile(null, coord);
        }
    }

    public void setTileProperties(HexCoordinate coord, String textureKey) {
        if(!tileSelectionControl.getList().isEmpty()){
            mapData.setTileTextureKey(textureKey, tileSelectionControl.getList().toArray(new HexCoordinate[tileSelectionControl.getList().size()]));
        } else {
            mapData.setTileTextureKey(textureKey, coord);
        }
    }

    public void setTileProperties(HexCoordinate coord, byte height) {
        if(!tileSelectionControl.getList().isEmpty()){
            mapData.setTileHeight(height, tileSelectionControl.getList().toArray(new HexCoordinate[tileSelectionControl.getList().size()]));
        } else {
            mapData.setTileHeight(height, coord);
        }
    }

    public void setTileProperties(HexCoordinate coord, HexTile tile) {
        if(!tileSelectionControl.getList().isEmpty()){
            mapData.setTile(tile, tileSelectionControl.getList().toArray(new HexCoordinate[tileSelectionControl.getList().size()]));
        } else {
            mapData.setTile(tile, coord);
        }
    }
    
    public int getTileHeight(HexCoordinate coord) {
        HexTile tile = mapData.getTile(coord);
        if (tile != null) {
            return tile.getHeight();
        } else {
            return HexSetting.GROUND_HEIGHT;
        }
    }

    public int getTileTextureValue(HexCoordinate inspectedPosition) {
        HexTile tile = mapData.getTile(inspectedPosition);
        if (tile != null) {
            return tile.getTextureKey();
        } else {
            return 0;
        }
    }

    public String getTileTextureKey(HexCoordinate inspectedPosition) {
        HexTile tile = mapData.getTile(inspectedPosition);
        if (tile != null) {
            return mapData.getTextureValue(tile.getTextureKey());
        } else {
            return mapData.getTextureValue((byte) 0);
        }
    }

    public List<String> getTextureKeys() {
        return mapData.getTextureKeys();
    }

    /**
     * @return True if there is something who can be saved.
     */
    public boolean isEmpty() {
        return !mapData.containTilesData();
    }

    String getTextureValueFromKey(byte textureKey) {
        return mapData.getTextureValue(textureKey);
    }

    public void setEnabledGhost(boolean enable) {
        ghostControl.setEnabled(enable);
    }
    // </editor-fold>
    private final ActionListener keyListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("selectionGrp") && isPressed) {
                selectionGroup = true;
            } else if (name.equals("selectionGrp") && !isPressed) {
                selectionGroup = false;
            } else if (name.equals("help") && !isPressed) {
//                openHelpWindow();
            }
        }
    };
    private final ActionListener mouseListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Confirm") && !isPressed) {
            } else if (name.equals("Cancel") && !isPressed) {
                clearSelectionGroup();
            }
        }
    };

    public void save(FileManagerPopup popup) {
        if (isEmpty() || !mapData.saveArea(popup.getInput())) {
            popup.popupBox("    " + popup.getInput() + " couldn't be saved.");
        }
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    protected void insertedChunk(ChunkControl control) {
        ghostControl.updateCulling();
    }

    @Override
    protected void updatedChunk(ChunkControl control) {
    }

    @Override
    protected void removedChunk(Vector2Int pos) {
        ghostControl.updateCulling();
    }

    @Override
    public void leftMouseActionResult(MouseInputEvent event) {
        HexTile tile = mapData.getTile(event.getEventPosition());
        editorMainGUI.getTileWindow().updatePosition(event.getEventPosition(), tile != null ? false : true,
                !tileSelectionControl.getList().isEmpty());
        if (selectionGroup) {
            tileSelectionControl.addTile(event.getEventPosition(), tile != null ? tile.getHeight() : 0);
            editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
        }
//        gridSystem.setEnabledGhost(tile != null ? false : true);

    }

    @Override
    public void rightMouseActionResult(MouseInputEvent event) {
//        if (tileWidgetMenu == null || !tileWidgetMenu.isVisible()) {
//            openWidgetMenu(event.getEventPosition());
//            mouseControl.lockCursor();
//        }
    }

    /**
     * Window related to the selected hex.
     *
     * @param coord of the selected hex
     */
    private void openWidgetMenu(HexCoordinate tilePos) {
        if (tileWidgetMenu == null) {
            tileWidgetMenu = new TileWidgetMenu(screen, app.getCamera(), this, tilePos);
        }
        HexTile tile = mapData.getTile(tilePos);
        if (tile != null) {
            tileWidgetMenu.show(tilePos, tile.getHeight());
        } else {
            tileWidgetMenu.show(tilePos);
        }
    }

    private void closeTileMenu() {
        if (tileWidgetMenu != null && tileWidgetMenu.isVisible()) {
            tileWidgetMenu.hide();
        }
        mouseControl.unlockCursor();
        initMenu = true;
    }

    public void loadFromFile(FileManagerPopup popup) {
        if (popup != null && popup.getInput() != null) {
            if (!mapData.loadArea(popup.getInput())) {
                popup.popupBox("    " + popup.getInput() + " couldn't be loaded.");
            } else {
                popup.removeFromScreen();
            }
        } else {
            if (popup != null) {
                popup.popupBox("    " + "There is nothing to load.");
            }
            reloadSystem();
        }
    }

    public void clearSelectionGroup() {
        tileSelectionControl.clear();
        editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
    }

    public void reloadSystem() {
        mapData.Cleanup();
    }

    @Override
    public void cleanupSystem() {
        app.getStateManager().getState(MouseControlAppState.class).removeTileInputListener(this);
    }
}
