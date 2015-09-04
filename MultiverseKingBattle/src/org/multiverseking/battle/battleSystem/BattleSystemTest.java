package org.multiverseking.battle.battleSystem;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import java.util.ArrayList;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.utility.Vector2Int;
import org.multiverseking.battle.battleSystem.ability.AbilitySystem;
import org.multiverseking.battle.battleSystem.component.FocusComponent;
import org.multiverseking.field.CollisionSystem;
import org.multiverseking.field.exploration.HexMovementSystem;
import org.multiverseking.field.position.HexPositionComponent;
import org.multiverseking.field.position.MoveToComponent;
import org.multiverseking.render.AbstractRender;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.render.animation.Animation;
import org.multiverseking.render.animation.AnimationComponent;
import org.multiverseking.render.animation.AnimationSystem;
import org.multiverseking.utility.system.DependendAppState;
import org.multiverseking.utility.system.EntityDataAppState;

/**
 *
 * @author roah
 */
public class BattleSystemTest extends DependendAppState {
    private GridMouseControlAppState mouseSystem;
    private ArrayList<EntityId> playerUnits = new ArrayList<>();
    private Integer selectedUnit = 0;
    private RTSCamera camera;
    private RenderSystem renderSystem;
    private boolean countDown = false;
    private boolean selectingMovePosition = false;
    private float timerCountDown = 0;
    private EntityData entityData;

    public BattleSystemTest() {
        super(new Class[]{
            CollisionSystem.class, AnimationSystem.class,
            HexMovementSystem.class, AbilitySystem.class});
//            BattleGUIRender.class});
    }

    @Override
    public void initialiseSystem(AppStateManager stateManager) {
        this.camera = app.getStateManager().getState(RTSCamera.class);
        this.renderSystem = app.getStateManager().getState(RenderSystem.class);
        this.mouseSystem = app.getStateManager().getState(GridMouseControlAppState.class);
        this.entityData = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
        mouseSystem.register(tileInputListener);
        /**
         * Load the player titan core to use during the battle.
         */
        loadPlayerData();
        registerInput();
    }

    private void loadPlayerData() {
        Node playerData = (Node) app.getAssetManager().loadModel(
                "org/multiverseking/assets/Data/playerData.j3o");
        playerUnits.add(entityData.createEntity());
        entityData.setComponents(playerUnits.get(0),
                new RenderComponent((String) playerData.getUserData("coreName"),
                AbstractRender.RenderType.Core),
                new HexPositionComponent(new HexCoordinate(
                HexCoordinate.Coordinate.OFFSET, Vector2Int.fromString(
                (String) playerData.getChild("battle").getUserData("corePosition")))),
                new AnimationComponent(Animation.SUMMON));

        loadTitan(playerData.getChild("battle"));
    }

    private void loadTitan(Spatial data) {
        for (int i = 1; i < 4; i++) {
            HexCoordinate pos = new HexCoordinate(HexCoordinate.Coordinate.OFFSET,
                    Vector2Int.fromString((String) data.getUserData("titan" + i + "Position")));
            EntityId id = entityData.createEntity();
            playerUnits.add(id);
            entityData.setComponents(id, new RenderComponent((String) data.getUserData("titan" + i),
                    AbstractRender.RenderType.Titan),
                    new HexPositionComponent(pos.add(Vector2Int.fromString(
                    (String) data.getUserData("corePosition")))),
                    new AnimationComponent(Animation.SUMMON));
        }
    }

    /**
     * @todo fr && us input
     */
    private void registerInput() { //col input
        app.getInputManager().addMapping("char_0", new KeyTrigger(KeyInput.KEY_F1));
        app.getInputManager().addMapping("char_1", new KeyTrigger(KeyInput.KEY_F2));
        app.getInputManager().addMapping("char_2", new KeyTrigger(KeyInput.KEY_F3));
        app.getInputManager().addMapping("char_3", new KeyTrigger(KeyInput.KEY_F4));
        app.getInputManager().addMapping("move", new KeyTrigger(KeyInput.KEY_T));
        app.getInputManager().addMapping("charAttack", new KeyTrigger(KeyInput.KEY_Q));
        app.getInputManager().addMapping("charBlock", new KeyTrigger(KeyInput.KEY_F));
//        app.getInputManager().addMapping("charDodge", new KeyTrigger(KeyInput.KEY_P + clic));
        app.getInputManager().addListener(keyListeners,
                new String[]{"char_0", "char_1", "char_2", "char_3", "move", "Cancel"});
    }
    private ActionListener keyListeners = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed && name.contains("char")) {
                Integer tmp = Integer.valueOf(name.split("_")[1]);
                if (countDown && selectedUnit == tmp) {
                    camera.setCenter(renderSystem.getSpatial(
                            playerUnits.get(selectedUnit)).getLocalTranslation());
                    countDownStop();
                } else {
                    entityData.removeComponent(playerUnits.get(selectedUnit), FocusComponent.class);
                    entityData.setComponent(playerUnits.get(tmp), new FocusComponent());
                    selectedUnit = tmp;
                    countDownReset();
                }
                selectingMovePosition = false;
            } else if (!isPressed && name.equals("move")
                    || !isPressed && selectingMovePosition && name.equals("Cancel")) {
                selectingMovePosition = !selectingMovePosition;
            }
        }
    };

    /**
     * Countdown is used for doubleTap input
     * (when pressing the key twice center the character).
     */
    protected void updateSystem(float tpf) {
        if (countDown) {
            timerCountDown += tpf;
            if (timerCountDown >= 2) {
                countDownStop();
            }
        }
    }

    private void countDownReset() {
        countDownStop();
        countDown = true;
    }

    private void countDownStop() {
        timerCountDown = 0;
        countDown = false;
    }
    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            if (selectingMovePosition && selectedUnit != 0
                    && event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
                entityData.setComponent(playerUnits.get(selectedUnit),
                        new MoveToComponent(event.getPosition()));
                selectingMovePosition = false;
            }
        }
    };

    @Override
    public void cleanupSystem() {
    }
}
