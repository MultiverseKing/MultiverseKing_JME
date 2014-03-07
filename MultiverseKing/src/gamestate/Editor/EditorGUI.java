/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate.Editor;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import hexsystem.HexTile;
import hexsystem.MapData;
import kingofmultiverse.MultiverseMain;
import utility.attribut.ElementalAttribut;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;
import utility.HexCoordinate;

/**
 * @todo User should not be able to move the main windows.
 * @author roah
 */
class EditorGUI extends AbstractAppState {

    private final MapData mapData;
    private MultiverseMain main;
    private HexCoordinate currentTilePosition;
    private RadioButtonGroup tilePButtonGroup;

    EditorGUI(MapData mapData) {
        this.mapData = mapData;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        main = (MultiverseMain) app;

        Window win = new Window(main.getScreen(), "EditorMain", new Vector2f(15f, 15f), new Vector2f(150, 20 + 40 * 4));
        win.setWindowTitle("Main Windows");
//        win.setMinDimensions(new Vector2f(130, 130));
        win.setResizeS(true);
        win.setResizeN(false);
        win.setResizeW(false);
        win.setResizeE(false);
//        win.setWidth(new Float(50));
        main.getScreen().addElement(win);

        RadioButtonGroup mainButton = new RadioButtonGroup(main.getScreen(), "mainButton") {
            @Override
            public void onSelect(int index, Button value) {
//                System.out.println(index);
                if (index == 1) {
                    elementalWindow();
                    setSelected(0);
                } else if (index == 2) {
                    mapData.saveMap();
                    setSelected(0);
                } else if (index == 3) {
                    mapData.loadMap(main.getAssetManager());
                    setSelected(0);
                }
            }
        };

        ButtonAdapter reset = new ButtonAdapter(main.getScreen(), "reset", new Vector2f(120, 40), new Vector2f(15, 40 * 2 - 10));
        mainButton.addButton(reset);

        ButtonAdapter mapElement = new ButtonAdapter(main.getScreen(), "mapElement", new Vector2f(15, 40));
        mapElement.setText("Change Map Elements");
        mainButton.addButton(mapElement);

        ButtonAdapter save = new ButtonAdapter(main.getScreen(), "save", new Vector2f(15, 40 * 2));
        save.setText("Save");
        mainButton.addButton(save);

        ButtonAdapter load = new ButtonAdapter(main.getScreen(), "load", new Vector2f(15, 40 * 3));
        load.setText("Load");
        mainButton.addButton(load);

        mainButton.setDisplayElement(win);

        // Add it to out initial window
//        win.addChild(makeWindow);
    }

    public final void elementalWindow() {
        Window eWin =
                new Window(main.getScreen(), "EWindows",
                new Vector2f((main.getScreen().getWidth() / 2) - 175, (main.getScreen().getHeight() / 2) - 100));
        eWin.setWindowTitle("Elemental Windows");
        RadioButtonGroup elementG = new RadioButtonGroup(main.getScreen(), "EButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index != ElementalAttribut.getSize()) {
                    changeMapElement(ElementalAttribut.convert((byte) index));
                } else {
                    main.getScreen().removeElement(main.getScreen().getElementById("EWindows"));
                }
            }

            private void changeMapElement(ElementalAttribut eAttribut) {
//                main.getStateManager().getState(HexMap.class).changeZoneElement(eAttribut);
                mapData.setMapElement(eAttribut);
            }
        };
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "EButton+" + ElementalAttribut.convert((byte) i), new Vector2f(15, 40 + (40 * i)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            elementG.addButton(button);
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "EClose", new Vector2f(15, 40 + (40 * ElementalAttribut.getSize())));
        closeButton.setText("CLOSE");
        elementG.addButton(closeButton);
        elementG.setDisplayElement(eWin);

        main.getScreen().addElement(eWin);
    }

    void openWin(HexCoordinate tile) {
        currentTilePosition = tile;
        if (main.getScreen().getElementById("tileP") != null) {
            tilePButtonGroup.setSelected(mapData.getTile(tile).getElement().ordinal());
        } else {
            tilePropertiesWin();
        }
    }

    private void tilePropertiesWin() {
        Window tileWin = new Window(main.getScreen(), "tileP", new Vector2f(main.getScreen().getWidth() - 200, 20), new Vector2f(185f, 40 + (40 * (ElementalAttribut.getSize() + 1))));
        tileWin.setWindowTitle("Tile Properties");
        tilePButtonGroup = new RadioButtonGroup(main.getScreen(), "tilePButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index < ElementalAttribut.getSize()) {
                    mapData.setTile(currentTilePosition, new HexTile(ElementalAttribut.convert((byte) index), (byte) mapData.getTile(currentTilePosition).getHeight()));
                } else if (index == ElementalAttribut.getSize()) {
                    main.getScreen().removeElement(main.getScreen().getElementById("tileP"));
                }
            }
        };
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "TButton+" + ElementalAttribut.convert((byte) i), new Vector2f(15, 40 + (40 * i)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            tilePButtonGroup.addButton(button);
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "TClose", new Vector2f(15, 40 + (40 * ElementalAttribut.getSize())));
        closeButton.setText("CLOSE");
        tilePButtonGroup.addButton(closeButton);

        Button upButton = new ButtonAdapter(main.getScreen(), "UP", new Vector2f(120, 40), new Vector2f(50, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() + 1));
            }
        };
        closeButton.setText("UP");
        tileWin.addChild(upButton);

        Button downButton = new ButtonAdapter(main.getScreen(), "Down", new Vector2f(120, (20 + (40 * ElementalAttribut.getSize()))), new Vector2f(50, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() - 1));
            }
        };
        closeButton.setText("DOWN");
        tileWin.addChild(downButton);

        tilePButtonGroup.setDisplayElement(tileWin); // null adds the button list to the screen layer
        tilePButtonGroup.setSelected(mapData.getTile(currentTilePosition).getElement().ordinal());

        main.getScreen().addElement(tileWin);
    }
}