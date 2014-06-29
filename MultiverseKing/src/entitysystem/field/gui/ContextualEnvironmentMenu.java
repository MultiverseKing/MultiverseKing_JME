package entitysystem.field.gui;

import com.jme3.renderer.Camera;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ContextualEnvironmentMenu extends ActionMenu {

    public ContextualEnvironmentMenu(Screen screen, GUIRenderSystem guiSystem, Camera camera) {
        super(screen, guiSystem, camera);
        menu.addMenuItem("Contextual environmentMenu Menu Item 1", 0, null);
        menu.addMenuItem("Contextual environmentMenu Menu Item 2", 1, null);
        menu.addMenuItem("Contextual environmentMenu Menu Item 3", 2, null);
        menu.addMenuItem("Contextual environmentMenu Menu Item 4", 3, null);
        screen.addElement(menu);
    }
    
}
