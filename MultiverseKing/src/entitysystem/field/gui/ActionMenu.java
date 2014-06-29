package entitysystem.field.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityId;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
abstract class ActionMenu {

    private final Screen screen;
    private final Camera camera;
    protected final GUIRenderSystem system;
    protected Vector3f inspectedSpatialPosition;
    protected EntityId inspectedEntityId = null;
    protected Menu menu;

    ActionMenu(Screen screen, GUIRenderSystem guiSystem, Camera camera) {
        this.screen = screen;
        this.camera = camera;
        this.system = guiSystem;
        
        menu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                system.setAction(inspectedEntityId, (Integer)value);
            }
        };
    }
    
    void update() {
        if (menu.getIsVisible()) {
            Vector3f value = camera.getScreenCoordinates(inspectedSpatialPosition);
            if (value.x > 0 && value.x < screen.getWidth()*0.9f
                    && value.y > 0 && value.y < screen.getHeight()*0.9f) {
                menu.setPosition(new Vector2f(value.x, value.y));
            } else {
                menu.hideMenu();
            }
        }
    }
    
    public final void show(EntityId id, Vector3f pos){
        inspectedEntityId = id;
        inspectedSpatialPosition = pos;
        Vector3f value = camera.getScreenCoordinates(pos);
        menu.showMenu(null, value.x, value.y);
    }
}
