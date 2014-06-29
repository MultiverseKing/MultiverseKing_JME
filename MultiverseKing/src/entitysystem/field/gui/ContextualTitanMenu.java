package entitysystem.field.gui;

import com.jme3.renderer.Camera;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ContextualTitanMenu extends ActionMenu {

    public ContextualTitanMenu(Screen screen, GUIRenderSystem guiSystem, Camera camera) {
        super(screen, guiSystem, camera);
        menu.addMenuItem(" Move        ", 0, null);
        menu.addMenuItem(" Ability     ", 1, null);
        menu.addMenuItem(" Inventory   ", 2, null);
        menu.addMenuItem(" Stats       ", 3, null);
        screen.addElement(menu);
    }
}
