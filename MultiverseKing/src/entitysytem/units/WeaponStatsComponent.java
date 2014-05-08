package entitysytem.units;

import utility.ElementalAttribut;

/**
 * 
 * @author roah
 */
public class WeaponStatsComponent {
    /**
     * How far the weapon can shoot / got activated.
     */
    private byte range;
    /**
     * How many damage the weapon deal.
     */
    private int damage;
    /**
     * How many time before activation.
     */
    private int LoadTime;
    /**
     * How many slot needed to equip.
     */
    private byte neededSlot;

    /**
     * Elemental attribut affiliated to this weapon.
     */
    private ElementalAttribut eAttribut;

    /**
     * Generate a new Weapon Component.
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
     * @return 
     */
    public ElementalAttribut geteAttribut() {
        return eAttribut;
    }
    
    /**
     * Range of activation.
     * @return 
     */
    public byte getRange() {
        return range;
    }

    /**
     * Damage done on activation.
     * @return 
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Time needed to load the effect.
     * @return 
     */
    public int getLoadTime() {
        return LoadTime;
    }

    /**
     * How much slot this equipement need to be equiped.
     * @return 
     */
    public byte getNeededSlot() {
        return neededSlot;
    }
    
}
