package gamemode.editor.map;

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

    public MapEditorGUI(MultiverseMain main, MapEditorSystem mapEditorSystem) {
        super(main.getScreen(), "MapEditorGui", "Map Editor", mapEditorSystem);
        this.main = main;
        this.mapEditorSystem = mapEditorSystem;
        addItem("Edit World", 0);
        addItem("Edit Map", 1);
        addItem("Edit Area", 2);
        addAdditionalField("Test Element");
        populateEditor();
    }

    public void setSelectedTile(HexTile tile, HexCoordinate tilePos){
        if(current != null && current instanceof AreaEditorMenu){
            ((AreaEditorMenu)current).openTileProperties(tile.getElement());
        }
    }
    
    @Override
    protected void onAdditionalFieldTrigger(int value) {
        switch (value) {
            case 0:
                if (eWin == null && getChildElementById("TestElementWindow") == null) {
                    elementalWindow();
                } else if (eWin != null && getChildElementById("TestElementWindow") == null) {
                    addChild(eWin);
                } else if (eWin != null && getChildElementById("TestElementWindow") != null) {
                    removeChild(getChildElementById("TestElementWindow"));
                } else {
                    throw new UnknownError("No idea...");
                }
                break;
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
    
    

    /**
     * Context menu used to let you chose the element for map to change to.
     */
    public final void elementalWindow() {
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
    
}
