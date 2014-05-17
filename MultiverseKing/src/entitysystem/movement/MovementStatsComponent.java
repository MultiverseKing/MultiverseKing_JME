package entitysystem.movement;

import entitysystem.ExtendedComponent;

/**
 * Entity unit, native stats.
 *
 * @author roah
 */
public class MovementStatsComponent implements ExtendedComponent {

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

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
