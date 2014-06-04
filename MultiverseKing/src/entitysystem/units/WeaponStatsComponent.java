package entitysystem.units;

import com.simsilica.es.PersistentComponent;
import utility.ElementalAttribut;

/**
 * @todo rework
 * @author roah
 */
public class WeaponStatsComponent implements PersistentComponent {

    private final byte range;
    private final int damage;
    private final int LoadTime;
    private final byte neededSlot;
    private final ElementalAttribut eAttribut;

    /**
     * Generate a new Weapon Component.
     *
     * @param range of activation.
     * @param damage on activation. (base dmg)
     * @param LoadTime before activation.
     * @param neededSlot to be equiped.
     * @param eAttribut core Element.
     */
    public WeaponStatsComponent(byte range, int damage, int LoadTime, byte neededSlot, ElementalAttribut eAttribut) {
        this.range = range;
        this.damage = damage;
        this.LoadTime = LoadTime;
        this.neededSlot = neededSlot;
        this.eAttribut = eAttribut;
    }

    /**
     * Elemental Attribut affiliated to this Weapon.
     *
     * @return
     */
    public ElementalAttribut geteAttribut() {
        return eAttribut;
    }

    /**
     * Range for the weapon to trigger his effect, how far the weapon can shoot
     * when activated.
     *
     * @return
     */
    public byte getRange() {
        return range;
    }

    /**
     * How many damage the weapon deal.
     *
     * @return
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Time needed to load the effect, time before the next trigger.
     *
     * @return
     */
    public int getLoadTime() {
        return LoadTime;
    }

    /**
     * How much slot this equipement consume when equiped.
     *
     * @return
     */
    public byte getNeededSlot() {
        return neededSlot;
    }
}
