package hexsystem.battle;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import editor.area.AreaEventRenderDebugSystem;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardRenderComponent;
import entitysystem.field.CollisionSystem;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.field.position.MoveToComponent;
import entitysystem.field.position.MovementSystem;
import entitysystem.render.AnimationComponent;
import entitysystem.loader.EntityLoader;
import entitysystem.loader.TitanLoader;
import entitysystem.render.AnimationSystem;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderSystem;
import entitysystem.SubSystem;
import entitysystem.render.RenderComponent.Type;
import hexsystem.area.AreaEventSystem;
import hexsystem.area.MapDataAppState;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.base.AreaMouseAppState;
import org.hexgridapi.base.MapData;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseRayListener;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Rotation;

/**
 * TODO: This should extends from game Battle system.
 * @author roah
 */
public class BattleTrainingSystem extends EntitySystemAppState implements MouseRayListener, SubSystem {

    private MapData mapData;
    private RenderSystem renderSystem;
    private AreaMouseAppState hexMapMouseSystem;
    private Action currentAction = null;
    private EntityId inspectedId = null;
    private BattleTrainingGUI bTrainingGUI;
    private AreaEventRenderDebugSystem debugSystem;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();
        debugSystem = app.getStateManager().getState(AreaEventRenderDebugSystem.class);
//        if (mapData.getAllChunkPos().isEmpty()) {
//            mapData.addChunk(new Vector2Int(), null);
//        }
        app.getStateManager().attachAll(new CollisionSystem(), new AnimationSystem(), new MovementSystem());

//        if (app.getStateManager().getState(RenderSystem.class) == null) {
//            app.getStateManager().attach(new RenderSystem());
////            throw new UnsatisfiedLinkError("This System need RenderSystem and AreaMouseInputSystem to work.");
//        }
        bTrainingGUI = new BattleTrainingGUI(((MultiverseMain) app).getScreen(), app.getCamera(), this);
        renderSystem = app.getStateManager().getState(RenderSystem.class);
//        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this, true);
        hexMapMouseSystem = app.getStateManager().getState(AreaMouseAppState.class);
        hexMapMouseSystem.registerRayInputListener(this);
        
        initialisePlayerCore(app.getStateManager().getState(AreaEventSystem.class).getStartPosition());

        /**
         * Init the testingUnit.
         */
//        addEntityTitan("TuxDoll");
//        addEntityTitan("Gilga");
//        camToStartPosition();

        return entityData.getEntities(HexPositionComponent.class, RenderComponent.class);
    }

    @Override
    public String getSubSystemName() {
        return "BattleSystem";
    }

    private void initialisePlayerCore(HexCoordinate startPosition) {
        if(debugSystem != null){
            debugSystem.hideDebug(startPosition, this);
        }
        EntityId e = entityData.createEntity();
        entityData.setComponents(e, new HexPositionComponent(startPosition), new RenderComponent("Well", Type.Core, this), new AnimationComponent(Animation.SUMMON));
    }

    /**
     * Move the camera to the center of the map.
     */
    private void camToStartPosition() {
        HexCoordinate startPosition = app.getStateManager().getState(AreaEventSystem.class).getStartPosition();
        Vector3f center = startPosition.convertToWorldPosition();
//        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
//                new Vector2Int(HexSetting.CHUNK_SIZE / 2, HexSetting.CHUNK_SIZE / 2)).convertToWorldPosition();
        ((MultiverseMain) app).getRtsCam().setCenter(new Vector3f(center.x + 3, 15, center.z + 3));
    }

    @Override
    protected void updateSystem(float tpf) {
        bTrainingGUI.update(tpf);
    }

    @Override
    protected void addEntity(Entity e) {
    }

    public void addEntityTitan(String name, HexCoordinate position) {
        EntityLoader loader = new EntityLoader();
        TitanLoader load = loader.loadTitanStats(name);
//        HexCoordinate position = new HexCoordinate(HexCoordinate.OFFSET,HexSetting.CHUNK_SIZE / 2, HexSetting.CHUNK_SIZE / 2);
        entityData.setComponents(entityData.createEntity(),
                new CardRenderComponent(CardRenderPosition.FIELD, name),
                new RenderComponent(name, Type.Titan),
                new HexPositionComponent(position, Rotation.C),
                new AnimationComponent(Animation.SUMMON),
                new TimeBreakComponent(),
                load.getInitialStatsComponent(),
                load.getInitialStatsComponent().getMovementComponent());
    }

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    @Override
    public void leftMouseActionResult(MouseInputEvent event) {
        if (currentAction != null) {
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
    @Override
    public void rightMouseActionResult(MouseInputEvent event) {
        if (currentAction == null) {
            Entity e = checkEntities(event.getEventPosition());
            if (e != null && entityData.getComponent(e.getId(), MoveToComponent.class) == null) {
                openEntityActionMenu(e, event.getEventPosition());
            }
        }
    }

    private Entity checkEntities(HexCoordinate coord) {
        for (Entity e : entities) {
            if(!e.get(RenderComponent.class).getType().equals(Type.Debug)){
                HexPositionComponent posComp = entityData.getComponent(e.getId(), HexPositionComponent.class);
                if (posComp != null && posComp.getPosition().equals(coord)) {
                    return e;
                }
            }
        }
        return null;
    }

    @Override
    public MouseInputEvent leftRayInputAction(Ray ray) {
        return collisionTest(ray);
    }

    @Override
    public MouseInputEvent rightRayInputAction(Ray ray) {
        return collisionTest(ray);
    }

    private MouseInputEvent collisionTest(Ray ray) {
        if (entities.isEmpty()) {
            return null;
        }
        CollisionResults results = renderSystem.subSystemCollideWith(this, ray);
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            for (Entity e : entities) {
                Spatial s = closest.getGeometry().getParent();
                do {
                    if (s != null && s.getName().equals(renderSystem.getSpatialName(e.getId()))) {
                        hexMapMouseSystem.setDebugPosition(closest.getContactPoint());
                        HexCoordinate pos = entityData.getComponent(e.getId(), HexPositionComponent.class).getPosition();
                        return new MouseInputEvent(pos, ray, closest);
                    }
                    s = s.getParent();
                } while (s != null && !s.getParent().getName().equals(renderSystem.getRenderNodeName()));
            }
        }
        return null;
    }

    private void openEntityActionMenu(Entity e, HexCoordinate pos) {
//        MapData mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        bTrainingGUI.showActionMenu(pos, e.getId(), e.get(RenderComponent.class).getType());
    }

    public void closeEntityPropertiesMenu() {
        if (currentAction == null) {
            inspectedId = null;
        }
    }

    void setAction(EntityId id, Action action) {
        switch (action) {
            case MOVE:
                if (!hexMapMouseSystem.setCursorPulseMode(this)) {
                    return;
                }
                inspectedId = id;
                currentAction = action;
                //Register the input for this system
                app.getInputManager().addListener(fieldInputListener, "Cancel");
                break;
            case ABILITY:
                break;
            case STATS:
                bTrainingGUI.showTitanStats(entities.getEntity(id));
                inspectedId = id;
                break;
            case CUSTOM:
                /**
                 * TODO : For action made by external user, Data have to be loaded.
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
        hexMapMouseSystem.setCursor(entityData.getComponent(inspectedId, HexPositionComponent.class).getPosition());
        currentAction = null;
        unregisterInput();
    }

    private void unregisterInput() {
        inspectedId = null;
        currentAction = null;
        hexMapMouseSystem.setCursorPulseMode(this);
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
    protected void cleanupSystem() {
        bTrainingGUI.removeFromScreen();
        mapData.Cleanup();
        renderSystem.removeSubSystem(this, false);
        app.getStateManager().detach(app.getStateManager().getState(CollisionSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(AnimationSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(MovementSystem.class));
    }

    @Override
    public void removeSubSystem() {
        app.getStateManager().detach(this);
    }
    
    public enum Action {
        MOVE,
        STATS,
        ABILITY,
        CUSTOM;
    }
}
