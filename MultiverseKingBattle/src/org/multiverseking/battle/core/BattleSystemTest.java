package org.multiverseking.battle.core;

import org.multiverseking.core.MultiverseGameState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.utility.Vector2Int;
import org.multiverseking.ability.ActionAbility;
import org.multiverseking.ability.ActionAbilityComponent;
import org.multiverseking.battle.core.ability.ActionSystem;
import org.multiverseking.battle.core.movement.StaminaComponent;
import org.multiverseking.battle.core.focus.MainSelectionSystem;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.battle.core.focus.MainFocusSystem;
import org.multiverseking.battle.core.focus.MainTitanComponent;
import org.multiverseking.battle.core.movement.BattleMovementSystem;
import org.multiverseking.battle.gui.BattleGUI;
import org.multiverseking.core.EntityDataAppState;
import org.multiverseking.core.utility.SubSystem;
import org.multiverseking.field.collision.CollisionData;
import org.multiverseking.field.position.HexMovementSystem;
import org.multiverseking.field.position.component.HexPositionComponent;
import org.multiverseking.render.AbstractRender;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.render.animation.Animation;
import org.multiverseking.render.animation.AnimationComponent;
import org.multiverseking.render.animation.AnimationSystem;
import org.multiverseking.utility.ElementalAttribut;

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
        super(/*CollisionSystem.class,*/AnimationSystem.class, HexMovementSystem.class,
                ActionSystem.class, BattleGUI.class, MainSelectionSystem.class,
                BattleMovementSystem.class, MainFocusSystem.class, BattleInput.class);
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
                "Data/playerData.j3o");
        playerMainUnitsID[0] = entityData.createEntity();
        entityData.setComponents(playerMainUnitsID[0],
                new RenderComponent((String) playerData.getUserData("coreName"),
                        AbstractRender.RenderType.Core, this),
                new HexPositionComponent(new HexCoordinate(
                                HexCoordinate.Coordinate.OFFSET, Vector2Int.fromString(
                                        (String) playerData.getChild("battle").getUserData("corePosition")))),
                new AnimationComponent(Animation.SUMMON));

        loadTitan(playerData.getChild("battle"));
    }

    private void loadTitan(Spatial data) {
        // @TODO Use the titan loader to get the titan stats and ability.
        for (int i = 1; i < 4; i++) {
            HexCoordinate pos = new HexCoordinate(HexCoordinate.Coordinate.OFFSET,
                    Vector2Int.fromString((String) data.getUserData("titan" + i + "Position")));
            EntityId id = entityData.createEntity();
            playerMainUnitsID[i] = id;

            //--------- Action Ability
            CollisionData castRange = new CollisionData(0, CollisionData.Type.CIRCLE, 1, 1);
            CollisionData effectRange = new CollisionData(0, new HexCoordinate());
            String description = "Normal attack";
            ActionAbility ability = new ActionAbility("attack", Animation.ATTACK, 25, 1,
                    ElementalAttribut.EARTH, description, castRange, effectRange);

            //---------
            entityData.setComponents(id, new RenderComponent((String) data.getUserData("titan" + i),
                    AbstractRender.RenderType.Titan, this),
                    new HexPositionComponent(pos.add(Vector2Int.fromString(
                                            (String) data.getUserData("corePosition")))),
                    new AnimationComponent(Animation.SUMMON),
                    new StaminaComponent(),
                    new ActionAbilityComponent(ability),
                    new MainTitanComponent());
            if(i == 1){
                entityData.setComponents(id, new MainFocusComponent());
            }
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
