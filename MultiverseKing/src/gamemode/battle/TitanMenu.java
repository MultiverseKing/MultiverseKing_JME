package gamemode.battle;

import com.jme3.renderer.Camera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
class TitanMenu extends ContextualMenu {

    TitanMenu(Screen screen, BattleGUISystem guiSystem, Camera camera) {
        super(screen, camera, guiSystem);
        ((Menu) screenElement).addMenuItem(" Move        ", 0, null);
        ((Menu) screenElement).addMenuItem(" Ability     ", 1, null);
        ((Menu) screenElement).addMenuItem(" Stats       ", 2, null);
//        screen.addElement(screenElement);
    }
}
