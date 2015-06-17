package org.multiverseking.field.battle;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.ArrayList;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.MouseRayListener;
import org.hexgridapi.utility.Rotation;
import org.multiverseking.EntityDataAppState;
import org.multiverseking.SubSystem;
import org.multiverseking.card.CardRenderComponent;
import org.multiverseking.card.attribut.CardRenderPosition;
import org.multiverseking.field.CollisionSystem;
import org.multiverseking.field.component.HealthComponent;
import org.multiverseking.field.component.InfluenceComponent;
import org.multiverseking.field.position.HexPositionComponent;
import org.multiverseking.field.position.MoveToComponent;
import org.multiverseking.field.exploration.HexMovementSystem;
import org.multiverseking.loader.EntityLoader;
import org.multiverseking.loader.PlayerLoader;
import org.multiverseking.loader.TitanLoader;
import org.multiverseking.render.AbstractRender.RenderType;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.render.animation.Animation;
import org.multiverseking.render.animation.AnimationComponent;
import org.multiverseking.render.animation.AnimationSystem;

/**
 * TODO: This do too much work should be split on multiple part as :
 * Battle Unit Control System / Battle Initialisation
 *
 * @author roah
 * @deprecated need to be rework
 */
public class BattleSystem extends AbstractAppState implements MouseRayListener, SubSystem {

    private final HexCoordinate startPosition;
    private SimpleApplication app;
    private EntityData entityData;
    private MapData mapData;
    private RenderSystem renderSystem;
    private MouseControlSystem mouseSystem;
    private Action currentAction = null;
    private EntityId inspectedId = null;
    private EntityId playerCore;

    public BattleSystem(HexCoordinate startPosition) {
        this.startPosition = startPosition;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = (SimpleApplication) app;
        entityData = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
        mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();
//        debugSystem = app.getStateManager().getState(AreaEventRenderDebugSystem.class);
//        if (mapData.getAllChunkPos().isEmpty()) {
//            mapData.addChunk(new Vector2Int(), null);
//        }
        if(app.getStateManager().getState(CollisionSystem.class) == null){
            app.getStateManager().attach(new CollisionSystem());
        }
        if(app.getStateManager().getState(AnimationSystem.class) == null){
            app.getStateManager().attach(new AnimationSystem());
        }
        if(app.getStateManager().getState(HexMovementSystem.class) == null){
            app.getStateManager().attach(new HexMovementSystem());
        }

//        if (app.getStateManager().getState(RenderSystem.class) == null) {
//            app.getStateManager().attach(new RenderSystem());
////            throw new UnsatisfiedLinkError("This System need RenderSystem and AreaMouseInputSystem to work.");
//        }
//        battleGUI = new BattleGUI(((MultiverseMain) app).getScreen(), app.getCamera(), this);
        renderSystem = app.getStateManager().getState(RenderSystem.class);
//        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this, true);
        mouseSystem = app.getStateManager().getState(MouseControlSystem.class);
        mouseSystem.registerRayInputListener(this);

        initialisePlayerCore();

        /**
         * Init the testingUnit.
         */
//        addEntityTitan("TuxDoll");
//        addEntityTitan("Gilga");
//        camToStartPosition();
        super.initialized = true;
    }

    private void initialisePlayerCore() {
//        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
//        if (debugSystem != null) {
//            debugSystem.showDebug(false, startPosition, this);
//        }

        PlayerLoader properties = PlayerLoader.getInstance(app.getAssetManager());
        playerCore = entityData.createEntity();
        entityData.setComponents(playerCore,
                new RenderComponent("Well", RenderType.Core),
                new HexPositionComponent(startPosition, Rotation.A, this),
                new AnimationComponent(Animation.SUMMON),
                new HealthComponent(properties.getLevel() * 10),
                new InfluenceComponent((byte) (1 + FastMath.floor((float) properties.getLevel() / 25f))));
    }

    private void removePlayerCore() {
//        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
//        if (debugSystem != null) {
//            debugSystem.showDebug(true, startPosition, this);
//        }
        entityData.removeEntity(playerCore);
    }
    
    /**
     * Move the camera to the center of the map.
     */
//    private void camToStartPosition() {
//        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
//        Vector3f center = startPosition.toWorldPosition();
////        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
////                new Vector2Int(HexSetting.CHUNK_SIZE / 2, HexSetting.CHUNK_SIZE / 2)).toWorldPosition();
//        ((HexGridEditorMain) app).getRtsCam().setCenter(new Vector3f(center.x + 3, 15, center.z + 3));
//    }
    
    public void addEntityTitan(String name, HexCoordinate position) {
        EntityLoader loader = new EntityLoader(app);
        TitanLoader load = loader.loadTitanStats(name);
//        HexCoordinate position = new HexCoordinate(HexCoordinate.OFFSET,HexSetting.CHUNK_SIZE / 2, HexSetting.CHUNK_SIZE / 2);
        entityData.setComponents(entityData.createEntity(),
                new CardRenderComponent(name, CardRenderPosition.FIELD, RenderType.Titan),
                new RenderComponent(name, RenderType.Titan),
                new HexPositionComponent(position, Rotation.C),
                new AnimationComponent(Animation.SUMMON),
                load.getInitialStatsComponent(),
                load.getInitialStatsComponent().getMovementComponent());
    }
    
    @Override
    public MouseInputEvent MouseRayInputAction(MouseInputEventType mouseInputType, Ray ray) {
        EntitySet entities = entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
        if (entities.isEmpty()) {
            return null;
        }
        if (mouseInputType.equals(MouseInputEventType.RMB)) {
            CollisionResults results = renderSystem.subSystemCollideWith(this, ray);
            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();
                for (Entity e : entities) {
                    Spatial s = closest.getGeometry().getParent();
                    do {
                        if (s != null && s.getName().equals(renderSystem.getSpatialName(e.getId()))) {
                            //                        mouseSystem.setDebugPosition(closest.getContactPoint());
                            HexCoordinate pos = entityData.getComponent(e.getId(), HexPositionComponent.class).getPosition();
                            return new MouseInputEvent(MouseInputEventType.RMB, pos, mapData.getTile(pos).getHeight(), ray, closest);
                        }
                        s = s.getParent();
                    } while (s != null && !s.getParent().getName().equals(renderSystem.getRenderNodeName()));
                }
            }
        }
        return null;
    }

    @Override
    public void onMouseAction(MouseInputEvent event) {
        if (event.getType().equals(MouseInputEventType.LMB)) {
            if (currentAction != null) {
                confirmAction(event.getPosition());
            } else {
                Entity e = checkEntities(event.getPosition());
                if (e != null) {
                    openEntityPropertiesMenu(e);
                }
            }
        } else if (event.getType().equals(MouseInputEventType.RMB)) {
            // Used when the spatial is not selected directly.
            if (currentAction == null) {
                Entity e = checkEntities(event.getPosition());
                if (e != null && entityData.getComponent(e.getId(), MoveToComponent.class) == null) {
                    openEntityActionMenu(e, event.getPosition());
                }
            }
        }
    }

    private Entity checkEntities(HexCoordinate coord) {
        EntitySet entities = entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
        for (Entity e : entities) {
            if (!e.get(RenderComponent.class).getRenderType().equals(RenderType.Debug)) {
                HexPositionComponent posComp = entityData.getComponent(e.getId(), HexPositionComponent.class);
                if (posComp != null && posComp.getPosition().equals(coord)) {
                    return e;
                }
            }
        }
        return null;
    }

    private void openEntityActionMenu(Entity e, HexCoordinate pos) {
//        MapData mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
//        battleGUI.showActionMenu(pos, e.getId(), e.get(RenderComponent.class).getRenderType());
    }

    private void openEntityPropertiesMenu(Entity e) {
        if (inspectedId == null || !inspectedId.equals(e.getId())) {
            ArrayList<EntityComponent> comps = new ArrayList<>();
            RenderType renderType = e.get(RenderComponent.class).getRenderType();
            switch (renderType) {
                case Ability:
                    break;
                case Core:
                    comps.add(entityData.getComponent(e.getId(), HealthComponent.class));
                    comps.add(entityData.getComponent(e.getId(), InfluenceComponent.class));
//                    app.getStateManager().getState(AbstractHexGridAppState.class).showAreaRange(
//                            e.get(HexPositionComponent.class).getPosition(), ((InfluenceComponent) comps.get(1)).getRange(), ColorRGBA.Red);
                    break;
                case Debug:
                    break;
                case Environment:
                    break;
                case Equipement:
                    break;
                case Titan:
                    break;
                case Unit:
                    break;
                default:
                    throw new UnsupportedOperationException(e.get(RenderComponent.class).getRenderType() + " is not currently supported.");
            }
//            battleGUI.statsWindow(e.getId(), renderType, comps);
            inspectedId = e.getId();
        }
    }

    void setAction(EntityId id, Action action) {
        switch (action) {
            case MOVE:
                if (!mouseSystem.setCursorPulseMode(this)) {
                    return;
                }
                inspectedId = id;
                currentAction = action;
                //Register the input for this system
                app.getInputManager().addListener(fieldInputListener, "Cancel");
                break;
            case ABILITY:
                break;
            case CUSTOM:
                /**
                 * TODO : For action made by external user, Data have to be
                 * loaded from files.
                 */
                break;
            default:
                throw new UnsupportedOperationException("Action type not implemented.");
        }
    }

    private void confirmAction(HexCoordinate eventPosition) {
        if (currentAction.equals(Action.MOVE)) { //movement action
            entityData.setComponent(inspectedId, new MoveToComponent(eventPosition));
        }
        unregisterInput();
    }

    private void actionCancel() {
//        mouseSystem.setCursor(entityData.getComponent(inspectedId, HexPositionComponent.class).getPosition());
        currentAction = null;
        unregisterInput();
    }

    private void unregisterInput() {
        inspectedId = null;
        currentAction = null;
        mouseSystem.setCursorPulseMode(this);
        //Unregister the input for this system
        app.getInputManager().removeListener(fieldInputListener);
    }
    private ActionListener fieldInputListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Cancel") && !keyPressed) {
                actionCancel();
            }
        }
    };

    public void reloadSystem() {
//        mapData.Cleanup();
//        mapData.addChunk(new Vector2Int(), null);
//        camToStartPosition();
    }

    @Override
    public void cleanup() {
//        battleGUI.removeFromScreen();
        removePlayerCore();
        mapData.Cleanup();
        renderSystem.removeSubSystem(this, false);
        app.getStateManager().detach(app.getStateManager().getState(CollisionSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(AnimationSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(HexMovementSystem.class));
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this);
    }

    public enum Action {

        MOVE,
        ABILITY,
        CUSTOM;
    }
}