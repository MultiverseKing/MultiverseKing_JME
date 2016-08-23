package org.multiverseking.field.collision;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.field.position.component.HexPositionComponent;
import org.multiverseking.render.AbstractRender.RenderType;

/**
 * Handle interaction on the field.
 *
 * Byte == collision layer (unit, trap, object, spell etc...) -1 == all layer 0
 * == unit 1 == trap 2 == spell more than two is for customLayer or for special
 * unit like flying unit, an object can be on multiple layer at the same time.
 *
 * @todo need better collision handling
 * @todo refactor
 * @author roah
 */
public class CollisionSystem extends EntitySystemAppState {

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
//        int layer = e.get(CollisionComponent.class).getCollisionData().getLayer();
//        if (!collisionLayer.containsKey(layer)) {
//            collisionLayer.put(layer, new ArrayList<>());
//        }
//        collisionLayer.get(layer).add(e.getId());
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
    public boolean isValidPosition(HexCoordinate castPosition, RenderType renderType) {
        switch (renderType) {
            case CORE:
                return false;
            case DEBUG:
                return true;
            case ENVIRONMENT:
                return false;
            case TITAN:
                return false;
            case UNIT:
//                if (collisionLayer.containsKey((byte) 0)) {
//                    return checkCollision(new Byte((byte) 0), castPosition);
//                }
                return true;
            default:
                throw new UnsupportedOperationException(renderType + " isn't a valid type for the field system.");
        }
    }

    private boolean checkCollision(int layer, HexCoordinate position) {
//        for (EntityId currentId : collisionLayer.get(layer)) {
//            CollisionData collisionData = entities.getEntity(currentId).get(CollisionComponent.class).getCollisionData();
//            switch (collisionData.getType()) {
//                case CUSTOM:
//                    for (HexCoordinate coord : collisionData.getPosition()) {
//                        HexCoordinate worldPos = coord.add(entityData
//                                .getComponent(currentId, HexPositionComponent.class).getPosition());
//                        if (worldPos.equals(position)) {
                            return false;
//                        }
//                    }
//                    break;
//                default:
//                //@todo other collision type
//            }
//        }
//        return true;
    }

    @Override
    protected void removeEntity(Entity e) {
//        for (Iterator<Integer> it = collisionLayer.keySet().iterator(); it.hasNext();) {
//            int layer = it.next();
//            if (collisionLayer.get(layer).contains(e.getId())) {
//                collisionLayer.get(layer).remove(e.getId());
//            }
//        }
    }

    @Override
    protected void cleanupSystem() {
//        for (Byte b : collisionLayer.keySet()) {
//            collisionLayer.get(b).clear();
//        }
//        collisionLayer.clear();
    }
}
