package gamemode.battle;

import gamemode.gui.PropertiesWindow;
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
public class BattleGUISystem extends EntitySystemAppState implements HexMapRayListener, SubSystem {

    private RenderSystem renderSystem;
    private HexMapMouseSystem hexMapMouseSystem;
    private Node iNode;
    private ContextualMenu actionMenu;
    private PropertiesWindow titanStatsWindow;
    private int currentAction = -1;
    private EntityId inspectedId = null;
    private Screen screen;
    
    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(RenderSystem.class) == null
                || app.getStateManager().getState(HexMapMouseSystem.class) == null) {
            Logger.getLogger(BattleGUISystem.class.getName()).warning(
                    "This System need RenderSystem and HexMapMouseInputSystem to work, it is removed.");
            app.getStateManager().detach(this);
            return null;
        }
        screen = ((MultiverseMain)app).getScreen();
        titanStatsWindow = new TitanStatsWindow(screen, this);
        actionMenu = new TitanMenu(screen, this, app.getCamera());
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this);
        hexMapMouseSystem = app.getStateManager().getState(HexMapMouseSystem.class);
        hexMapMouseSystem.registerRayInputListener(this);
        
        return entityData.getEntities(InitialTitanStatsComponent.class, RenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
        if(actionMenu != null){
            actionMenu.update(tpf);
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
        } 
//        else {
//            Entity e = checkEntities(event.getEventPosition());  
//            if(e != null){
//                openEntityPropertiesMenu(e);
//            }
//        }
    }

    /**
     * Used when the spatial is not selected directly.
     */ 
    public void rightMouseActionResult(HexMapInputEvent event) {
        if(currentAction == -1){
            Entity e = checkEntities(event.getEventPosition());
            if(e != null && entityData.getComponent(e.getId(), MoveToComponent.class) == null){
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
                    hexMapMouseSystem.setDebugPosition(closest.getContactPoint());
                    HexCoordinate pos = entityData.getComponent(e.getId(), HexPositionComponent.class).getPosition();
                    return new HexMapInputEvent(pos, ray, closest);
                }
            }
        }
        return null;
    }
    
    private void openEntityActionMenu(Entity e, HexCoordinate pos){
//        MapData mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        actionMenu.show(pos, e.getId());
    }
    
    public void closeEntityPropertiesMenu(){
        if(currentAction == -1){
            inspectedId = null;
        }
    }
    
    void setAction(EntityId id, Integer action){
        switch(action){
            case 0://Movement action
            if (!hexMapMouseSystem.setCursorPulseMode(this)) {
                return;
            }
            inspectedId = id;
            currentAction = action;
            //Register the input for this system
            app.getInputManager().addListener(fieldInputListener, "Cancel");
                break;
            case 1: //Ability
                break;
            case 2: //Stats
                titanStatsWindow.show(entities.getEntity(id));
                inspectedId = id;
                break;
            default:
                throw new UnsupportedOperationException("Action type not implemented.");
        } 
    }

    private void confirmAction(HexCoordinate eventPosition) {
        if(currentAction == 0){ //movement action
            entityData.setComponent(inspectedId, new MoveToComponent(eventPosition));
        }
        unregisterInput();
    }
    
    private void actionCancel(){
        hexMapMouseSystem.setCursor(entityData.getComponent(inspectedId, HexPositionComponent.class).getPosition());
        currentAction = -1;
        unregisterInput();
    }
    
    private void unregisterInput(){
        inspectedId = null;
        currentAction = -1;
        hexMapMouseSystem.setCursorPulseMode(this);
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