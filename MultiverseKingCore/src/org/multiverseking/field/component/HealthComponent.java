package org.multiverseking.field.component;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author roah
 */
public class HealthComponent implements EntityComponent {

    private final int max;
    private final int current;

    public HealthComponent(int max) {
        this.max = max;
        this.current = max;
    }

    public HealthComponent(int max, int current) {
        this.max = max;
        this.current = current;
    }

    public int getMax() {
        return max;
    }

    public int getCurrent() {
        return current;
    }    
}
