package org.multiverseking.battle.core.ability;

import com.jme3.input.controls.ActionListener;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.multiverseking.ability.ActionAbilityComponent;
import org.multiverseking.battle.core.focus.MainSelectionSystem;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.slf4j.LoggerFactory;

/**
 * Handle all action outside the bullet zone.
 * @author roah
 */
public class ActionSystem extends EntitySystemAppState {
    
    private HashMap<EntityId, AbilityManager> abilityManager = new HashMap<>();
    private EntityId currentFocus;
    private MainSelectionSystem focusSystem;
    private boolean abilityConfig = false;

    @Override
    protected EntitySet initialiseSystem() {
        app.getStateManager().attach(new ActionSystemGUI());
        focusSystem = app.getStateManager().getState(MainSelectionSystem.class);
        app.getStateManager().getState(GridMouseControlAppState.class).register(tileInputListener);
        registerCharacterKey();
        return entityData.getEntities(ActionAbilityComponent.class);
    }
    
    @Override
    protected void addEntity(Entity e) {
//        RenderType renderType = e.get(RenderComponent.class).getRenderType();
//        if (renderType.equals(RenderType.Titan) || renderType.equals(RenderType.Core)) {
//            isCore = renderType.equals(RenderType.Core);
//            currentFocus = e.getId();
//        }
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
    }

    private void registerCharacterKey() {
        app.getInputManager().addListener(keyListeners, new String[]{
            "skill_weapon", "skill_0", "skill_1", "skill_2", "skill_3", 
            MouseInputEvent.MouseInputEventType.RMB.toString()});
    }

    private final ActionListener keyListeners = (String name, boolean isPressed, float tpf) -> {
        if (!isPressed && name.contains("skill_")) {
            name = name.substring(6);
            LoggerFactory.getLogger(ActionSystem.class).debug("Try to cast skill_{}", name);
            switch(name){
                case "weapon":
                    break;
                case "0":
                    break;
                case "1":
                    break;
                case "2":
                    break;
                case "3":
                    break;
                default:
                    LoggerFactory.getLogger(ActionSystem.class)
                            .error("{} does not exit in is current state.", name);
                    return;
            }
            abilityConfig = true;
        } else if (!isPressed && abilityConfig
                && name.equals(MouseInputEvent.MouseInputEventType.RMB.toString())) {
                    LoggerFactory.getLogger(ActionSystem.class).debug("Cancel current ability.");
                abilityConfig = false;
        }
    };

    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
//            if (currentFocus != null && selectingMovePosition
//                    && event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
//                entities.getEntity(currentFocus).set(new MoveToComponent(event.getPosition()));
//                selectingMovePosition = false;
//                focusSystem.lockMouseFocus(tileInputListener, selectingMovePosition);
//            }
        }
    };

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
}
