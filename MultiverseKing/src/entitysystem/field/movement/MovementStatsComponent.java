package entitysystem.field.movement;

import com.simsilica.es.PersistentComponent;

/**
 * Entity unit, native stats.
 *
 * @author roah
 */
public class MovementStatsComponent implements PersistentComponent {

    private final byte movePoint;
    private final float moveSpeed;

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
     * How fast the unit move on th field.
     *
     * @return
     */
    public float getMoveSpeed() {
        return moveSpeed;
    }
}
