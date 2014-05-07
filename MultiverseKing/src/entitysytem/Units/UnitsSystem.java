/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysytem.Units;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.render.RenderComponent;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class UnitsSystem extends EntitySystemAppState {

    @Override
    protected EntitySet initialiseSystem() {
        return entityData.getEntities(StatsComponent.class, RenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    public void canBeSummon(HexCoordinate castPosition, Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
