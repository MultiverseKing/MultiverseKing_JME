package entitysystem.tonegod;

import static entitysystem.field.FieldGUIComponent.EntityType.ENVIRONMENT;
import static entitysystem.field.FieldGUIComponent.EntityType.TITAN;
import static entitysystem.field.FieldGUIComponent.EntityType.UNIT;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.es.EntityId;
import entitysystem.field.FieldGUIComponent;
import entitysystem.field.InteractiveFieldSystem;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ToneControl extends AbstractControl {
    private Menu titanMenu = null;
    private Menu unitMenu = null;
    private Menu environmentMenu = null;
    private final InteractiveFieldSystem eSystem;
    private final EntityId entityId;
    private final Screen screen;
    private final Camera cam;
    private FieldGUIComponent.EntityType field;
    private InteractiveNode toneNode;

    public ToneControl(EntityId entityId, InteractiveFieldSystem eSystem, Screen screen, FieldGUIComponent.EntityType field, Camera cam) {
        this.eSystem = eSystem;
        this.screen = screen;
        this.field = field;
        this.cam = cam;
        this.entityId = entityId;
        this.toneNode = getINode(getRenderMenu());
    }

    // <editor-fold defaultstate="collapsed" desc="Menu Method">
    private void initTitanMenu() {
        titanMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                switch(index){
                    case 0:
                        eSystem.move(entityId, field);
                }
            }            
        };
        titanMenu.addMenuItem(" Move        ", 0, null);
        titanMenu.addMenuItem(" Ability     ", 1, null);
        titanMenu.addMenuItem(" Inventory   ", 2, null);
        titanMenu.addMenuItem(" Stats       ", 3, null);
        screen.addElement(titanMenu);
    }

    private void initUnitMenu() {
        unitMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 1", 0, null);
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 2", 1, null);
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 3", 2, null);
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 4", 3, null);
        screen.addElement(unitMenu);
    }

    private void initEnvironmentMenu() {
        environmentMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 1", 0, null);
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 2", 1, null);
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 3", 2, null);
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 4", 3, null);
        screen.addElement(environmentMenu);
    }
    // </editor-fold>
    
    public void updateMenuElement(FieldGUIComponent.EntityType field) {
        this.field = field;
        this.toneNode.setElement(getRenderMenu());
    }
    
    private Menu getRenderMenu() {
        switch (field) {
            case ENVIRONMENT:
                if(environmentMenu == null){
                    initEnvironmentMenu();
                }
                return environmentMenu;
            case TITAN:
                if(titanMenu == null){
                    initTitanMenu();
                }
                return titanMenu;
            case UNIT:
                if(unitMenu == null){
                    initUnitMenu();
                }
                return unitMenu;
            default:
                throw new UnsupportedOperationException(field.name() + "Is not a supported type.");
        }
    }

    private InteractiveNode getINode(final Menu menu) {
        return new InteractiveNode(screen, this, menu) {
            @Override
            public void onSpatialRightMouseDown(MouseButtonEvent evt) {
            }

            @Override
            public void onSpatialRightMouseUp(MouseButtonEvent evt) {
                menu.showMenu(null, screen.getMouseXY().x, screen.getMouseXY().y - menu.getHeight());
            }

            @Override
            public void onSpatialLeftMouseDown(MouseButtonEvent evt) {
            }

            @Override
            public void onSpatialLeftMouseUp(MouseButtonEvent evt) {
            }
        };
    }

    public InteractiveNode getNode() {
        return toneNode;
    }

    public FieldGUIComponent.EntityType getField() {
        return field;
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        spatial.getParent().attachChild(toneNode);
        toneNode.attachChild(spatial);
        super.setSpatial(spatial);
        toneNode.setDefaultMaterial(((Geometry) ((Node) spatial).getChild(0)).getMaterial());
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (toneNode.element.getIsVisible()) {
            Vector3f value = cam.getScreenCoordinates(spatial.getWorldTranslation());
            if (value.x > 0 && value.x < screen.getWidth()
                    && value.y > 0 && value.y < screen.getHeight()) {
                toneNode.element.setPosition(new Vector2f(value.x, value.y));
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    
}