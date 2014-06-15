package entitysystem.render;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class GUIFieldMenu {

    private final Screen screen;
    private final Menu[] menu = new Menu[3];
    private final Camera camera;
    private Vector3f inspectedSpatialPosition;
    private byte menuIndex;
//    private final Menu environmentMenu;
//    private final Menu titanMenu;
//    private final Menu unitMenu;

    GUIFieldMenu(Screen screen, Camera camera) {
        this.screen = screen;
        this.camera = camera;

        /**
         * Titan Menu !
         */
        menu[0] = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                switch (index) {
                    case 0:
//                        eSystem.move(entityId, field);
                }
            }
        };
        menu[0].addMenuItem(" Move        ", 0, null);
        menu[0].addMenuItem(" Ability     ", 1, null);
        menu[0].addMenuItem(" Inventory   ", 2, null);
        menu[0].addMenuItem(" Stats       ", 3, null);
        screen.addElement(menu[0]);

        /**
         * Unit Menu !
         */
        menu[1] = new Menu(screen, Vector2f.ZERO, false) {

            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        menu[1].addMenuItem("Contextual unitMenu Menu Item 1", 0, null);
        menu[1].addMenuItem("Contextual unitMenu Menu Item 2", 1, null);
        menu[1].addMenuItem("Contextual unitMenu Menu Item 3", 2, null);
        menu[1].addMenuItem("Contextual unitMenu Menu Item 4", 3, null);
        screen.addElement(menu[1]);

        /**
         * Environment Menu !
         */
        menu[2] = new Menu(screen, Vector2f.ZERO, false) {

            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        menu[2].addMenuItem("Contextual environmentMenu Menu Item 1", 0, null);
        menu[2].addMenuItem("Contextual environmentMenu Menu Item 2", 1, null);
        menu[2].addMenuItem("Contextual environmentMenu Menu Item 3", 2, null);
        menu[2].addMenuItem("Contextual environmentMenu Menu Item 4", 3, null);
        screen.addElement(menu[2]);
    }

    void show(GUIRenderComponent.EntityType type, Vector3f pos) {
        inspectedSpatialPosition = pos;
        Vector3f value = camera.getScreenCoordinates(pos);
        switch (type) {
            case ENVIRONMENT:
                menuIndex = 2;
                menu[menuIndex].showMenu(null, value.x, value.y);
                break;
            case TITAN:
                menuIndex = 0;
                menu[menuIndex].showMenu(null, value.x, value.y);
                break;
            case UNIT:
                menuIndex = 1;
                menu[menuIndex].showMenu(null, value.x, value.y);
                break;
            default:
                throw new UnsupportedOperationException(type.name() + "Is not a valid type for : " + getClass().toString());
        }
    }

    public void update() {
        if (menu[menuIndex].getIsVisible()) {
            Vector3f value = camera.getScreenCoordinates(inspectedSpatialPosition);
            if (value.x > 0 && value.x < screen.getWidth()*0.9f
                    && value.y > 0 && value.y < screen.getHeight()*0.9f) {
                menu[menuIndex].setPosition(new Vector2f(value.x, value.y));
            } else {
                menu[menuIndex].hideMenu();
            }
        }
    }
}
