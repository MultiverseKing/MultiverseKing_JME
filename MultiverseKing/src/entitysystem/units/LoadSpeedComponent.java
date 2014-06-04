package entitysystem.units;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class LoadSpeedComponent implements PersistentComponent {

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
}
