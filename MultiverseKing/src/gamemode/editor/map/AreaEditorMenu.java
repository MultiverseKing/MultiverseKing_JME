package gamemode.editor.map;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import gamemode.editor.EditorMenuWindow;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.LayoutHint.VAlign;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class AreaEditorMenu extends EditorMenuWindow {

    private final MapEditorSystem mapEditorSystem;
    private RadioButtonGroup tilePButtonGroup;
    private Window tileWin;

    public AreaEditorMenu(ElementManager screen, Element parent, MapEditorSystem system) {
        super(screen, parent, "Area Editor");
        this.mapEditorSystem = system;
        addButtonField("Edit Tile", "Edit", 0, new Vector2f(0, -10));
        show(getGridSize().x * 2, getGridSize().y * 3, VAlign.center);
        initTileEdit();
    }

    public void openTileProperties(ElementalAttribut eAttribut) {
        tilePButtonGroup.setSelected(eAttribut.ordinal());
    }

    @Override
    protected void onButtonTrigger(int index) {
        switch (index) {
            case 0:
                if (tileWin.getIsVisible()) {
                    tileWin.hide();
                } else {
                    tileWin.show();
                }

                break;
        }
    }

    private void initTileEdit() {
        tileWin = new Window(screen, "tilePropertiesWin",
                new Vector2f(screen.getWidth() - getWindow().getPosition().x - 170, 0),
                new Vector2f(155f, 40 + (40 * (ElementalAttribut.getSize() ))));
        tileWin.setWindowTitle("    Tile Properties");
        tileWin.setIsResizable(false);
        tileWin.getDragBar().setIsMovable(false);

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
        getWindow().addChild(tileWin);
        tileWin.hide();
    }
}
