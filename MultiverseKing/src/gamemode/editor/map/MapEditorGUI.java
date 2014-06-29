package gamemode.editor.map;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import gamemode.editor.EditorMenu;
import gamemode.editor.EditorMenuWindow;
import hexsystem.HexTile;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;
import utility.ElementalAttribut;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public final class MapEditorGUI extends EditorMenu {

    private final MultiverseMain main;
    private final MapEditorSystem mapEditorSystem;
    private EditorMenuWindow current;
    private Window eWin;
    private int currentIndex;
    private Window tileWin;
    private RadioButtonGroup tilePButtonGroup;
    private HexCoordinate selectedCoord;

    public MapEditorGUI(MultiverseMain main, MapEditorSystem mapEditorSystem) {
        super(main.getScreen(), "MapEditorGui", "Map Editor", mapEditorSystem);
        this.main = main;
        this.mapEditorSystem = mapEditorSystem;
        addItem("Edit World", 0);
        addItem("Edit Map", 1);
        addItem("Edit Area", 2);
        addAdditionalField("Map Element");
        addAdditionalField("Tile Element");
        populateEditor();
    }

    public void setSelectedTile(HexTile tile, HexCoordinate tilePos){
        selectedCoord = tilePos;
        if(tileWin != null){
            tilePButtonGroup.setSelected(tile.getElement().ordinal());
        }
    }
    
    @Override
    protected void onAdditionalFieldTrigger(int value) {
        Window menu = null;
        String menuId = null;
        switch (value) {
            case 0:
                if(tileWin != null && getChildElementById("tilePropertiesWin") != null){
                    removeChild(getChildElementById("tilePropertiesWin"));
                }
                menu = eWin;
                menuId = "TestElementWindow";
                break;
            case 1:
                if(selectedCoord != null){
                    if(eWin != null && getChildElementById("TestElementWindow") != null){
                        removeChild(getChildElementById("TestElementWindow"));
                    }
                    menu = tileWin;
                    menuId = "tilePropertiesWin";
                }
                break;
        }
        if(menuId != null){
            if(menu == null && getChildElementById(menuId) == null){
                generateMenu(menuId);
            } else if (menu != null && getChildElementById(menuId) == null) {
                addChild(menu);
            } else if (menu != null && getChildElementById(menuId) != null) {
                removeChild(getChildElementById(menuId));
            } else {
                throw new UnknownError("No idea...");
            }
        }
    }

    @Override
    protected void onSelectedItemChange(int index) {
        if (current != null && currentIndex != index) {
            current.detachFromParent();
        } else if (current != null && currentIndex == index){
            return;
        }
        switch (index) {
            case 0:
                /**
                 * @todo: see GlobeHexMap_0_2_2.
                 */
                break;
            case 1:
                /**
                 * @todo: see mighty quest for epic loot.
                 */
                break;
            case 2:
                current = new AreaEditorMenu(screen, this, (MapEditorSystem) getSystem());
                break;
        }
        currentIndex = index;
    }
    
    private void generateMenu(String menuId){
        if(menuId.equals("TestElementWindow")){
            genElementalWindow();
        } else if(menuId.equals("tilePropertiesWin")){
            genPropertiesWindow();
        }
    }
    
    /**
     * Context menu used to let you chose the element for map to change to.
     */
    public void genElementalWindow() {
        eWin = new Window(main.getScreen(), "TestElementWindow",
                new Vector2f(0, -getChildElementById("CloseRootButtonWin").getPosition().y
                + 25),
                new Vector2f(340, FastMath.ceil(new Float(ElementalAttribut.getSize()) / 3 + 1) * 40 + 12));
        eWin.removeAllChildren();
        eWin.setIsResizable(false);
        eWin.setIsMovable(false);

        RadioButtonGroup elementG = new RadioButtonGroup(main.getScreen(), "TestElementButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index != ElementalAttribut.getSize()) {
                    mapEditorSystem.setMapElement(ElementalAttribut.convert((byte) index));
                } else {
                    removeChild(eWin);
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
                    "TestElementButton+" + ElementalAttribut.convert((byte) i), new Vector2f(10 + (110 * offSetX), 12 + (40 * offSetY)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            elementG.addButton(button);
            offSetX++;
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "TestElementClose", new Vector2f(10, 12 + (40 * (offSetY + 1))));
        closeButton.setText("CLOSE");

        elementG.addButton(closeButton);
        elementG.setDisplayElement(eWin);
        elementG.setSelected(mapEditorSystem.getCurrentMapElement().ordinal());
        addChild(eWin);
    }
    
    private void genPropertiesWindow() {
        tileWin = new Window(screen, "tilePropertiesWin",
                new Vector2f(0, -getChildElementById("CloseRootButtonWin").getPosition().y
                + 25),
                new Vector2f(155f, 40 + (40 * (ElementalAttribut.getSize() ))));
        tileWin.setWindowTitle("    Tile Properties");
        tileWin.setIsResizable(false);
        tileWin.setMinDimensions(Vector2f.ZERO);
//        tileWin.getDragBar().setIsMovable(false);

        tilePButtonGroup = new RadioButtonGroup(screen, "tilePButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index < ElementalAttribut.getSize()) {
                    mapEditorSystem.updateTileProperties(ElementalAttribut.convert((byte) index));
                }
            }
        };
        /**
         * Button used to change the element of a tile.
         */
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            ButtonAdapter button = new ButtonAdapter(screen, "tilePropertiesWinButton+" + ElementalAttribut.convert((byte) i),
                    new Vector2f(10, 40 + (40 * i)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            tilePButtonGroup.addButton(button);
        }
//        Button closeButton = new ButtonAdapter(screen, "tilePropertiesWinClose", new Vector2f(10, 40 + (40 * ElementalAttribut.getSize())));
//        closeButton.setText("CLOSE");
//        tilePButtonGroup.addButton(closeButton);

        /**
         * Button used to move up a selected tile.
         */
        Button upButton = new ButtonAdapter(screen, "UP", new Vector2f(120, 40), new Vector2f(25, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapEditorSystem.updateTileProperties(1);
            }
        };
//        upButton.setText("UP");
        tileWin.addChild(upButton);

        /**
         * Button used to move down a selected tile.
         */
        Button downButton = new ButtonAdapter(screen, "Down",
                new Vector2f(120, (20 + (40 * (ElementalAttribut.getSize()-1)))), new Vector2f(25, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapEditorSystem.updateTileProperties(-1);
            }
        };
//        downButton.setText("DOWN");
        tileWin.addChild(downButton);

        tilePButtonGroup.setDisplayElement(tileWin);
        addChild(tileWin);
    }
}
