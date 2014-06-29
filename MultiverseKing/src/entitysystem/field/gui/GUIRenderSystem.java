package entitysystem.field.gui;

import entitysystem.utility.SubSystem;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.MoveToComponent;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.loader.TitanLoader.InitialTitanStatsComponent;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderSystem;
import hexsystem.HexMapMouseSystem;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import kingofmultiverse.MultiverseMain;
import hexsystem.events.HexMapRayListener;
import java.util.logging.Logger;
import tonegod.gui.core.Screen;
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

    private Node iNode;
    private ActionMenu actionMenu;
    private PropertiesWindow titanStatsWindow;
    private RenderSystem renderSystem = null;
    private HexMapMouseSystem hexMouseSystem = null;
    private int currentAction = -1;
    private EntityId inspectedId = null;
    private Screen screen;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(RenderSystem.class) == null
                || app.getStateManager().getState(HexMapMouseSystem.class) == null) {
            Logger.getLogger(GUIRenderSystem.class.getName()).warning(
                    "This System need RenderSystem and HexMapMouseInputSystem to work, it is removed.");
            app.getStateManager().detach(this);
            return null;
        }
        screen = ((MultiverseMain)app).getScreen();
        titanStatsWindow = new TitanStatsWindow(screen, this);
        actionMenu = new ContextualTitanMenu(screen, this, app.getCamera());
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this);
        hexMouseSystem = app.getStateManager().getState(HexMapMouseSystem.class);
        hexMouseSystem.registerRayInputListener(this);
        return entityData.getEntities(InitialTitanStatsComponent.class, RenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
        if(actionMenu != null){
            actionMenu.update();
        }
    }

    @Override
    protected void addEntity(Entity e) {
        renderSystem.addSpatialToSubSystem(e.getId(), iNode);
    }

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
        if(e.get(InitialTitanStatsComponent.class) == null){
            renderSystem.removeSpatialFromSubSystem(e.getId());
        }
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        if(currentAction != -1){
            confirmAction(event.getEventPosition());
        } else {
            Entity e = checkEntities(event.getEventPosition());  
            if(e != null){
                openEntityPropertiesMenu(e);
            }
        }
    }

    /**
     * Used when the spatial is not selected directly.
     */ 
    public void rightMouseActionResult(HexMapInputEvent event) {
        if(currentAction == -1){
            Entity e = checkEntities(event.getEventPosition());
            if(e != null){
                openEntityActionMenu(e, event.getEventPosition());
            }
        }
    }

    private Entity checkEntities(HexCoordinate coord){
        for (Entity e : entities) {
            HexPositionComponent posComp = entityData.getComponent(e.getId(), HexPositionComponent.class);
            if (posComp != null && posComp.getPosition().equals(coord)) {
                return e;
            }
        }
        return null;
    }
    
    public HexMapInputEvent leftRayInputAction(Ray ray) {
        return collisionTest(ray);
    }

    public HexMapInputEvent rightRayInputAction(Ray ray) {
        return collisionTest(ray);
    }
    
    private HexMapInputEvent collisionTest(Ray ray){
        if(entities.isEmpty()){
            return null;
        }
        CollisionResults results = new CollisionResults();
        iNode.collideWith(ray, results);
        if (results.size() != 0 && results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            for (Entity e : entities) {
                if (closest.getGeometry().getParent().getName().equals(renderSystem.getSpatialName(e.getId()))) {
                    hexMouseSystem.setDebugPosition(closest.getContactPoint());
                    HexCoordinate pos = entityData.getComponent(e.getId(), HexPositionComponent.class).getPosition();
                    return new HexMapInputEvent(pos, ray, closest);
                }
            }
        }
        return null;
    }
    
    private void openEntityActionMenu(Entity e, HexCoordinate pos){
        MapData mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        actionMenu.show(e.getId(), mapData.convertTileToWorldPosition(pos));
    }
    
    private void openEntityPropertiesMenu(Entity e){
        titanStatsWindow.show(e);
//        screen.addElement(titanStatsWindow);
        inspectedId = e.getId();
    }
    
    public void closeEntityPropertiesMenu(){
        if(currentAction == -1){
            inspectedId = null;
        }
    }
    
    public void setAction(EntityId id, Integer action){
        if (!hexMouseSystem.setCursorPulseMode(this)) {
            return;
        }
        inspectedId = id;
        currentAction = action;
        //Register the input for this system
        app.getInputManager().addListener(fieldInputListener, "Cancel");
    }

    private void confirmAction(HexCoordinate eventPosition) {
        if(currentAction == 0){ //movement action
            entityData.setComponent(inspectedId, new MoveToComponent(eventPosition));
        }
        unregisterInput();
    }
    
    private void actionCancel(){
        hexMouseSystem.setCursor(entityData.getComponent(inspectedId, HexPositionComponent.class).getPosition());
        currentAction = -1;
        unregisterInput();
    }
    
    private void unregisterInput(){
        inspectedId = null;
        currentAction = -1;
        hexMouseSystem.setCursorPulseMode(this);
        //Unregister the input for this system
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
        super.stateDetached(stateManager);
    }
    
    public void remove() {
        app.getStateManager().detach(this);
    }
}