package entitysytem.units;

import com.simsilica.es.PersistentComponent;

/**
 * Base damage an entity/unit/object/all can do without equipement.
 *
 * @author roah
 */
public class DamageComponent implements PersistentComponent {

    private int damage;

    /**
     * Base damage.
     *
     * @param damage
     */
    public DamageComponent(int damage) {
        this.damage = damage;
    }

    /**
     * Base damage.
     *
     * @return
     */
    public int getDamage() {
        return damage;
    }
}
