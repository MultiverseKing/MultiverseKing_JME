package org.multiverseking.battle.battleSystem.ability;

import com.jme3.input.controls.ActionListener;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.multiverseking.battle.battleSystem.focus.FocusSystem;
import org.multiverseking.battle.battleSystem.focus.MainFocusComponent;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.field.position.MoveToComponent;
import org.multiverseking.render.AbstractRender.RenderType;
import org.multiverseking.render.RenderComponent;

/**
 *
 * @author roah
 */
public class AbilitySystem extends EntitySystemAppState {
    
    private boolean selectingMovePosition = false;
    private HashMap<EntityId, AbilityManager> abilityManager = new HashMap<>();
    private EntityId currentFocus;
    private FocusSystem focusSystem;

    @Override
    protected EntitySet initialiseSystem() {
        focusSystem = app.getStateManager().getState(FocusSystem.class);
        app.getStateManager().getState(GridMouseControlAppState.class).register(tileInputListener);
        registerCharacterKey();
        return entityData.getEntities(MainFocusComponent.class, RenderComponent.class);
    }

    @Override
    protected void addEntity(Entity e) {
        RenderType renderType = e.get(RenderComponent.class).getRenderType();
        if (renderType.equals(RenderType.Titan) || renderType.equals(RenderType.Core)) {
            if(currentFocus != null) {
                entityData.removeComponent(currentFocus, MainFocusComponent.class);
            }
            currentFocus = e.getId();
        }
//        AbilityManager aManager = new AbilityManager(e.get(AbilityComponent.class).getName(),
//                e.get(AbilityComponent.class).getSegment(), e.get(SpeedComponent.class).getSpeed());
//        abilityManager.put(e.getId(), aManager);
    }

    @Override
    protected void updateSystem(float tpf) {
//        for (EntityId id : abilityManager.keySet()) {
//            cast(abilityManager.get(id).update(tpf), id);
//        }
    }

    private void cast(ArrayList<String> abilityCast, EntityId id) {
//        if (abilityCast != null) {
//            for (String abilityName : abilityCast) {
//                EntityId abilityID = entityData.createEntity();
//                /**
//                 * @todo : Get all ability in the list, call the FXSystem to
//                 * render them on the screen, call other system to the ability
//                 * effect.
//                 */
//            }
//        }
    }

    private void registerCharacterKey() {
        app.getInputManager().addListener(keyListeners, new String[]{
            "attack", "defends", "move", "skill_0", "skill_1", "skill_2",
            "skill_3", MouseInputEvent.MouseInputEventType.RMB.toString()});
    }

    private final ActionListener keyListeners = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                if (name.equals("attack") || name.equals("defends")) {
                    System.out.println("use - " + name);
                } else if (name.contains("skill_")) {
                    System.out.println("use - " + name);
                } else if (name.equals("move") && !selectingMovePosition
                        || selectingMovePosition && name.equals(MouseInputEvent.MouseInputEventType.RMB.toString())) {
                    selectingMovePosition = !selectingMovePosition;
                    focusSystem.lockMouseFocus(tileInputListener, selectingMovePosition);
                }
            }
        }
    };

    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            if (currentFocus != null && selectingMovePosition
                    && event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
                entities.getEntity(currentFocus).set(new MoveToComponent(event.getPosition()));
                selectingMovePosition = false;
                focusSystem.lockMouseFocus(tileInputListener, selectingMovePosition);
            }
        }
    };

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
        if (e.getId().equals(currentFocus)) {
            currentFocus = null;
        }
    }

    @Override
    protected void cleanupSystem() {
        app.getInputManager().removeListener(keyListeners);
        app.getStateManager().getState(GridMouseControlAppState.class).unregister(tileInputListener);
        focusSystem.lockMouseFocus(tileInputListener, false);
    }
}
