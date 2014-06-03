package entitysystem.units;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntityDataAppState;
import entitysystem.render.AnimationComponent;
import entitysystem.card.CardRenderComponent;
import entitysystem.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import utility.HexCoordinate;
import utility.Rotation;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import entitysystem.loader.UnitLoader;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handle interaction on the field.
 *
 * @author roah
 */
public class CollisionSystem extends EntityDataAppState implements HexMapInputListener {

    /**
     * Byte == collision layer (unit, trap, object, spell etc...)
     * 0 == unit
     * 1 == trap
     * 2 == spell
     * more than two is for customLayer or for special unit like flying unit,
     * an object can be on multiple layer at the same time.
     * 
     * HashMap<HexCoordinate, EntityId> == entity position on that layer.
     */
    private HashMap<Byte, ArrayList<EntityId>> collisionLayer = new HashMap<Byte, ArrayList<EntityId>>(3);

    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        return entityData.getEntities(CollisionComponent.class, HexPositionComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void addEntity(Entity e) {
        ArrayList<Byte> entityCollisionLayer = e.get(CollisionComponent.class).getAllCollisionLayer();
        for(Byte layer : entityCollisionLayer){
            if(collisionLayer.isEmpty() || !collisionLayer.containsKey(layer)){
                collisionLayer.put(layer, new ArrayList<EntityId>());
            }
            collisionLayer.get(layer).add(e.getId());
        }
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
        switch (properties.getCardSubType()) {
            case UNIT:
                if(collisionLayer.containsKey((byte)0)){
                    ArrayList<EntityId> list = collisionLayer.get((byte)0);
                    for(EntityId idList : list){
                        ArrayList<HexCoordinate> collision 
                                = entityData.getComponent(idList, CollisionComponent.class).getCollisionOnLayer((byte)0);
                        for(HexCoordinate coord : collision){
                            HexCoordinate worldPos = coord.add(entityData.getComponent(idList, HexPositionComponent.class).getPosition());
                            if(worldPos.equals(castPosition)){
                                return false;
                            }
                        }
                    }
                }
                String name = entityData.getComponent(id, RenderComponent.class).getName();
                UnitLoader unitLoader = new EntityLoader().loadUnitStats(name);
                if (unitLoader != null) {
                    entityData.setComponents(id,
                            new HexPositionComponent(castPosition, Rotation.A),
                            new CardRenderComponent(CardRenderPosition.FIELD),
                            new AnimationComponent(Animation.SUMMON),
                            new EAttributComponent(properties.getElement()),
                            unitLoader.getCollisionComp(), //Collision Comp
                            unitLoader.getuLife(),      //life component
                            unitLoader.getuStats(),      //stats component
                            unitLoader.getAbilityComp());
                    return true;
                } else {
                    System.err.println("Unit can't be loaded missing data. Researched unit : " + name);
                    return false;
                }
            case TRAP:
                //todo : What kind of trap is it ? does it need a walkable tile to be put on ?
                return false;
            case SPELL:
                //todo : instant cast even if the tile isn't walkable.
                return true;
            default:
                throw new UnsupportedOperationException(properties.getCardSubType() + " isn't a valid type for the field system.");
        }
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void removeEntity(Entity e) {
        for(ArrayList layer : collisionLayer.values()){
            if(layer.contains(e.getId())){
                layer.remove(e.getId());
            }
        }
    }
    
    @Override
    protected void cleanupSystem() {
        collisionLayer.clear();
    }
}
