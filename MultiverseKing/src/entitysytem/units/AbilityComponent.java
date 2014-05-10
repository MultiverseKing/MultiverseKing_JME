package entitysytem.units;

import com.simsilica.es.PersistentComponent;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class AbilityComponent implements PersistentComponent {
    private final int damage;
    private final float loadTime;
    private final byte activationRange;
    private final byte effectSize;
    private final ElementalAttribut eAttribut;
    private String description;

    /**
     * Create a new ability for an entity unit.
     *
     * @param activationRange ability trigger.
     * @param effectSize when activated.
     * @param eAttribut of the effect.
     * @param loadTime between activation.
     */
    public AbilityComponent(byte activationRange, byte effectSize, ElementalAttribut eAttribut, float loadTime, int damage, String description) {
        this.activationRange = activationRange;
        this.effectSize = effectSize;
        this.eAttribut = eAttribut;
        this.loadTime = loadTime;
        this.damage = damage;
        this.description = description;
    }
    /**
     * Damage done by this ability.
     * @return 
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Area range trigger of the ability.
     *
     * @return
     */
    public byte getActivationRange() {
        return activationRange;
    }

    /**
     * Area effect range of the ability. 0 if non area effect.
     *
     * @return
     */
    public byte getAreaEffectSize() {
        return effectSize;
    }

    /**
     * Time between two activation.
     *
     * @return
     */
    public float getLoadTime() {
        return loadTime;
    }

    /**
     * Elemental Attribut of the ability effect.
     *
     * @return
     */
    public ElementalAttribut getEAttribut() {
        return eAttribut;
    }


    public String getDescription() {
        return description;
    }
    
}
