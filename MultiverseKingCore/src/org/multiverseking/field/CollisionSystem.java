package org.multiverseking.field;

import org.multiverseking.field.component.CollisionComponent;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiverseking.EntitySystemAppState;
import org.multiverseking.render.AbstractRender.RenderType;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.multiverseking.field.position.HexPositionComponent;

/**
 * Handle interaction on the field.
 *
 * @author roah
 */
public class CollisionSystem extends EntitySystemAppState {

    /**
     * Byte == collision layer (unit, trap, object, spell etc...) 0 == unit 1 ==
     * trap 2 == spell more than two is for customLayer or for special unit like
     * flying unit, an object can be on multiple layer at the same time.
     *
     * HashMap<HexCoordinate, EntityId> == entity position on that layer.
     */
    private HashMap<Byte, ArrayList<EntityId>> collisionLayer = new HashMap<>(3);

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
        Byte[] entityCollisionLayer = e.get(CollisionComponent.class).getUsedLayers();
        for (Byte layer : entityCollisionLayer) {
            if (collisionLayer.isEmpty() || !collisionLayer.containsKey(layer)) {
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
    public boolean isValidPosition(HexCoordinate castPosition, RenderType renderType) {
        switch (renderType) {
            case Ability:
                return true;
            case Core:
                return false;
            case Debug:
                return true;
            case Environment:
                return false;
            case Equipement:
                return false;
            case Titan:
                return false;
            case Unit:
                if (collisionLayer.containsKey((byte) 0)) {
                    return checkCollision(new Byte((byte) 0), castPosition);
                }
                return true;
            default:
                throw new UnsupportedOperationException(renderType + " isn't a valid type for the field system.");
        }
    }

    private boolean checkCollision(Byte layer, HexCoordinate position) {
        for (EntityId currentId : collisionLayer.get(layer.byteValue())) {
            ArrayList<HexCoordinate> collision = entityData.getComponent(currentId, CollisionComponent.class).getCollisionOnLayer((byte) 0);
            for (HexCoordinate coord : collision) {
                HexCoordinate worldPos = coord.add(entityData.getComponent(currentId, HexPositionComponent.class).getPosition());
                if (worldPos.equals(position)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void removeEntity(Entity e) {
        for (Byte layer : collisionLayer.keySet()) {
            if (collisionLayer.get(layer).contains(e.getId())) {
                collisionLayer.get(layer).remove(e.getId());
            }
        }
    }

    @Override
    protected void cleanupSystem() {
        for (Byte b : collisionLayer.keySet()) {
            collisionLayer.get(b).clear();
        }
        collisionLayer.clear();
    }
}
