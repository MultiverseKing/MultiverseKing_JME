package gamemode.editor.map;

import gamemode.editor.EditorMenuWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class AreaEditorMenu extends EditorMenuWindow {

    private final MapEditorSystem mapEditorSystem;

    public AreaEditorMenu(ElementManager screen, Element parent, MapEditorSystem system) {
        super(screen, parent, "Area Editor");
        this.mapEditorSystem = system;
//        addButtonField("Edit Tile", "Edit", 0, new Vector2f(0, -10));
//        show(getGridSize().x * 2, getGridSize().y * 3, VAlign.center);
    }

    @Override
    protected void onButtonTrigger(int index) {
    }
}
