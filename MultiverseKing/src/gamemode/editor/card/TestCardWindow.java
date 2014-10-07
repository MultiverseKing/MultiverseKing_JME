package gamemode.editor.card;

import com.jme3.math.Vector2f;
import gamemode.gui.EditorWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
final class TestCardWindow extends EditorWindow {

    private final CardEditorSystem system;

    /**
     * Window used to add or remove card from the GUI.
     */
    TestCardWindow(Screen screen, Element parent, CardEditorSystem system) {
        super(screen, parent, "Test Card");
        this.system = system;

        addButtonField("Add Card", "+1", new Vector2f(0, -5));
        addButtonField("Remove Card", "-1", new Vector2f(getLayoutGridSize().x, -8));
        showConstrainToParent(VAlign.bottom, null);
        getWindow().setPosition(new Vector2f(getWindow().getPosition().x,
                getWindow().getPosition().y - screen.getElementById("ReturnButtonWin").getHeight() - 15));
        getWindow().scale(0.8f);
    }

    @Override
    protected void onButtonTrigger(String label) {
        if (label.equals("+1")) {
                system.addEntityCard("Cendrea");
        } else if(label.equals("-1")){
                system.removeEntityCard();
        }
    }
}
