package entitysystem.field;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.RenderSystem;
import hexsystem.HexMapMouseInput;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapRayListener;
import java.util.ArrayList;
import kingofmultiverse.MultiverseMain;
import kingofmultiverse.RTSCamera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import utility.ExtendedNode;

/**
 * How the player can interact with element on the field. This system need the
 * EntityRenderSystem && HexMapMouseInput to work or it can't work properly
 * since it need raycast and raycast need object to be rendered on the field to
 * collide with.
 *
 * @author roah
 */
public class EntityFieldSystem extends EntitySystemAppState implements HexMapRayListener {

    /**
     * Contain all Element the player can interact with on the Field.
     */
    private ArrayList<EntityId> elements = new ArrayList<EntityId>();
//    private Node renderSystemNode;
    private Spatial rayDebug;
    private HexMapMouseInput mouseInput;
    private Screen screen;
    private Menu actionUnitMenu = null;
    private Camera cam;
    private Vector3f camPos;
    private Vector3f unitPosition;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(RenderSystem.class) == null) {
            app.getStateManager().attach(new RenderSystem());
        }
        if (app.getStateManager().getState(HexMapMouseInput.class) == null) {
            app.getStateManager().attach(new HexMapMouseInput());
        }

        RenderSystem renderSystem = app.getStateManager().getState(RenderSystem.class);
        
//        renderSystemNode = renderSystem.getNode();
        screen = ((MultiverseMain)app).getScreen();
        screen.setUse3DSceneSupport(true);
        mouseInput = app.getStateManager().getState(HexMapMouseInput.class);
        rayDebug = mouseInput.getRayDebug();
        
        mouseInput.registerRayInputListener(this);
        cam = app.getStateManager().getState(RTSCamera.class).getCamera();
        
//        layoutGUI();
//        app.getInputManager().addListener(inputListener, "Cancel");
        return entityData.getEntities(EntityFieldComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        if(actionUnitMenu != null){
            Vector3f value = cam.getScreenCoordinates(unitPosition);
            if(value.x > 0 && value.x < screen.getWidth()&&
                    value.y > 0 && value.y < screen.getHeight())
            actionUnitMenu.setPosition(new Vector2f(value.x, value.y));
        }
    }

    @Override
    protected void addEntity(Entity e) {
        elements.add(e.getId());
    }

    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void removeEntity(Entity e) {
        elements.remove(e.getId());
    }

    
    /**
     * Handle that in the RenderSystem not there.
     * @param ray
     * @return 
     */
    public HexMapInputEvent leftRayInputAction(Ray ray) {
        return null;
    }

    public HexMapInputEvent rightRayInputAction(Ray ray) {
//        CollisionResults results = new CollisionResults();
////        Ray ray = event.getLastUsedRay();
////        renderSystemNode.collideWith(event.getLastUsedRay(), results);
//        renderSystemNode.collideWith(ray, results);
//        if (results.size() != 0) {
//            if (results.size() > 0) {
//                CollisionResult closest = results.getClosestCollision();
//
//                for (EntityId id : elements) {
//                    if(actionUnitMenu != null && actionUnitMenu.getUID().equals(id.toString())){
//                        return null;
//                    }
//                    if (closest.getGeometry().getParent().getName().equals(id.toString())) {
//                        rayDebug.setLocalTranslation(closest.getContactPoint());
//                        app.getRootNode().attachChild(rayDebug);
//                        openEntityActionMenu(id);
//                        return new HexMapInputEvent(entityData.getComponent(id,
//                                HexPositionComponent.class).getPosition(), ray);
//                    }
//                }
//            }
//        }
        return null;
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        
        for (EntityId id : elements) {
            HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
            if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
                if(actionUnitMenu != null && actionUnitMenu.getUID().equals(id.toString())){
                    return;
                }
                openEntityActionMenu(id);
                return;
            }
        }
    }

    private ActionListener inputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("ActionMenu") && !keyPressed) {
                /**
                 * Remove action menu
                 */
//                screen.removeElement(screen.getElementById("actionUnitMenu"));
//                app.getInputManager().removeListener(this);
            }
        }
    };
    
    private void openEntityActionMenu(EntityId id) {
//        actionUnitMenu.showMenu(null, screen.getMouseXY().getX(), screen.getMouseXY().getY()-actionUnitMenu.getHeight());
//        unitPosition = entityData.getComponent(id, HexPositionComponent.class).getPosition().convertToWorldPosition();
        layoutGUI(id);
    }
    private void layoutGUI(EntityId id) {
        createMenus();
 
        Box box = new Box(1,1,1);
        Sphere sphere = new Sphere(12,12,1);
 
        Material mat = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
 
        Node n = getNewGUINode(renderSystemNode.getChild(id.toString()), mat.clone(), menu);
        n.setName(id.toString());
 
        renderSystemNode.attachChild(n);
    }
    
    Menu menu, menu2;
    private void createMenus() {
        menu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
 
            }
        };
        menu.addMenuItem("Move      ", 0, null);
        menu.addMenuItem("Inventory ", 1, null);
        menu.addMenuItem("FLy       ", 2, null);
        menu.addMenuItem("Skill     ", 3, null);
        screen.addElement(menu);
 
//        menu2 = new Menu(screen, Vector2f.ZERO, false) {
//            @Override
//            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
// 
//            }
//        };
//        menu2.addMenuItem("Contextual Sphere Menu Item 1", 0, null);
//        menu2.addMenuItem("Contextual Sphere Menu Item 2", 1, null);
//        menu2.addMenuItem("Contextual Sphere Menu Item 3", 2, null);
//        menu2.addMenuItem("Contextual Sphere Menu Item 4", 3, null);
//        screen.addElement(menu2);
    }

    private Node getNewGUINode(Spatial obj, Material mat, final Menu menu) {
//        Geometry geom = new Geometry();
//        geom.setMesh(mesh);
        Node n = (Node) obj;
        Geometry geo = (Geometry) n.getChild("Material");
        
        ExtendedNode node = new ExtendedNode() {
            public void onGetFocus(MouseMotionEvent evt) {
                ((Geometry)getChild(0)).getMaterial().setColor("Color", ColorRGBA.Yellow);
                evt.setConsumed();
            }
            public void onLoseFocus(MouseMotionEvent evt) {
                ((Geometry)getChild(0)).getMaterial().setColor("Color", ColorRGBA.Blue);
                evt.setConsumed();
            }
            public void onMouseLeftPressed(MouseButtonEvent evt) {  }
            public void onMouseLeftReleased(MouseButtonEvent evt) {  }
            public void onMouseRightPressed(MouseButtonEvent evt) {  }
            public void onMouseRightReleased(MouseButtonEvent evt) {
                menu.showMenu(null, screen.getMouseXY().x, screen.getMouseXY().y-menu.getHeight());
                evt.setConsumed();
            }
        };
        node.attachChild(geo);
        
//        node.setMaterial(mat);
        return node;
    }
    
    @Override
    protected void cleanupSystem() {
        elements.clear();
        screen.setUse3DSceneSupport(false);
    }
}