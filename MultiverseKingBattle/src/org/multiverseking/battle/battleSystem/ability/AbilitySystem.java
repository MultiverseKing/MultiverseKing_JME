package org.multiverseking.battle.battleSystem.ability;

import battleSystem.ability.AbilityComponent;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiverseking.utility.system.EntitySystemAppState;
import java.util.ArrayList;
import java.util.HashMap;
import org.multiverseking.field.component.SpeedComponent;

/**
 *
 * @author roah
 */
public class AbilitySystem extends EntitySystemAppState {

    private boolean skillTab = false;
    private HashMap<EntityId, AbilityManager> abilityManager = new HashMap<>();

    @Override
    protected EntitySet initialiseSystem() {
        registerCharacterKey(app.getInputManager());
        return entityData.getEntities(SpeedComponent.class, AbilityComponent.class);
    }

    @Override
    protected void addEntity(Entity e) {
        AbilityManager aManager = new AbilityManager(e.get(AbilityComponent.class).getName(),
                e.get(AbilityComponent.class).getSegment(), e.get(SpeedComponent.class).getSpeed());
        abilityManager.put(e.getId(), aManager);
    }

    @Override
    protected void updateSystem(float tpf) {
        for (EntityId id : abilityManager.keySet()) {
            cast(abilityManager.get(id).update(tpf), id);
        }
    }

    private void cast(ArrayList<String> abilityCast, EntityId id) {
        if (abilityCast != null) {
            for (String abilityName : abilityCast) {
                EntityId abilityID = entityData.createEntity();
                /**
                 * @todo : Get all ability in the list, call the FXSystem to
                 * render them on the screen, call other system to the ability
                 * effect.
                 */
            }
        }
    }

    private void registerCharacterKey(InputManager inputManager) {
        inputManager.addMapping("skill_0", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("skill_1", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("skill_2", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("skill_3", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("skillTab", new KeyTrigger(KeyInput.KEY_LMENU));
        inputManager.addListener(keyListeners, new String[]{"skill_0", "skill_1", "skill_2", "skill_3", "skillTab"});
    }

    private void unRegisterCharacterKey(InputManager inputManager) {
        inputManager.deleteMapping("skill_0");
        inputManager.deleteMapping("skill_1");
        inputManager.deleteMapping("skill_2");
        inputManager.deleteMapping("skill_3");
        inputManager.deleteMapping("skillTab");
    }
    private ActionListener keyListeners = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("skillTab") && isPressed) {
                skillTab = true;
            } else if(name.equals("skillTab") && !isPressed) {
                skillTab = false;
            }
            if (name.contains("skill_") && !isPressed) {
                if(skillTab) {
                    System.err.println("use ALT - " + name);
                } else {
                    System.err.println("use - " + name);
                }
            }
        }
    };

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    @Override
    protected void cleanupSystem() {
        unRegisterCharacterKey(app.getInputManager());
    }
}
