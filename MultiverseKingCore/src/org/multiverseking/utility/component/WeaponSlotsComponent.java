package org.multiverseking.utility.component;

import com.simsilica.es.EntityComponent;

/**
 * Used to balace equipement set to a titan.
 * @author roah
 */
public class WeaponSlotsComponent implements EntityComponent {
    private final byte count;

    public WeaponSlotsComponent(byte count) {
        this.count = count;
    }

    public byte getCount() {
        return count;
    }
}
