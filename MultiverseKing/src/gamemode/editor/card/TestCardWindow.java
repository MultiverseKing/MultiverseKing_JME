package gamemode.editor.card;

import com.jme3.math.Vector2f;
import gamemode.editor.EditorWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.LayoutHint.VAlign;

/**
 *
 * @author roah
 */
final class TestCardWindow extends EditorWindow {

    private final CardEditorSystem system;

    /**
     * Window used to add or remove card from the GUI.
     */
    TestCardWindow(ElementManager screen, Element parent, CardEditorSystem system) {
        super(screen, parent, "Test Card");
        this.system = system;

        addButtonField("Add Card", "+1", 0, new Vector2f(0, -5));
        addButtonField("Remove Card", "-1", 1, new Vector2f(getGridSize().x, -8));
        showConstrainToParent(new Vector2f(getGridSize().x * 2.1f, getGridSize().y * 2.6f), VAlign.bottom);
        getWindow().setPosition(new Vector2f(getWindow().getPosition().x,
                getWindow().getPosition().y - screen.getElementById("ReturnButtonWin").getHeight() - 15));
        getWindow().scale(0.8f);
    }

    @Override
    protected void onButtonTrigger(int index) {
        switch (index) {
            case 0:
                system.addEntityCard("Cendrea");
                break;
            case 1:
                system.removeEntityCard();
                break;
        }
    }
}
