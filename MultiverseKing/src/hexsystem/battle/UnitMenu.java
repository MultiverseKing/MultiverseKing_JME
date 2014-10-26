package hexsystem.battle;

import com.jme3.renderer.Camera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
class UnitMenu extends ContextualMenu {

    UnitMenu(Screen screen, BattleSystem guiSystem, Camera camera) {
        super(screen, camera, guiSystem);
        ((Menu) screenElement).addMenuItem("Contextual unitMenu Menu Item 1", 0, null);
        ((Menu) screenElement).addMenuItem("Contextual unitMenu Menu Item 2", 1, null);
        ((Menu) screenElement).addMenuItem("Contextual unitMenu Menu Item 3", 2, null);
        ((Menu) screenElement).addMenuItem("Contextual unitMenu Menu Item 4", 3, null);
//        screen.addElement(screenElement);
    }
}
