package org.multiverseking.field.position.component;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author roah
 */
public class SpeedComponent implements EntityComponent {

    private final float speed;

    public SpeedComponent(float speed) {
        this.speed = speed;
    }

    /**
     * Base load speed for all action.
     */
    public float getSpeed() {
        return speed;
    }
}
