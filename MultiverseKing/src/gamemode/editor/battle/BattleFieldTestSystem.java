package gamemode.editor.battle;

import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardRenderComponent;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.AnimationComponent;
import entitysystem.loader.EntityLoader;
import entitysystem.loader.TitanLoader;
import entitysystem.render.RenderComponent;
import hexsystem.HexSettings;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class BattleFieldTestSystem extends EntitySystemAppState {

    private MapData mapData;
    private ArrayList<EntityId> units = new ArrayList<EntityId>();

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        /**
         * Load the map and populate the Menu.
         */
        if (mapData.getMapName() == null || !mapData.getMapName().equalsIgnoreCase("reset")) {
            if (!mapData.loadMap("Reset")) {
                Logger.getLogger(BattleFieldTestSystem.class.getName()).log(Level.WARNING, null, new IOException("Files missing."));
                app.getStateManager().detach(this);
                return null;
            }
        }
        
        /**
         * Init the testingDoll.
         */
        addEntityTestDoll();

        /**
         * Move the camera to the center of the map.
         */
        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(HexSettings.CHUNK_SIZE / 2, HexSettings.CHUNK_SIZE / 2)).convertToWorldPosition();
        ((MultiverseMain) app).getRtsCam().setCenter(new Vector3f(center.x + 3, 15, center.z + 3));

        //*
        return entityData.getEntities(RenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addEntity(Entity e) {
        units.add(e.getId());
    }

    private void addEntityTestDoll() {
        units.add(entityData.createEntity());
        EntityLoader loader = new EntityLoader();
        TitanLoader load = loader.loadTitanStats("TuxDoll");
        entityData.setComponents(units.get(0),
                new CardRenderComponent(CardRenderPosition.FIELD, "TuxDoll"),
                new RenderComponent("TuxDoll"),
                new HexPositionComponent(new HexCoordinate(HexCoordinate.OFFSET, 
                        new Vector2Int(HexSettings.CHUNK_SIZE / 2, HexSettings.CHUNK_SIZE / 2)), Rotation.A),
                new AnimationComponent(Animation.SUMMON),
                load.getInitialStatsComponent(),
                load.getInitialStatsComponent().getMovementComponent());
    }
    
    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        if(units.contains(e.getId())){
            units.remove(e.getId());
        }
    }

    @Override
    protected void cleanupSystem() {
        units.clear();
    }
}
