package entitysytem.units;

import entitysystem.movement.MovementStatsComponent;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.render.AnimationComponent;
import entitysystem.card.CardRenderComponent;
import entitysystem.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import hexsystem.HexTile;
import java.util.ArrayList;
import utility.HexCoordinate;
import utility.Rotation;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardProperties;
import entitysystem.loader.UnitLoader;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;

/**
 * Handle interaction on the field.
 *
 * @author roah
 */
public class FieldSystem extends EntitySystemAppState implements HexMapInputListener {

    ArrayList<EntityId> units = new ArrayList<EntityId>();
    ArrayList<EntityId> trap = new ArrayList<EntityId>();
    ArrayList<EntityId> persistentSpell = new ArrayList<EntityId>();

    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        return entityData.getEntities(MovementStatsComponent.class, RenderComponent.class, HexPositionComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void addEntity(Entity e) {
        units.add(e.getId());
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Check if can be casted on the defined position.
     *
     * @param castPosition
     * @param id entity to cast.
     * @return true if it can, false otherwise.
     */
    public boolean canBeCast(HexCoordinate castPosition, EntityId id, CardProperties properties) {
        HexTile tile = getMapData().getTile(castPosition);

        if (tile != null) {
            boolean walkable = getMapData().getTile(castPosition).getWalkable();
            switch (properties.getCardSubType()) {
                case SUMMON:
                    if (walkable) {
                        String name = entityData.getComponent(id, RenderComponent.class).getName();
                        UnitLoader unitLoader = entityData.getEntityLoader().loadUnitStats(name);
                        if (unitLoader != null) {
                            entityData.setComponents(id,
                                    new HexPositionComponent(castPosition, Rotation.A),
                                    new CardRenderComponent(CardRenderPosition.FIELD),
                                    new AnimationComponent(Animation.SUMMON),
                                    new EAttributComponent(properties.getElement()),
                                    unitLoader.getuLife(),      //life component
                                    unitLoader.getuStats(),      //stats component
                                    unitLoader.getAbilityComp());
                            return true;
                        } else {
                            System.err.println("Unit can't be loaded mising data. Researched unit : " + name);
                            return false;
                        }
                    } else {
                        return false;
                    }
                case TRAP:
                    //todo : What kind of trap is it ? does it need a walkable tile to be put on ?
                    break;
                case SPELL:
                    //todo : instant cast even if the tile isn't walkable.
                    return true;
                default:
                    throw new UnsupportedOperationException(properties.getCardSubType() + " isn't a valid type for the field system.");
            }
        }
        //tile does not exist nothing to cast.
        return false;
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void removeEntity(Entity e) {
        units.remove(e.getId());
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void cleanupSystem() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
