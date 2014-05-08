package entitysytem.Units;

import com.simsilica.es.PersistentComponent;

/**
 * Entity stats native unit stats.
 *
 * @author roah
 */
public class UnitStatsComponent implements PersistentComponent {

    /**
     * How many life point the unit got.
     */
    private int life;
    /**
     * How fast the unit load his action.
     */
    private float speed;
    /**
     * How far the unit can travel.
     */
    private byte movePoint;

    public UnitStatsComponent(int life, float speed, byte movePoint) {
        this.life = life;
        this.speed = speed;
        this.movePoint = movePoint;
    }

    /**
     * Life point this unit have.
     *
     * @return
     */
    public int getLife() {
        return life;
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
