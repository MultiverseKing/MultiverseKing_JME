package entitysystem.attribut;

/**
 *
 * @author roah
 */
public enum Rarity {

    /**
     * 4 duplicate card limit by deck, 50 equipped card limit by world.
     */
    COMMON,
    /**
     * 3 duplicate card limit by deck, 12 equipped card limit by world.
     */
    RARE,
    /**
     * No duplicate card, 5 equipped card limit by world.
     */
    EPIQUE,
    /**
     * No duplicate card, 1 equipped card limit by world, 1 use by battle.
     */
    UNIQUE,
    /**
     * No duplicate card, 1 card by Galaxy, cannot be used by player during battle.
     */
    ANTIQUE,
    /**
     * No duplicate card, 1 card by Universe, 1 use by battle.
     */
    UNIVERSE,
    /**
     * No duplicate card, 1 card by Multiverse, 1 use by battle.
     */
    MULTIVERSE;
}
