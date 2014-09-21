package gamemode.editor.map;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;
import utility.ElementalAttribut;

/**
 * Context menu used to let you chose the element for map to change to.
 *
 * @author roah
 */
class ElementalWindow extends Window {

    private final RadioButtonGroup elementG;

    ElementalWindow(final ElementManager screen, final AreaEditorSystem system, Vector2f position) {
        super(screen, "TestEAttributWindow", position,
                new Vector2f(340, FastMath.ceil(new Float(ElementalAttribut.getSize()) / 3 + 1) * 40 + 12));

//        eWin = new Window(screen, "TileEAttributWindow",
//                new Vector2f(0, -getChildElementById("CloseRootButtonWin").getPosition().y
//                + 25),
//                new Vector2f(340, FastMath.ceil(new Float(ElementalAttribut.getSize()) / 3 + 1) * 40 + 12));

        removeAllChildren();
        setIsResizable(false);
        setIsMovable(false);

        elementG = new RadioButtonGroup(screen, "TestElementButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index != ElementalAttribut.getSize()) {
                    system.setMapElement(ElementalAttribut.convert((byte) index));
                } else {
                    screen.getElementById("TestEAttributWindow").hide();
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
            ButtonAdapter button = new ButtonAdapter(screen,
                    "TestEAttributButton+" + ElementalAttribut.convert((byte) i), new Vector2f(10 + (110 * offSetX), 12 + (40 * offSetY)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            elementG.addButton(button);
            offSetX++;
        }
        Button closeButton = new ButtonAdapter(screen, "TestEAttributClose", new Vector2f(10, 12 + (40 * (offSetY + 1))));
        closeButton.setText("CLOSE");

        elementG.addButton(closeButton);
        init(elementG, system);
    }

    private void init(RadioButtonGroup elementG, AreaEditorSystem system) {
        elementG.setDisplayElement(this);
        elementG.setSelected(system.getMapElement().ordinal());
    }

    public void setSelected(int index) {
        elementG.setSelected(index);
    }
}
