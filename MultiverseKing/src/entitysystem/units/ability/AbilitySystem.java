package entitysystem.units.ability;

import entitysystem.render.VFXComponent;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntityDataAppState;
import entitysystem.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import entitysystem.units.LoadSpeedComponent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author roah
 */
public class AbilitySystem extends EntityDataAppState {

    HashMap<EntityId, AbilityManager> abilityManager = new HashMap<EntityId, AbilityManager>();

    @Override
    protected EntitySet initialiseSystem() {
        return entityData.getEntities(LoadSpeedComponent.class, AbilityComponent.class, HexPositionComponent.class);
    }
    
    @Override
    protected void addEntity(Entity e) {
        AbilityManager aManager = new AbilityManager(e.get(AbilityComponent.class).getName(),
                e.get(AbilityComponent.class).getSegment(), e.get(LoadSpeedComponent.class).getSpeed());
        abilityManager.put(e.getId(), aManager);
    }

    @Override
    protected void updateSystem(float tpf) {
        for (EntityId id : abilityManager.keySet()) {
            cast(abilityManager.get(id).update(tpf), id);
        }
    }
    
    private void cast(ArrayList<String> abilityCast, EntityId id) {
        if(abilityCast != null){
            for(String abilityName : abilityCast){
                EntityId abilityID = entityData.createEntity();
                entityData.setComponents(abilityID, new VFXComponent(abilityName), 
                        entityData.getComponent(id, HexPositionComponent.class).clone());
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
