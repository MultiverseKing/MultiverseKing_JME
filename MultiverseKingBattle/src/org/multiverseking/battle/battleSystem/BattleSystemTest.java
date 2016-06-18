package org.multiverseking.battle.battleSystem;

import org.multiverseking.core.MultiverseGameState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.utility.Vector2Int;
import org.multiverseking.battle.battleSystem.ability.AbilitySystem;
import org.multiverseking.battle.battleSystem.focus.FocusComponent;
import org.multiverseking.battle.battleSystem.focus.FocusSystem;
import org.multiverseking.battle.battleSystem.focus.MainFocusComponent;
import org.multiverseking.battle.battleSystem.gui.BattleGUI;
import org.multiverseking.core.EntityDataAppState;
import org.multiverseking.core.utility.SubSystem;
import org.multiverseking.field.CollisionSystem;
import org.multiverseking.field.exploration.HexMovementSystem;
import org.multiverseking.field.position.HexPositionComponent;
import org.multiverseking.render.AbstractRender;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.render.animation.Animation;
import org.multiverseking.render.animation.AnimationComponent;
import org.multiverseking.render.animation.AnimationSystem;

/**
 *
 * @author roah
 */
public class BattleSystemTest extends MultiverseGameState implements SubSystem {

    private GridMouseControlAppState mouseSystem;
    private final EntityId[] playerMainUnitsID = new EntityId[4];
    private RenderSystem renderSystem;
    private EntityData entityData;

    public BattleSystemTest() {
        super(CollisionSystem.class, AnimationSystem.class,
                HexMovementSystem.class, AbilitySystem.class,
                BattleGUI.class, BattleInput.class, FocusSystem.class);
    }

    @Override
    public void initializeSystem(AppStateManager stateManager) {
        this.renderSystem = app.getStateManager().getState(RenderSystem.class);
        this.mouseSystem = app.getStateManager().getState(GridMouseControlAppState.class);
        this.entityData = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
        renderSystem.registerSubSystem(this, true);
        /**
         * Load the player titan core to use during the battle.
         */
        loadPlayerData();
    }

    private void loadPlayerData() {
        Node playerData = (Node) app.getAssetManager().loadModel(
                "org/multiverseking/assets/Data/playerData.j3o");
        playerMainUnitsID[0] = entityData.createEntity();
        entityData.setComponents(playerMainUnitsID[0],
                new RenderComponent((String) playerData.getUserData("coreName"),
                        AbstractRender.RenderType.Core, this),
                new HexPositionComponent(new HexCoordinate(
                                HexCoordinate.Coordinate.OFFSET, Vector2Int.fromString(
                                        (String) playerData.getChild("battle").getUserData("corePosition")))),
                new AnimationComponent(Animation.SUMMON),
                new MainFocusComponent());

        loadTitan(playerData.getChild("battle"));
    }

    private void loadTitan(Spatial data) {
        for (int i = 1; i < 4; i++) {
            HexCoordinate pos = new HexCoordinate(HexCoordinate.Coordinate.OFFSET,
                    Vector2Int.fromString((String) data.getUserData("titan" + i + "Position")));
            EntityId id = entityData.createEntity();
            playerMainUnitsID[i] = id;
            entityData.setComponents(id, new RenderComponent((String) data.getUserData("titan" + i),
                    AbstractRender.RenderType.Titan, this),
                    new HexPositionComponent(pos.add(Vector2Int.fromString(
                                            (String) data.getUserData("corePosition")))),
                    new AnimationComponent(Animation.SUMMON));
        }
    }
    
    protected void updateSystem(float tpf) {
    }

    public EntityId[] getMainUnitsID() {
        return playerMainUnitsID;
    }

    @Override
    public void cleanupSystem() {
        for (EntityId id : playerMainUnitsID) {
            entityData.removeEntity(id);
        }
        renderSystem.removeSubSystem(this, true);
    }

    @Override
    public void rootSystemIsRemoved() {
    }
}
