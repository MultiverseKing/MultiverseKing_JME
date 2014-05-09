package entitysytem.units;

import com.simsilica.es.PersistentComponent;

/**
 * Entity Titan Stats.
 *
 * @author roah
 */
public class TitanStatsComponent implements PersistentComponent {

    private int armor;

    public TitanStatsComponent(int armor) {
        this.armor = armor;
    }

    /**
     * Damage reduction without equipement.
     *
     * @return
     */
    public int getArmor() {
        return armor;
    }
}
