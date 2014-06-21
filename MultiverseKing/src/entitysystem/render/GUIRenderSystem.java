package entitysystem.render;

import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import hexsystem.HexMapMouseSystem;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import kingofmultiverse.MultiverseMain;
import kingofmultiverse.RTSCamera;
import hexsystem.events.HexMapRayListener;
import java.util.HashMap;
import java.util.logging.Logger;
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
public class GUIRenderSystem extends EntitySystemAppState implements HexMapRayListener, SubSystem {

    /**
     * Map containing everything the player can interact with on the Field.
     */
    private HashMap<EntityId, GUIRenderComponent.EntityType> interactiveEntities = new HashMap<EntityId, GUIRenderComponent.EntityType>();
    private Node iNode;
    private GUIFieldMenu menus;
//    private Screen screen;
    private Camera cam;
    private RenderSystem renderSystem = null;
    private HexMapMouseSystem hexMouseSystem = null;
    private Vector3f inspectedSpatialPosition = null;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(RenderSystem.class) == null
                || app.getStateManager().getState(HexMapMouseSystem.class) == null) {
            Logger.getLogger(GUIRenderSystem.class.getName()).warning(
                    "This System need RenderSystem and HexMapMouseInputSystem to work, it is removed.");
            app.getStateManager().detach(this);
            return null;
        }
        menus = new GUIFieldMenu(((MultiverseMain) app).getScreen(), this,
                app.getStateManager().getState(RTSCamera.class).getCamera());
//        screen = ((MultiverseMain) app).getScreen();
//        screen.setUse3DSceneSupport(true);
//        cam = app.getStateManager().getState(RTSCamera.class).getCamera();
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this);
        hexMouseSystem = app.getStateManager().getState(HexMapMouseSystem.class);
        hexMouseSystem.registerRayInputListener(this);
        return entityData.getEntities(GUIRenderComponent.class, RenderComponent.class);
    }

    /**
     * Initialize all needed Menu to work with.
     */
    @Override
    protected void updateSystem(float tpf) {
        menus.update();
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
        GUIRenderComponent.EntityType field = e.get(GUIRenderComponent.class).getEntityType();
        if(renderSystem.addSpatialToSubSystem(e.getId(), iNode.getName())){
            interactiveEntities.put(e.getId(), field);
        } else {
            entityData.removeComponent(e.getId(), GUIRenderComponent.class);
        }
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * We check if the menu associated with the spatial is the same the
         * current, if not we change it.
         */
        if (e.get(GUIRenderComponent.class).getEntityType() != interactiveEntities.get(e.getId())) {
            interactiveEntities.put(e.getId(), e.get(GUIRenderComponent.class).getEntityType());
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        renderSystem.removeSpatialFromSubSystem(e.getId());
        interactiveEntities.remove(e.getId());
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        for (EntityId id : interactiveEntities.keySet()) {
            HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
            if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
                openEntityActionMenu(id, event.getEventPosition());
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
                    HexCoordinate pos = entityData.getComponent(id, HexPositionComponent.class).getPosition();
                    if(input.equals("L")){
                        openEntityActionMenu(id, pos);
                    }
                    inspectedSpatialPosition = closest.getGeometry().getWorldTranslation();
                    return new HexMapInputEvent(pos, ray, closest);
                }
            }
        }
        return null;
    }
    
    private void openEntityActionMenu(EntityId id, HexCoordinate pos){
        MapData mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        menus.show(id, interactiveEntities.get(id), mapData.convertTileToWorldPosition(pos));
    }
    
    public void move(EntityId id){
//        MeshParameter param = new MeshParameter(app.getStateManager().getState(HexSystemAppState.class).getMapData());
//        MeshManager mesh = new MeshManager();
//        mesh.getMesh()
        if (!app.getStateManager().getState(HexMapMouseSystem.class).setCursorPulseMode(this)) {
            return;
        }
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

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    public void remove() {
        app.getStateManager().detach(this);
    }
}