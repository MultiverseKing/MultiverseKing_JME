package entitysytem.units;

import com.simsilica.es.PersistentComponent;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class AbilityComponent implements PersistentComponent {

    private byte activationRange;
    private byte areaEffectSize;
    private float loadTime;
    private ElementalAttribut eAttribut;

    /**
     * Create a new ability for an entity unit.
     *
     * @param activationRange ability trigger.
     * @param effectSize when activated.
     * @param eAttribut of the effect.
     * @param loadTime between activation.
     */
    public AbilityComponent(byte activationRange, byte effectSize, ElementalAttribut eAttribut, float loadTime) {
        this.activationRange = activationRange;
        this.areaEffectSize = effectSize;
        this.eAttribut = eAttribut;
        this.loadTime = loadTime;
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
        return areaEffectSize;
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
}
