package entitysystem.ability;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import java.util.HashMap;
import utility.ElementalAttribut;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class AbilityComponent implements PersistentComponent {

    private final String name;
    private final int power;
    private final byte activationCost;
    private final Vector2Int range;
    private final ElementalAttribut eAttribut;
    private final String description;
    /**
     * HexCoordinate == where the hit happen relative to the caster unit or
     * target, Byte == Collision layer to use.
     */
    private final HashMap<Byte, ArrayList> hitCollision;

    /**
     * Create a new ability for an entity unit.
     *
     * @param activationRange ability trigger.
     * @param effectSize when activated.
     * @param eAttribut of the effect.
     * @param loadTime between activation.
     */
    public AbilityComponent(String name, Vector2Int range, ElementalAttribut eAttribut,
            byte activationCost, int power, HashMap<Byte, ArrayList> hitCollision, String description) {
        this.name = name;
        this.range = range;
        this.hitCollision = hitCollision;
        this.eAttribut = eAttribut;
        this.activationCost = activationCost;
        this.power = power;
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
    public Vector2Int getRange() {
        return range;
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
     * Segment needed for activation.
     *
     * @return
     */
    public byte getSegment() {
        return activationCost;
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
