package entitysystem.render;

import entitysystem.utility.SubSystem;
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
import entitysystem.field.position.MoveToComponent;
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
//    private Camera cam;
    private RenderSystem renderSystem = null;
    private HexMapMouseSystem hexMouseSystem = null;
//    private Vector3f inspectedSpatialPosition = null;
    private EntityId inspectedId = null;
    private String currentAction = null;

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
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this);
        hexMouseSystem = app.getStateManager().getState(HexMapMouseSystem.class);
        hexMouseSystem.registerRayInputListener(this);
        return entityData.getEntities(GUIRenderComponent.class, RenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
        menus.update();
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
        if(currentAction != null){
            confirmAction(event.getEventPosition());
        }
    }

    /*
     * Used when the spatial is not selected directly
     */ 
    public void rightMouseActionResult(HexMapInputEvent event) {
        if(currentAction == null){
            for (EntityId id : interactiveEntities.keySet()) {
                HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
                if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
                    openEntityActionMenu(id, event.getEventPosition());
                }
            }
        }
    }

    public HexMapInputEvent leftRayInputAction(Ray ray) {
//        return collisionTest(ray, "L");
        return null;
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
                    if(input.equals("R")){
                        openEntityActionMenu(id, pos);
                    }
//                    inspectedSpatialPosition = closest.getGeometry().getWorldTranslation();
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
    
    public void setAction(EntityId id, String action){
//        MeshParameter param = new MeshParameter(app.getStateManager().getState(HexSystemAppState.class).getMapData());
//        MeshManager mesh = new MeshManager();
//        mesh.getMesh()
        if (!hexMouseSystem.setCursorPulseMode(this)) {
            return;
        }
        inspectedId = id;
        currentAction = action;
        //Register the input for this system
//        hexMouseSystem.registerTileInputListener(this);
        app.getInputManager().addListener(fieldInputListener, "Cancel");
    }

    private void confirmAction(HexCoordinate eventPosition) {
        if(currentAction.equals("Move")){
            entityData.setComponent(inspectedId, new MoveToComponent(eventPosition));
        }
        unregisterInput();
    }
    
    private void actionCancel(){
        hexMouseSystem.setCursor(entityData.getComponent(inspectedId, HexPositionComponent.class).getPosition());
        currentAction = null;
        unregisterInput();
    }
    
    private void unregisterInput(){
        inspectedId = null;
        currentAction = null;
        hexMouseSystem.setCursorPulseMode(this);
        //Unregister the input for this system
//        hexMouseSystem.removeTileInputListener(this);
        app.getInputManager().removeListener(fieldInputListener);
    }
    
    private ActionListener fieldInputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Cancel") && !keyPressed) {
                actionCancel();
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