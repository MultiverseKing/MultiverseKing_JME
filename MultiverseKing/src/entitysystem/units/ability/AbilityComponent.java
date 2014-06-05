package entitysystem.units.ability;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import java.util.HashMap;
import utility.ElementalAttribut;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class AbilityComponent implements PersistentComponent {

    private final String name;
    private final int power;
    private final byte activationSegment;
    private final byte activationRange;
    private final ElementalAttribut eAttribut;
    private final String description;
    /**
     * HexCoordinate == where the hit happen relative to the caster unit or
     * target, Byte == Collision layer to use.
     */
    private final HashMap<Byte, ArrayList> hitCollision;
    private final boolean castFromSelf;

    /**
     * Create a new ability for an entity unit.
     *
     * @param activationRange ability trigger.
     * @param effectSize when activated.
     * @param eAttribut of the effect.
     * @param loadTime between activation.
     */
    public AbilityComponent(String name, byte activationRange, ElementalAttribut eAttribut,
            byte activationSegment, int power, HashMap<Byte, ArrayList> hitCollision,
            boolean castFromSelf, String description) {
        this.name = name;
        this.activationRange = activationRange;
        this.hitCollision = hitCollision;
        this.eAttribut = eAttribut;
        this.activationSegment = activationSegment;
        this.power = power;
        this.castFromSelf = castFromSelf;
        this.description = description;
    }

    /**
     * Power this ability have.
     *
     * @return
     */
    public int getPower() {
        return power;
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
     * Where the ability will hit when activated.
     *
     * @return
     */
    public HashMap<Byte, ArrayList> getHitCollision() {
        return hitCollision;
    }

    /**
     * is the ability collision hit is relative to the caster unit or the target
     * unit.
     *
     * @return
     */
    public boolean isCastFromSelf() {
        return castFromSelf;
    }

    /**
     * Segment needed for activation.
     *
     * @return
     */
    public byte getSegment() {
        return activationSegment;
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
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Name of the ability.
     *
     * @return
     */
    public String getName() {
        return name;
    }
}
