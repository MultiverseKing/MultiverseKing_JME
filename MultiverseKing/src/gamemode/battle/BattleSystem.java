package gamemode.battle;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardRenderComponent;
import entitysystem.field.CollisionSystem;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.field.position.MovementSystem;
import entitysystem.render.AnimationComponent;
import entitysystem.loader.EntityLoader;
import entitysystem.loader.TitanLoader;
import entitysystem.render.AnimationSystem;
import entitysystem.render.RenderComponent;
import hexsystem.HexSettings;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import java.util.ArrayList;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class BattleSystem extends EntitySystemAppState {

    private MapData mapData;
    private ArrayList<EntityId> titanList = new ArrayList<EntityId>();
    private ArrayList<EntityId> unitList = new ArrayList<EntityId>();

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        /**
         * Load the map and populate the Menu.
         */
//        if (mapData.getMapName() == null || !mapData.getMapName().equalsIgnoreCase("reset")) {
//            if (!mapData.loadMap("Reset")) {
//                Logger.getLogger(BattleSystem.class.getName()).log(Level.WARNING, null, new IOException("Files missing."));
//                app.getStateManager().detach(this);
//                return null;
//            }
//        }
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(Vector2Int.ZERO, null);
        }
        app.getStateManager().attachAll(new BattleGUISystem(), new CollisionSystem(), new AnimationSystem(), new MovementSystem());
        /**
         * Init the testingUnit.
         */
//        addEntityTitan("TuxDoll");
        addEntityTitan("Gilga");

        /**
         * Move the camera to the center of the map.
         */
        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(HexSettings.CHUNK_SIZE / 2, HexSettings.CHUNK_SIZE / 2)).convertToWorldPosition();
        ((MultiverseMain) app).getRtsCam().setCenter(new Vector3f(center.x + 3, 15, center.z + 3));


        /**
         * Register battle input.
         */
//        registerInput();
        //*
        return entityData.getEntities(RenderComponent.class);
    }

    private void registerInput() {
        //Register the input for this system
        app.getInputManager().addMapping("TimeBreak", new KeyTrigger(KeyInput.KEY_TAB));
        app.getInputManager().addListener(battleInputListener, "TimeBreak");
    }
    private ActionListener battleInputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("TimeBreak") && !keyPressed) {
                timeBreak();
            }
        }
    };

    private void timeBreak() {
        System.out.println("Break!");
        for (EntityId id : titanList) {
            entityData.setComponent(id, new TimeBreakComponent(true));
        }
        for (EntityId id : unitList) {
            entityData.setComponent(id, new TimeBreakComponent(true));
        }
    }

    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addEntity(Entity e) {
        unitList.add(e.getId());
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
    }

    @Override
    protected void cleanupSystem() {
        titanList.clear();
        unitList.clear();
    }
}
