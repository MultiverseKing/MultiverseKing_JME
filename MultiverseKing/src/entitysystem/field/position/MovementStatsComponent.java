package entitysystem.field.position;

import com.simsilica.es.PersistentComponent;

/**
 * Entity unit, native stats.
 *
 * @author roah
 */
public class MovementStatsComponent implements PersistentComponent {

    private final byte movePoint;
    private final float moveSpeed;

    public MovementStatsComponent(byte movePoint) {
        moveSpeed = 1.5f;
        this.movePoint = movePoint;
    }
    
    public MovementStatsComponent(float moveSpeed, byte movePoint) {
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
