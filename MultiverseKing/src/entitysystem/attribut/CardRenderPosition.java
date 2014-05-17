package entitysystem.attribut;

/**
 * Define where card is on the screen.
 *
 * @author roah
 */
public enum CardRenderPosition {

    /**
     * Card in the current player Hand.
     */
    HAND,
    /**
     * Card in the current player deck.
     */
    DECK,
    /**
     * Card on the current field. 
     * (Opponent card show up if activated)
     */
    FIELD,
    /**
     * Card removed from all other Position.
     */
    OUTERWORLD,
}
