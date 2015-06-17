package org.multiverseking.field.component;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author roah
 */
public class EnergyComponent implements EntityComponent {
    
    private final int current;
    private final int max;

    public EnergyComponent(int max) {
        this.max = max;
        this.current = max;
    }

    public EnergyComponent(int current, int max) {
        this.current = current;
        this.max = max;
    }
    
    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }
}
