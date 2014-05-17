package entitysytem.units;

import entitysystem.ExtendedComponent;

/**
 *
 * @author roah
 */
public class LoadSpeedComponent implements ExtendedComponent {

    private final float speed;

    public LoadSpeedComponent(float speed) {
        this.speed = speed;
    }

    /**
     * Base load speed for all action.
     *
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
