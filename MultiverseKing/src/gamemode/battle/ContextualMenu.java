package gamemode.battle;

import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityId;
import gamemode.gui.CameraTrackWindow;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
abstract class ContextualMenu extends CameraTrackWindow {

    private final BattleSystem system;
    private EntityId inspectedEntityId = null;

    ContextualMenu(Screen screen, Camera camera, BattleSystem guiSystem) {
        super(screen, camera);
        this.system = guiSystem;
        screenElement = new Menu(screen, new Vector2f(), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                system.setAction(inspectedEntityId, (Integer) value);
            }
        };
        screen.addElement(screenElement);
    }

    void show(HexCoordinate pos, EntityId id) {
        super.show(pos);
        inspectedEntityId = id;
    }
}
