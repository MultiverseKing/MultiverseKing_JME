package org.multiversekingesapi.ability;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiversekingesapi.EntitySystemAppState;
import java.util.ArrayList;
import java.util.HashMap;
import org.multiversekingesapi.field.component.SpeedComponent;

/**
 *
 * @author roah
 */
public class AbilitySystem extends EntitySystemAppState {

    HashMap<EntityId, AbilityManager> abilityManager = new HashMap<>();

    @Override
    protected EntitySet initialiseSystem() {
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

    @Override
    protected void updateEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cleanupSystem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
