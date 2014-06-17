package gamemode.editor;

import hexsystem.events.HexMapInputListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
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
public class MapEditor implements TileChangeListener, HexMapInputListener {

    private final MultiverseMain main;
    private Window mainMenu = null;
    private MapData mapData;
    private HexCoordinate currentTilePosition;
    private RadioButtonGroup tilePButtonGroup;

    public MapEditor(MultiverseMain main) {
        this.main = main;
        initEditorGUI();
        initMap();
    }

    /**
     * Method called each time a left mouse action is done.
     */
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
        if (main.getScreen().getElementById("mainWin") != null) {
            main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
        }
        mainMenu = new Window(main.getScreen(), "mainWin", new Vector2f(15f, 15f), new Vector2f(130, 40 * 5));
        mainMenu.setWindowTitle("Map Editor");
        mainMenu.setMinDimensions(new Vector2f(130, 130));
        mainMenu.setIsResizable(false);
        mainMenu.getDragBar().setIsMovable(false);
        main.getScreen().addElement(mainMenu);
        EditorMainAppState editorMain = main.getStateManager().getState(EditorMainAppState.class);
        if (editorMain != null) {
//            editorMain.populateReturnEditorMain(mainMenu);
        }

        /**
         * Button used to change the current map elemental attribut.
         *
         * @todo show the window only if there is a field generated.
         */
        Button mapElement = new ButtonAdapter(main.getScreen(), "mapProperties", new Vector2f(15, 40)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!mainMenu.getElementsAsMap().containsKey("EWindows") && !mapData.getAllChunkPos().isEmpty()) {
                    removeWin();
                    elementalWindow();
                } else if (mainMenu.getElementsAsMap().containsKey("EWindows")) {
                    mainMenu.removeChild(mainMenu.getElementsAsMap().get("EWindows"));
                } else {
                    System.err.println("No field loaded, load a field !");
                }
            }
        };
        mapElement.setText("Map Properties");
        mainMenu.addChild(mapElement);

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
         * Open the context menu to edit the room. load asset and stuff.
         */
        Button areaEdit = new ButtonAdapter(main.getScreen(), "AreaEdit", new Vector2f(15, 40 * 3)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                System.err.println("TODO");
            }
        };
        areaEdit.setText("Area Edit");
        mainMenu.addChild(areaEdit);

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
     * Context menu used to let you chose the element for map to change to.
     */
//    Window eWin;
    public final void elementalWindow() {
        final Window eWin = new Window(main.getScreen(), "EWindows",
                new Vector2f(mainMenu.getWidth() + 10, 0), new Vector2f(340, FastMath.ceil(new Float(ElementalAttribut.getSize()) / 3 + 1) * 40 + 12));
        eWin.removeAllChildren();
        eWin.setIsResizable(false);
        eWin.setIsMovable(false);

        RadioButtonGroup elementG = new RadioButtonGroup(main.getScreen(), "EButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index != ElementalAttribut.getSize()) {
                    mapData.setMapElement(ElementalAttribut.convert((byte) index));
                } else {
                    main.getScreen().getElementById(mainMenu.getUID()).removeChild(eWin);
                }
            }
        };
        int offSetX = 0;
        int offSetY = -1;
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            if (i % 3 == 0) {
                offSetX = 0;
                offSetY++;
            }
            ButtonAdapter button = new ButtonAdapter(main.getScreen(),
                    "EButton+" + ElementalAttribut.convert((byte) i), new Vector2f(10 + (110 * offSetX), 12 + (40 * offSetY)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            elementG.addButton(button);
            offSetX++;
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "EClose", new Vector2f(10, 12 + (40 * (offSetY + 1))));
        closeButton.setText("CLOSE");
        elementG.addButton(closeButton);
        elementG.setDisplayElement(eWin);

        elementG.setSelected(mapData.getMapElement().ordinal());
        mainMenu.addChild(eWin);
    }

    /**
     * Method used to open a window related to the selected hex.
     *
     * @param tile selected one.
     */
    void openHexPropertiesWin(HexCoordinate tile) {
        currentTilePosition = tile;
        if (main.getScreen().getElementById("tileP") != null) {
            tilePButtonGroup.setSelected(mapData.getTile(tile).getElement().ordinal());
        } else {
            tilePropertiesWin();
        }
    }

    /**
     * Context menu used to show the tile properties.
     */
    private void tilePropertiesWin() {
        Window tileWin = new Window(main.getScreen(), "tileP", new Vector2f(main.getScreen().getWidth() - 180, 0),
                new Vector2f(155f, 40 + (40 * (ElementalAttribut.getSize() + 1))));
        tileWin.setWindowTitle("    Tile Properties");
        tileWin.setIsResizable(false);
        tileWin.getDragBar().setIsMovable(false);
        mainMenu.addChild(tileWin);

        tilePButtonGroup = new RadioButtonGroup(main.getScreen(), "tilePButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index < ElementalAttribut.getSize()) {
                    mapData.setTile(currentTilePosition, new HexTile(ElementalAttribut.convert((byte) index),
                            (byte) mapData.getTile(currentTilePosition).getHeight()));
                } else if (index == ElementalAttribut.getSize()) {
                    main.getScreen().removeElement(main.getScreen().getElementById("tileP"));
                }
            }
        };
        /**
         * Button used to change the element of a tile.
         */
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "TButton+" + ElementalAttribut.convert((byte) i),
                    new Vector2f(10, 40 + (40 * i)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            tilePButtonGroup.addButton(button);
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "TClose", new Vector2f(10, 40 + (40 * ElementalAttribut.getSize())));
        closeButton.setText("CLOSE");
        tilePButtonGroup.addButton(closeButton);

        /**
         * Button used to move up a selected tile.
         */
        Button upButton = new ButtonAdapter(main.getScreen(), "UP", new Vector2f(120, 40), new Vector2f(25, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() + 1));
            }
        };
//        upButton.setText("UP");
        tileWin.addChild(upButton);

        /**
         * Button used to move down a selected tile.
         */
        Button downButton = new ButtonAdapter(main.getScreen(), "Down",
                new Vector2f(120, (20 + (40 * ElementalAttribut.getSize()))), new Vector2f(25, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() - 1));
            }
        };
//        downButton.setText("DOWN");
        tileWin.addChild(downButton);

        tilePButtonGroup.setDisplayElement(tileWin);
        tilePButtonGroup.setSelected(mapData.getTile(currentTilePosition).getElement().ordinal());

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
                        Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, "Cannot locate the MapData Folder.", FileNotFoundException.class);
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

    public void cleanup() {
        mapData.removeTileChangeListener(this);
        main.getStateManager().getState(HexMapMouseSystem.class).removeTileInputListener(this);
    }
}
