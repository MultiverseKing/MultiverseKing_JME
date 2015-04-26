package org.multiversekingesapi.field.exploration;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.multiversekingesapi.loader.PlayerProperties;
import org.multiversekingesapi.render.AreaEventRenderDebugSystem;
import org.multiversekingesapi.render.AbstractRender;
import org.multiversekingesapi.render.RenderComponent;
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

    public ExplorationSystem() {
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
        setUsedState(true);
        
        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
        if (renderDebugSystem != null) {
            renderDebugSystem.showDebug(false, startPosition, this);
        }
        /**
         * Load the titan controlled by the player outside of battle to move
         * arround.
         */
        if (playerId == null) {
            playerId = entityData.createEntity();
        }
        String name = PlayerProperties.getInstance(app.getAssetManager()).getBlessedTitan();
        entityData.setComponents(playerId, new RenderComponent(
                name, AbstractRender.RenderType.Titan),
                new HexPositionComponent(startPosition),
                new AnimationComponent(Animation.IDLE),
                new CameraTrackComponent());
    }

    @Override
    public void rootSystemIsRemoved() {
    }
    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            if (event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
                entityData.setComponent(playerId, new MoveToComponent(event.getPosition()));
            }
        }
    };

    @Override
    public void cleanup() {
        super.cleanup();

        setUsedState(false);
        
        entityData.removeEntity(playerId);
        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
        if (renderDebugSystem != null) {
            renderDebugSystem.showDebug(true, startPosition, this);
        }
        ((HexGridDefaultApp) app).getRtsCam().setCenter(
                startPosition.toWorldPosition(mapData.getTile(startPosition).getHeight()));

    }

    private void setUsedState(boolean enable) {
        AppState state;
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                state = getState(enable, CollisionSystem.class);
            } else if (i == 1) {
                state = getState(enable, AnimationSystem.class);
            } else if (i == 2) {
                state = getState(enable, HexMovementSystem.class);
            } else {
                state = getState(enable, CameraControlSystem.class);
            }

            if (enable && state != null) {
                app.getStateManager().attach(state);
            } else if (!enable && state != null) {
                app.getStateManager().detach(state);
            }
        }

    }

    private <T extends AppState>AppState getState(boolean enable, Class<? extends AppState> classType) {
        AppState state = app.getStateManager().getState(classType);
        if (enable && state == null) {
            try {
                return classType.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ExplorationSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(!enable && state != null){
            return state;
        }
        return null;
    }
}
