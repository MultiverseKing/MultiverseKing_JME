package gamemode.editor.map;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import hexsystem.events.HexMapInputListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import gamemode.editor.EditorMainAppState;
import hexsystem.HexMapMouseSystem;
import hexsystem.HexSystemAppState;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import utility.ElementalAttribut;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 * TODO: Map Edition mode (build up multiple area to create a map) TODO: Area
 * Edition mode (build up area with object and stuff) TODO: Area should work
 * with any element(fire area can be converted to ice area)
 *
 * @author Eike Foede, Roah
 */
public class MapEditorSystem extends AbstractAppState implements TileChangeListener, HexMapInputListener {

    private final MultiverseMain main;
    private Window mainMenu = null;
    private MapData mapData;
    private HexCoordinate currentTilePosition;
    private MapEditorGUI mapEditorGui;

    public MapEditorSystem(MultiverseMain main) {
        this.main = main;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mapEditorGui = new MapEditorGUI(main, this);
        main.getScreen().addElement(mapEditorGui);
        initMap();
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        openHexPropertiesWin(event.getEventPosition());
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        openHexPropertiesWin(event.getEventPosition());
    }

    public void tileChange(TileChangeEvent event) {
    }

    private void initMap() {
        mapData = main.getStateManager().getState(HexSystemAppState.class).getMapData();
        main.getStateManager().getState(HexMapMouseSystem.class).registerTileInputListener(this);
        mapData.registerTileChangeListener(this);
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(Vector2Int.ZERO, null);
        }
    }

    private void initEditorGUI() {
        /**
         * Open the Save and load context menu.
         */
        Button saveLoad = new ButtonAdapter(main.getScreen(), "save/load", new Vector2f(15, 40 * 2)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!mainMenu.getElementsAsMap().containsKey("saveLoadWin")) {
                    removeWin();
                    saveLoadMenu();
                } else {
                    mainMenu.removeChild(mainMenu.getElementsAsMap().get("saveLoadWin"));
                }
            }
        };
        saveLoad.setText("Save/Load");
        mainMenu.addChild(saveLoad);

        /**
         * Load a predefined void map from a File(same as the starting one).
         */
        Button reset = new ButtonAdapter(main.getScreen(), "reset", new Vector2f(15, 40 * 4)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!mapData.loadMap("Reset")) {
                    System.err.println("Cannot Load Reset map.");
                }
            }
        };
        reset.setText("Reset");
        mainMenu.addChild(reset);
    }

    /**
     * Method used to open a window related to the selected hex.
     *
     * @param tile selected one.
     */
    public void openHexPropertiesWin(HexCoordinate tile) {
        currentTilePosition = tile;
        mapEditorGui.setSelectedTile(mapData.getTile(tile), tile);
    }

    public void updateTileProperties(int height) {
        mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() + height));
    }
    
    public void updateTileProperties(ElementalAttribut eAttribut) {
        updateTileProperties(new HexTile(eAttribut, (byte) mapData.getTile(currentTilePosition).getHeight()));
    }
    public void updateTileProperties(HexTile tile) {
        mapData.setTile(currentTilePosition, tile);
    }

    private void saveLoadMenu() {
        final Window saveLoad = new Window(main.getScreen(), "saveLoadWin", new Vector2f(mainMenu.getWidth() + 10, 0), new Vector2f(225, 40 * 2 + 15));
        saveLoad.setIsMovable(false);
        saveLoad.setIsResizable(false);
        saveLoad.removeAllChildren();
        mainMenu.addChild(saveLoad);

        final TextField field = new TextField(main.getScreen(), new Vector2f(10, 15), new Vector2f(205, 30));
        saveLoad.addChild(field);

        /**
         * Button used to save the map in a folder/file of his name.
         */
        Button save = new ButtonAdapter(main.getScreen(), "save", new Vector2f(10, 40 + 10), new Vector2f(80, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (field != null && !field.getText().isEmpty()) {
                    Window error = (Window) mainMenu.getChildElementById("saveLoadWin").getChildElementById("saveLoadError");
                    if (mapData.saveMap(field.getText())) {
                        error.setText("     File Saved.");
                    } else {
                        error.setText("     Error file not saved.");
                    }
                    error.show();
                }
            }
        };
        save.setText("Save");
        saveLoad.addChild(save);

        /**
         * Button to load the named map.
         *
         * @todo add a context menu where you will be able to set the name of
         * the map to load.
         */
        Button load = new ButtonAdapter(main.getScreen(), "load", new Vector2f(95, 40 + 10), new Vector2f(80, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (field != null && !field.getText().isEmpty()) {
                    if (!mapData.loadMap(field.getText())) {
                        Window error = (Window) mainMenu.getChildElementById("saveLoadWin").getChildElementById("saveLoadError");
                        error.setText("     File not found.");
                        error.show();
                    }
                }
            }
        };
        load.setText("Load");
        saveLoad.addChild(load);

        /**
         * Button used to show up all map in stored in the map folder.
         */
        Button fileList = new ButtonAdapter(main.getScreen(), "fileList", new Vector2f(180, 40 + 10), new Vector2f(35, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (saveLoad.getChildElementById("filePreview") == null) {
                    File folder = new File(System.getProperty("user.dir") + "/assets/Data/MapData/");
                    if (folder.exists()) {
                        fileList(folder);
                    } else {
                        Logger.getLogger(MapEditorSystem.class.getName()).log(Level.SEVERE, "Cannot locate the MapData Folder.", FileNotFoundException.class);
                    }
                } else {
                    saveLoad.removeChild(mainMenu.getChildElementById("filePreview"));
                }
            }

            private void fileList(File folder) {
                /**
                 * New Window and Button Group to hold the list.
                 */
                Window filePreview = new Window(main.getScreen(), "filePreview",
                        new Vector2f(getElementParent().getWidth() + 5, 0), new Vector2f(120, mainMenu.getHeight()));
                getElementParent().addChild(filePreview);
                filePreview.setIsMovable(false);
                filePreview.setIsResizable(false);
                filePreview.removeAllChildren();

                RadioButtonGroup filesButtonGroup = new RadioButtonGroup(main.getScreen(), "filesButtonGroup") {
                    @Override
                    public void onSelect(int index, Button value) {
                        mapData.loadMap(value.getUID());
                    }
                };
                File[] flist = folder.listFiles();
                byte b = 0;
                for (int i = 0; i < flist.length; i++) {
                    if (flist[i].getName().equalsIgnoreCase("Reset") || flist[i].getName().equalsIgnoreCase("Temp")) {
                        b++;
                    } else {
                        ButtonAdapter button = new ButtonAdapter(main.getScreen(), flist[i].getName(),
                                new Vector2f(10, 10 + 40 * (i - b)));
                        button.setText(flist[i].getName());
                        filesButtonGroup.addButton(button);
                    }
                }
                filesButtonGroup.setDisplayElement(filePreview);
                filePreview.setIsVisible(true);
            }
        };
        saveLoad.addChild(fileList);


        Window error = new Window(main.getScreen(), "saveLoadError", new Vector2f(0, saveLoad.getHeight()),
                new Vector2f(225, 30));
        saveLoad.addChild(error);
        error.hide();
    }

    private void removeWin() {
        Map<String, Element> map = mainMenu.getElementsAsMap();
        String[] winList = new String[]{"saveLoadWin", "EWindows"};
        for (String s : winList) {
            if (map.containsKey(s)) {
                mainMenu.removeChild(mainMenu.getChildElementById(s));
            }
        }
    }

    @Override
    public void cleanup() {
        main.getScreen().removeElement(mapEditorGui);
        main.getStateManager().attach(new EditorMainAppState());
        mapData.removeTileChangeListener(this);
        main.getStateManager().getState(HexMapMouseSystem.class).removeTileInputListener(this);
    }

    public void setMapElement(ElementalAttribut eAttribut) {
        mapData.setMapElement(eAttribut);
    }

    public ElementalAttribut getCurrentMapElement() {
        return mapData.getMapElement();
    }
}
