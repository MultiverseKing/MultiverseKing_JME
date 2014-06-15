package entitysystem.tonegod;

import static entitysystem.render.GUIRenderComponent.EntityType.ENVIRONMENT;
import static entitysystem.render.GUIRenderComponent.EntityType.TITAN;
import static entitysystem.render.GUIRenderComponent.EntityType.UNIT;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.simsilica.es.EntityId;
import entitysystem.render.GUIRenderComponent;
import entitysystem.render.GUIRenderSystem;
import hexsystem.HexMapMouseSystem;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ToneControl extends AbstractControl {
    private final GUIRenderSystem eSystem;
    private final Screen screen;
    private final Camera cam;
    private Menu titanMenu = null;
    private Menu unitMenu = null;
    private Menu environmentMenu = null;
    private GUIRenderComponent.EntityType field;
    private InteractiveNode interactiveNode;
    private EntityId entityId;

    public ToneControl(GUIRenderSystem eSystem, Screen screen, Camera cam) {
        this.eSystem = eSystem;
        this.screen = screen;
        this.cam = cam;
    }
    
    public ToneControl(EntityId entityId, GUIRenderSystem eSystem, Screen screen, GUIRenderComponent.EntityType field, Camera cam) {
        this.eSystem = eSystem;
        this.screen = screen;
        this.field = field;
        this.cam = cam;
        this.entityId = entityId;
        this.interactiveNode = getINode(getRenderMenu());
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
    
    public void updateMenuElement(GUIRenderComponent.EntityType field) {
        this.field = field;
        this.interactiveNode.setElement(getRenderMenu());
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
            HexMapMouseSystem hexSystem;
            @Override
            public void onSpatialRightMouseDown(MouseButtonEvent evt) {
            }

            @Override
            public void onSpatialRightMouseUp(MouseButtonEvent evt) {
                menu.showMenu(null, screen.getMouseXY().x, screen.getMouseXY().y - menu.getHeight());
                eSystem.setSelected(spatial);
            }

            @Override
            public void onSpatialLeftMouseDown(MouseButtonEvent evt) {
            }

            @Override
            public void onSpatialLeftMouseUp(MouseButtonEvent evt) {
            }
        };
    }
    
    public void showMenu(Vector3f origin) {
        Vector3f value = cam.getScreenCoordinates(spatial.getWorldTranslation());
        ((Menu)interactiveNode.getElement()).showMenu(null, value.x, value.y);
    }

    public InteractiveNode getNode() {
        return interactiveNode;
    }

    public GUIRenderComponent.EntityType getField() {
        return field;
    }
    
//    @Override
//    public void setSpatial(Spatial spatial) {
//        spatial.getParent().attachChild(interactiveNode);
//        interactiveNode.attachChild(spatial);
//        super.setSpatial(spatial);
//        interactiveNode.setDefaultMaterial(((Geometry) ((Node) spatial).getChild(0)).getMaterial());
//    }

//    public void remove(){
//        Node s = interactiveNode.getParent();
//        s.detachChild(interactiveNode);
//        s.attachChild(spatial);
//    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if (interactiveNode.element.getIsVisible()) {
            Vector3f value = cam.getScreenCoordinates(spatial.getWorldTranslation());
            if (value.x > 0 && value.x < screen.getWidth()
                    && value.y > 0 && value.y < screen.getHeight()) {
                interactiveNode.element.setPosition(new Vector2f(value.x, value.y));
            }
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}