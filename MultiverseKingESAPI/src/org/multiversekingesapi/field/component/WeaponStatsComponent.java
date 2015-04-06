package org.multiversekingesapi.field.component;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import org.hexgridapi.utility.HexCoordinate;
import org.multiversekingesapi.utility.ElementalAttribut;

/**
 * @todo rework
 * @author roah
 */
public class WeaponStatsComponent implements PersistentComponent {

    /**
     * Area where the weapon is activated if it can be activated.
     */
    private final byte activationRange;
    /**
     * The hit collision contain where the weapon hit when used. When the weapon
     * is used the rotation and position of the holder is used to know where
     * collision realy happen.
     */
    private final ArrayList<HexCoordinate> hitCollision;
    /**
     * Each weapon consume weapon slot on titan, the avarage slot of a titan is
     * about 4.
     */
    private final byte neededSlot;
    private final int power;
    private final int LoadTime;
    private final ElementalAttribut eAttribut;

    public WeaponStatsComponent(byte activationRange, ArrayList<HexCoordinate> hitCollision, int power, int LoadTime, byte neededSlot, ElementalAttribut eAttribut) {
        this.activationRange = activationRange;
        this.hitCollision = hitCollision;
        this.power = power;
        this.LoadTime = LoadTime;
        this.neededSlot = neededSlot;
        this.eAttribut = eAttribut;
    }

    /**
     * Elemental Attribut affiliated to this Weapon.
     *
     * @return
     */
    public ElementalAttribut getEAttribut() {
        return eAttribut;
    }

    /**
     * Where the weapon will realy got his effect apply, where the weapon can
     * shoot when activated.
     *
     * @return
     */
    public ArrayList<HexCoordinate> getCollisionArea() {
        return hitCollision;
    }

    /**
     * Range for the weapon to trigger his effect, /!\ This should be not used
     * to know where the weapon effect is apply /!\. This is usualy equals to
     * the max collision range arround the weapon holder.
     *
     * @return
     */
    public byte getActivationRange() {
        return activationRange;
    }

    /**
     * The power of the weapon effect, for damage calculation or other.
     *
     * @return
     */
    public int getWeaponPower() {
        return power;
    }

    /**
     * Time needed to reload the weapon, time before the next activation.
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
