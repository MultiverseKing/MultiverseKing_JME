package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class SpeedComponent implements PersistentComponent {

    private final float speed;

    public SpeedComponent(float speed) {
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
}
