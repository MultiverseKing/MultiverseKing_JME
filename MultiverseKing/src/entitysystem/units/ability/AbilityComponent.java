package entitysystem.units.ability;

import entitysystem.ExtendedComponent;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class AbilityComponent implements ExtendedComponent {
    private final String name;
    private final int damage;
    private final byte segment;
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
    public AbilityComponent(String name, byte activationRange, byte effectSize, 
            ElementalAttribut eAttribut, byte segment, int damage, String description) {
        this.name = name;
        this.activationRange = activationRange;
        this.effectSize = effectSize;
        this.eAttribut = eAttribut;
        this.segment = segment;
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
     * Segment needed for activation.
     *
     * @return
     */
    public byte getSegment() {
        return segment;
    }

    /**
     * Elemental Attribut of the ability effect.
     *
     * @return
     */
    public ElementalAttribut getEAttribut() {
        return eAttribut;
    }

    /**
     * Description of the ability.
     * @return 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Name of the ability.
     * @return 
     */
    public String getName() {
        return name;
    }

    @Override
    public ExtendedComponent clone() {
        return new AbilityComponent(name, activationRange, effectSize, eAttribut, segment, damage, description);
    }
}
