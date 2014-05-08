package entitysytem.units;

/**
 * Base damage an entity/unit can do without equipement.
 *
 * @author roah
 */
public class DamageComponent {

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
     * Base damage of this entity.
     *
     * @return
     */
    public int getDamage() {
        return damage;
    }
}
