package entitysytem.units;

import com.simsilica.es.PersistentComponent;

/**
 * Entity unit, native stats.
 *
 * @author roah
 */
public class UnitStatsComponent implements PersistentComponent {

    private final float speed;
    private final byte movePoint;

    public UnitStatsComponent(float speed, byte movePoint) {
        this.speed = speed;
        this.movePoint = movePoint;
    }

    /**
     * Base load speed for all action.
     *
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * How far the unit can travel.
     *
     * @return
     */
    public byte getMovePoint() {
        return movePoint;
    }
}
