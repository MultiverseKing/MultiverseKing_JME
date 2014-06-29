package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class HealthComponent implements PersistentComponent {

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
