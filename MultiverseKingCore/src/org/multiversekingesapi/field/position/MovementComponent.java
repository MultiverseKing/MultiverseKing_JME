package org.multiversekingesapi.field.position;

import com.simsilica.es.EntityComponent;

/**
 * Entity movement stats.
 *
 * @author roah
 */
public class MovementComponent implements EntityComponent {

    private final byte movePoint;
    private final float moveSpeed;

    public MovementComponent(byte movePoint) {
        moveSpeed = 1.5f;
        this.movePoint = movePoint;
    }
    
    public MovementComponent(byte movePoint, float moveSpeed) {
        this.movePoint = movePoint;
        this.moveSpeed = moveSpeed;
    }

    /**
     * How far the unit can travel.
     *
     * @return
     */
    public byte getMovePoint() {
        return movePoint;
    }

    /**
     * How fast the unit move on the field.
     *
     * @return
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }
}
