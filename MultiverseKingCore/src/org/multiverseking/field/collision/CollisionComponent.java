package org.multiverseking.field.collision;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author roah
 */
public class CollisionComponent implements EntityComponent {

    /**
     * Collision layer of the entity and collision size.
     */
    private final CollisionData collisionData;

    /**
     * Create a new collision component for a 1 Hex size unit defined layer.
     */
    public CollisionComponent(CollisionData collisionData) {
        this.collisionData = collisionData;
    }

    public CollisionData getCollisionData() {
        return collisionData;
    }
}
