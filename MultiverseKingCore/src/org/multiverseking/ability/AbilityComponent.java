package org.multiverseking.ability;

import com.simsilica.es.EntityComponent;
import org.hexgridapi.utility.Vector2Int;
import org.multiverseking.field.Collision;
import org.multiverseking.utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class AbilityComponent implements EntityComponent {

    private final String name;
    private final int power;
    private final byte activationCost;
    private final Vector2Int castRange;
    private final ElementalAttribut eAttribut;
    private final String description;
    private final Collision collision;

    /**
     * Create a new ability for an entity unit.
     */
    public AbilityComponent(String name, Vector2Int castRange, ElementalAttribut eAttribut,
            byte activationCost, int power, Collision collision, String description) {
        this.name = name;
        this.castRange = castRange;
        this.collision = collision;
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
    public Vector2Int getCastRange() {
        return castRange;
    }

    /**
     * Where the ability will hit when activated.
     */
    public Collision getHitCollision() {
        return collision;
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
