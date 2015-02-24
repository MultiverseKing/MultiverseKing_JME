package core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
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
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public final class EditorMainSystem extends AbstractHexGridAppState {

    private Screen screen;
    private SimpleApplication app;
    private Element rootElement;
    private MouseControlAppState mouseControl;
    private GhostControl ghostControl;
    private TileSelectionControl tileSelectionControl;

    /* todo */
    private TileWidgetMenu tileWidgetMenu;
    private boolean initMenu = true;
    private FileManagerPopup popup = null;
//    private EditorTileProperties gui;
    private EditorMainGUI editorMainGUI;
    
    /**
     * Inner Class.
     */
    private final TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void leftMouseActionResult(MouseInputEvent event) {
//            HexTile tile = mapData.getTile(event.getEventPosition());
//            editorMainGUI.getTileWindow().updatePosition(event.getEventPosition(), tile != null ? false : true, isSelectionGroup);
//            if (isSelectionGroup) {
//                tileSelectionControl.addTile(event.getEventPosition(), tile != null ? tile.getHeight() : 0);
//                editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
//            }
    //        gridSystem.setEnabledGhost(tile != null ? false : true);

        }

        @Override
        public void rightMouseActionResult(MouseInputEvent event) {
    //        if (tileWidgetMenu == null || !tileWidgetMenu.isVisible()) {
    //            openWidgetMenu(event.getEventPosition());
    //            mouseControl.lockCursor();
    //        }
        }
    };
    
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

        
        mouseControl = app.getStateManager().getState(MouseControlAppState.class);
        if (mouseControl == null) {
            mouseControl = new MouseControlAppState();
            app.getStateManager().attach(mouseControl);
        }
//        mouseControl.registerTileInputListener(tileInputListener);

        if (screen == null) {
            this.screen = new Screen(app);
            this.app.getGuiNode().addControl(screen);
        }
        editorMainGUI = new EditorMainGUI(screen, rootElement, this);
//        gui = new EditorTileProperties(screen, editorRoot.getWindow(), this);
        initialiseGhostGrid((SimpleApplication) app);
        
        /**
         * @todo
         */
        screen.getApplication().getInputManager().addMapping("help", new KeyTrigger(KeyInput.KEY_F1));
    }

    protected void initialiseGhostGrid(SimpleApplication app) {
        Node node = new Node("GhostNode");
        ghostControl = new GhostControl(app, meshParam, new Vector2Int(), this);
        node.addControl(ghostControl);
        gridNode.attachChild(node);
        node 
        tileSelectionControl = new TileSelectionControl(app, mouseControl, mapData);
        node.addControl(tileSelectionControl);
        gridNode.attachChild(node);
    }

    // <editor-fold defaultstate="collapsed" desc="Properties Getters && Setters">
    public void enableGhostUpdate(boolean enable) {
        ghostControl.setEnabled(true);
    }

    public void removeTile() {
        mapData.setTile(tileSelectionControl.getTileList().toArray(new HexCoordinate[tileSelectionControl.getTileList().size()]), null);
    }

    public void setNewTile() {
        mapData.setTile(tileSelectionControl.getTileList().toArray(new HexCoordinate[tileSelectionControl.getTileList().size()]), new HexTile[]{new HexTile()});
    }
    
    public void setTilePropertiesTexTure(String textureKey) {
        HexCoordinate[] tileList = tileSelectionControl.getTileList().toArray(new HexCoordinate[tileSelectionControl.getTileList().size()]);
        HexTile[] t = mapData.getTile(tileList);
        for(int i = 0; i < t.length; i++){
            t[i] = t[i].cloneChangedTextureKey(mapData.getTextureKey(textureKey));
        }
        mapData.setTile(tileList, t);
    }
    
    public void setTilePropertiesDown() {
        HexCoordinate[] tileList = tileSelectionControl.getTileList().toArray(new HexCoordinate[tileSelectionControl.getTileList().size()]);
        HexTile[] t = mapData.getTile(tileList);
        for(int i = 0; i < t.length; i++){
            t[i] = t[i].cloneChangedHeight((t[i].getHeight()+1));
        }
        mapData.setTile(tileList, t);
    }
        
    public void setTilePropertiesUp() {
        HexCoordinate[] tileList = tileSelectionControl.getTileList().toArray(new HexCoordinate[tileSelectionControl.getTileList().size()]);
        HexTile[] t = mapData.getTile(tileList);
        for(int i = 0; i < t.length; i++){
            t[i] = t[i].cloneChangedHeight((t[i].getHeight()-1));
        }
        mapData.setTile(tileList, t);
    }
        
//    public void setTileProperties(HexCoordinate coord, int height) {
//        if(!tileSelectionControl.getList().isEmpty()){
//            mapData.setTilesHeight(height, tileSelectionControl.getList().toArray(new HexCoordinate[tileSelectionControl.getList().size()]));
//        } else {
//            mapData.setTilesHeight(height, coord);
//        }
//    }

    public int getTileHeight() {
        HexTile tile = mapData.getTile(tileSelectionControl.getTileList().get(0));
        if (tile != null) {
            return tile.getHeight();
        } else {
            return HexSetting.GROUND_HEIGHT;
        }
    }

    public int getTileTextureValue() {
        HexTile tile = mapData.getTile(tileSelectionControl.getTileList().get(0));
        if (tile != null) {
            return tile.getTextureKey();
        } else {
            return 0;
        }
    }

    public String getTileTextureKey() {
        HexTile tile = mapData.getTile(tileSelectionControl.getTileList().get(0));
        if (tile != null) {
            return mapData.getTextureValue(tile.getTextureKey());
        } else {
            return mapData.getTextureValue(0);
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

    String getTextureValueFromKey(int textureKey) {
        return mapData.getTextureValue(textureKey);
    }

    public void setEnabledGhost(boolean enable) {
        ghostControl.setEnabled(enable);
    }
    // </editor-fold>

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

    /**
     * Window related to the selected hex.
     * @param coord of the selected hex
     * @deprecated 
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

    /**
     * @deprecated 
     */
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

    /**
     * @deprecated 
     */
    public void clearSelectionGroup() {
//        tileSelectionControl.clearSelectionGroup();
        editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
    }

    public void reloadSystem() {
        mapData.Cleanup();
    }

    @Override
    public void cleanupSystem() {
//        app.getStateManager().getState(MouseControlAppState.class).removeTileInputListener(this);
    }
}
