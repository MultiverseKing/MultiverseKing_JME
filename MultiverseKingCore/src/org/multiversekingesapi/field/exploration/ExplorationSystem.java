package org.multiversekingesapi.field.exploration;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.HexGridDefaultApp;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.utility.HexCoordinate;
import org.multiversekingesapi.EntityDataAppState;
import org.multiversekingesapi.SubSystem;
import org.multiversekingesapi.field.AreaEventSystem;
import org.multiversekingesapi.field.CollisionSystem;
import org.multiversekingesapi.field.position.HexPositionComponent;
import org.multiversekingesapi.field.position.MoveToComponent;
import org.multiversekingesapi.field.position.HexMovementSystem;
import org.multiversekingesapi.loader.PlayerProperties;
import org.multiversekingesapi.render.AreaEventRenderDebugSystem;
import org.multiversekingesapi.render.AbstractRender;
import org.multiversekingesapi.render.RenderComponent;
import org.multiversekingesapi.render.RenderSystem;
import org.multiversekingesapi.render.animation.Animation;
import org.multiversekingesapi.render.animation.AnimationComponent;
import org.multiversekingesapi.render.animation.AnimationSystem;
import org.multiversekingesapi.render.camera.CameraControlSystem;
import org.multiversekingesapi.render.camera.CameraTrackComponent;

/**
 *
 * @author roah
 */
public class ExplorationSystem extends AbstractAppState implements SubSystem {
    
    private SimpleApplication app;
    private EntityData entityData;
    private MapData mapData;
    private MouseControlSystem mouseSystem;
    private EntityId playerId;
    private AreaEventRenderDebugSystem renderDebugSystem;

    public ExplorationSystem(){
    }
    
    public ExplorationSystem(EntityId playerID) {
        this.playerId = playerID;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.entityData = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
        this.mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();
        this.mouseSystem = app.getStateManager().getState(MouseControlSystem.class);
        mouseSystem.registerTileInputListener(tileInputListener);
        this.renderDebugSystem = app.getStateManager().getState(AreaEventRenderDebugSystem.class);
        
        /**
         * Initialise all other system needed.
         */
        if(app.getStateManager().getState(CollisionSystem.class) == null){
            app.getStateManager().attach(new CollisionSystem());
        }
        if(app.getStateManager().getState(AnimationSystem.class) == null){
            app.getStateManager().attach(new AnimationSystem());
        }
        if(app.getStateManager().getState(HexMovementSystem.class) == null){
            app.getStateManager().attach(new HexMovementSystem());
        }
        if(app.getStateManager().getState(CameraControlSystem.class) == null){
            app.getStateManager().attach(new CameraControlSystem(((HexGridDefaultApp)app).getRtsCam()));
        }
        
        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
        if (renderDebugSystem != null) {
            renderDebugSystem.showDebug(false, startPosition, this);
        }
        /**
         * Load the titan controlled by the player outside of battle to move arround.
         */
        if(playerId == null){
            playerId = entityData.createEntity();
        }
        String name = PlayerProperties.getInstance(app.getAssetManager()).getBlessedTitan();
//        float ms = new EntityLoader((SimpleApplication) app).loadTitanStats(name)
//                .getInitialStatsComponent().getMoveSpeed();
        entityData.setComponents(playerId, new RenderComponent(
                name, AbstractRender.RenderType.Titan), 
                new HexPositionComponent(startPosition),
                new AnimationComponent(Animation.IDLE),
                new CameraTrackComponent());
//                new MovementComponent(Byte.MAX_VALUE, ms));
//        ((HexGridDefaultApp)app).getRtsCam().setCenter(startPosition.convertToWorldPosition());
//        ((HexGridDefaultApp)app).getRtsCam().setTracker(app.getStateManager().getState(RenderSystem.class).getSpatial(playerId));
    }

    @Override
    public void rootSystemIsRemoved() {
    }

    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            if(event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)){
                entityData.setComponent(playerId, new MoveToComponent(event.getPosition()));
            }
        }
    };
}
