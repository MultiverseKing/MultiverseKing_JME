package entitysystem.render;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.CoreRenderComponent;
import entitysystem.render.CoreRenderSystem;
import hexsystem.HexMapMouseSystem;
import hexsystem.events.HexMapInputEvent;
import kingofmultiverse.MultiverseMain;
import kingofmultiverse.RTSCamera;
import tonegod.gui.core.Screen;
import hexsystem.events.HexMapRayListener;
import java.util.HashMap;
import java.util.logging.Logger;
import tonegod.gui.controls.menuing.Menu;
import utility.HexCoordinate;

/**
 * How the player can interact with element on the field. This system need the
 * EntityRenderSystem && HexMapMouseInput to work or it can't work properly
 * since it need raycast and raycast need object to be rendered on the field to
 * collide with.
 *
 * @todo Show a popup menu when an error occur and have been catch internaly.
 * @author roah
 */
public class RenderSubSystemFieldGUI extends EntitySystemAppState implements HexMapRayListener {

    /**
     * Map containing everything the player can interact with on the Field.
     */
    private HashMap<EntityId, RenderGUIComponent.EntityType> interactiveEntities = new HashMap<EntityId, RenderGUIComponent.EntityType>();
    private Node iNode = new Node("InteractiveNode");
    private Screen screen;
    private Camera cam;
    private CoreRenderSystem renderSystem = null;
    private HexMapMouseSystem hexMouseSystem = null;
    private Menu titanMenu;
    private Menu unitMenu;
    private Menu environmentMenu;
    private Vector3f inspectedSpatialPosition;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(CoreRenderSystem.class) == null
                || app.getStateManager().getState(HexMapMouseSystem.class) == null) {
            Logger.getLogger(RenderSubSystemFieldGUI.class.getName()).warning(
                    "This System need RenderSystem and HexMapMouseInputSystem to work, it is removed.");
            app.getStateManager().detach(this);
            return null;
        }
        screen = ((MultiverseMain) app).getScreen();
        screen.setUse3DSceneSupport(true);
        cam = app.getStateManager().getState(RTSCamera.class).getCamera();
        renderSystem = app.getStateManager().getState(CoreRenderSystem.class);
        hexMouseSystem = app.getStateManager().getState(HexMapMouseSystem.class);
        hexMouseSystem.registerRayInputListener(this);
        renderSystem.addSystemNode(iNode);
        initMenu();
        return entityData.getEntities(RenderGUIComponent.class, CoreRenderComponent.class);
    }

    /**
     * Initialize all needed Menu to work with.
     */
    private void initMenu() {
        titanMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                switch (index) {
                    case 0:
//                        eSystem.move(entityId, field);
                }
            }
        };
        titanMenu.addMenuItem(" Move        ", 0, null);
        titanMenu.addMenuItem(" Ability     ", 1, null);
        titanMenu.addMenuItem(" Inventory   ", 2, null);
        titanMenu.addMenuItem(" Stats       ", 3, null);
        screen.addElement(titanMenu);
        
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
    
    @Override
    protected void updateSystem(float tpf) {
//        if (interactiveNode.element.getIsVisible()) {
//            Vector3f value = cam.getScreenCoordinates(inspectedSpatialPosition);
//            if (value.x > 0 && value.x < screen.getWidth()
//                    && value.y > 0 && value.y < screen.getHeight()) {
//                interactiveNode.element.setPosition(new Vector2f(value.x, value.y));
//            }
//        }
    }

    @Override
    protected void addEntity(Entity e) {
        RenderGUIComponent.EntityType field = e.get(RenderGUIComponent.class).getEntityType();
        if(renderSystem.addSpatialToSubSystem(e.getId(), iNode.getName())){
            interactiveEntities.put(e.getId(), field);
        } else {
            entityData.removeComponent(e.getId(), RenderGUIComponent.class);
        }
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * We check if the spatial is on the screen. If not we remove the
         * Component from this system.
         */
        if (e.get(CoreRenderComponent.class) == null) {
            entityData.removeComponent(e.getId(), RenderGUIComponent.class);
            return;
        } 
        /**
         * We check if the menu associated with the spatial is the same the
         * current, if not we change it.
         */
        if (e.get(RenderGUIComponent.class).getEntityType() != interactiveEntities.get(e.getId())) {
            interactiveEntities.put(e.getId(), e.get(RenderGUIComponent.class).getEntityType());
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        renderSystem.removeSpatialFromSubSystem(e.getId());
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        for (EntityId id : interactiveEntities.keySet()) {
            HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
            if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
                openEntityActionMenu(id);
            }
        }
    }

    /*
     * Used when the spatial is not selected directly
     */ 
    public void rightMouseActionResult(HexMapInputEvent event) {
    }

    public HexMapInputEvent leftRayInputAction(Ray ray) {
        return collisionTest(ray, "L");
    }

    public HexMapInputEvent rightRayInputAction(Ray ray) {
        return collisionTest(ray, "R");
    }
    
    private HexMapInputEvent collisionTest(Ray ray, String input){
        if(interactiveEntities.isEmpty()){
            return null;
        }
        CollisionResults results = new CollisionResults();
        iNode.collideWith(ray, results);
        if (results.size() != 0 && results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();

            for (EntityId id : interactiveEntities.keySet()) {
                if (closest.getGeometry().getParent().getName().equals(renderSystem.getSpatialName(id))) {
                    hexMouseSystem.setDebugPosition(closest.getContactPoint());
                    if(input.equals("L")){
                        openEntityActionMenu(id);
                    }
                    inspectedSpatialPosition = closest.getGeometry().getWorldTranslation();
                    return new HexMapInputEvent(entityData.getComponent(id,
                            HexPositionComponent.class).getPosition(), ray);
                }
            }
        }
        return null;
    }
    
    private void openEntityActionMenu(EntityId id){
        System.out.println("Kazzammm");
    }
    
    RenderGUIComponent.EntityType inspectedEntity = null;
    
    public void setSelected(Spatial s){
//        if(s.getControl(ToneControl.class) != null){
//            EntityId id = MultiverseMain.getKeyByValue(interactiveEntities, s.getControl(ToneControl.class));
//            hexMapSystem.setCursor(entityData.getComponent(id, HexPositionComponent.class).getPosition());
//        }
    }
    
    public void move(EntityId id, RenderGUIComponent.EntityType field){
        if (!app.getStateManager().getState(HexMapMouseSystem.class).setCursorPulseMode(this)) {
            return;
        }
        inspectedEntity = field;
        screen.setUse3DSceneSupport(false);
        //Register the input for this system
        app.getStateManager().getState(HexMapMouseSystem.class).registerTileInputListener(this);
        app.getInputManager().addListener(fieldInputListener, "Cancel");
    }

    private void confirmedMove(HexCoordinate eventPosition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private ActionListener fieldInputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Cancel") && !keyPressed) {
//                castCanceled();
            }
        }
    };
    
    @Override
    protected void cleanupSystem() {
//        for(ToneControl c : interactiveEntities.values()){
//            c.remove();
//        }
//        interactiveEntities.clear();
//        screen.setUse3DSceneSupport(false);
    }
}