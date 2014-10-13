package gamemode.battle;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
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
import entitysystem.loader.TitanLoader.InitialTitanStatsComponent;
import entitysystem.render.AnimationSystem;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderSystem;
import entitysystem.utility.SubSystem;
import hexsystem.AreaMouseInputSystem;
import hexsystem.HexSettings;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapRayListener;
import java.util.ArrayList;
import java.util.logging.Logger;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class BattleSystem extends EntitySystemAppState implements HexMapRayListener, SubSystem {

    private MapData mapData;
    private ArrayList<EntityId> titanList = new ArrayList<EntityId>();
    private ArrayList<EntityId> unitList = new ArrayList<EntityId>();
    private RenderSystem renderSystem;
    private AreaMouseInputSystem hexMapMouseSystem;
    private Node iNode;
    private int currentAction = -1;
    private EntityId inspectedId = null;
    private BattleTrainingGUI bTrainingGUI;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(new Vector2Int(), null);
        }
        app.getStateManager().attachAll(new CollisionSystem(), new AnimationSystem(), new MovementSystem());

        /**
         * Register battle input.
         */
//        registerInput();
        if (app.getStateManager().getState(RenderSystem.class) == null
                || app.getStateManager().getState(AreaMouseInputSystem.class) == null) {
            app.getStateManager().attach(new RenderSystem());
            app.getStateManager().attach(new AreaMouseInputSystem());
            Logger.getLogger(BattleSystem.class.getName()).warning(
                    "This System need RenderSystem and AreaMouseInputSystem to work, they got added.");
        }
        bTrainingGUI = new BattleTrainingGUI(((MultiverseMain) app).getScreen(), app.getCamera(), this);
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        iNode = renderSystem.addSubSystemNode("InteractiveNode");
        renderSystem.registerSubSystem(this);
        hexMapMouseSystem = app.getStateManager().getState(AreaMouseInputSystem.class);
        hexMapMouseSystem.registerRayInputListener(this);

        /**
         * Init the testingUnit.
         */
//        addEntityTitan("TuxDoll");
//        addEntityTitan("Gilga");
        camToCenter();

        return entityData.getEntities(InitialTitanStatsComponent.class, RenderComponent.class);
    }

    /**
     * Move the camera to the center of the map.
     */
    private void camToCenter() {
        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(HexSettings.CHUNK_SIZE / 2, HexSettings.CHUNK_SIZE / 2)).convertToWorldPosition();
        ((MultiverseMain) app).getRtsCam().setCenter(new Vector3f(center.x + 3, 15, center.z + 3));
    }

    @Override
    protected void updateSystem(float tpf) {
        bTrainingGUI.update(tpf);
    }

    @Override
    protected void addEntity(Entity e) {
        unitList.add(e.getId());
        renderSystem.addSpatialToSubSystem(e.getId(), iNode);
    }

    private void addEntityTitan(String name) {
        titanList.add(entityData.createEntity());
        EntityLoader loader = new EntityLoader();
        TitanLoader load = loader.loadTitanStats(name);
        entityData.setComponents(titanList.get(0),
                new CardRenderComponent(CardRenderPosition.FIELD, name),
                new RenderComponent(name),
                new HexPositionComponent(new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(HexSettings.CHUNK_SIZE / 2, HexSettings.CHUNK_SIZE / 2)), Rotation.C),
                new AnimationComponent(Animation.SUMMON),
                new TimeBreakComponent(),
                load.getInitialStatsComponent(),
                load.getInitialStatsComponent().getMovementComponent());
    }

    public void reloadSystem() {
        mapData.Cleanup();
        for (EntityId id : titanList) {
            entityData.removeEntity(id);
        }
        for (EntityId id : unitList) {
            entityData.removeEntity(id);
        }
        mapData.addChunk(new Vector2Int(), null);
        camToCenter();
    }

    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        if (unitList.contains(e.getId())) {
            unitList.remove(e.getId());
        } else if (titanList.contains(e.getId())) {
            titanList.remove(e.getId());
        }
        if (e.get(InitialTitanStatsComponent.class) == null) {
            renderSystem.removeSpatialFromSubSystem(e.getId());
        }
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
        if (currentAction != -1) {
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
        if (currentAction == -1) {
            Entity e = checkEntities(event.getEventPosition());
            if (e != null && entityData.getComponent(e.getId(), MoveToComponent.class) == null) {
                openEntityActionMenu(e, event.getEventPosition());
            }
        }
    }

    private Entity checkEntities(HexCoordinate coord) {
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

    private HexMapInputEvent collisionTest(Ray ray) {
        if (entities.isEmpty()) {
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

    private void openEntityActionMenu(Entity e, HexCoordinate pos) {
//        MapData mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        bTrainingGUI.showActionMenu(pos, e.getId());
    }

    public void closeEntityPropertiesMenu() {
        if (currentAction == -1) {
            inspectedId = null;
        }
    }

    void setAction(EntityId id, Integer action) {
        switch (action) {
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
                bTrainingGUI.showTitanStats(entities.getEntity(id));
                inspectedId = id;
                break;
            default:
                throw new UnsupportedOperationException("Action type not implemented.");
        }
    }

    private void confirmAction(HexCoordinate eventPosition) {
        if (currentAction == 0) { //movement action
            entityData.setComponent(inspectedId, new MoveToComponent(eventPosition));
        }
        unregisterInput();
    }

    private void actionCancel() {
        hexMapMouseSystem.setCursor(entityData.getComponent(inspectedId, HexPositionComponent.class).getPosition());
        currentAction = -1;
        unregisterInput();
    }

    private void unregisterInput() {
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
        titanList.clear();
        unitList.clear();
    }

    public void remove() {
        app.getStateManager().detach(this);
    }
}
