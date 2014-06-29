package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 * Used to balace equipement set to a titan.
 * @author roah
 */
public class WeaponSlotsComponent implements PersistentComponent {
    private final byte count;

    public WeaponSlotsComponent(byte count) {
        this.count = count;
    }

    public byte getCount() {
        return count;
    }
}
