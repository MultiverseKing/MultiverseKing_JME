package entitysystem.attribut;

/**
 * 
 * @author roah
 */
public enum CardType {

    /**
     * Used for card who generate an effect on the field.
     */
    ABILITY,
    /**
     * Used for card who affect the Titan Equipement.
     */
    EQUIPEMENT,
    /**
     * Used for card who generate unit on the field.
     */
    SUMMON,
    /**
     * This card Type can't be played directly during battle.
     */
    TITAN;
}
