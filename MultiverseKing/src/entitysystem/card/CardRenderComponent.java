package entitysystem.card;

import entitysystem.ExtendedComponent;
import entitysystem.attribut.CardRenderPosition;

/**
 * Used by the card render system, Contain where the Card should be rendered on
 * the screen.
 *
 * @author roah
 */
public class CardRenderComponent implements ExtendedComponent {

    private CardRenderPosition cardPos;

    /**
     * Define where the card will be generated/ rendered on the screen.
     * @param cardPos
     */
    public CardRenderComponent(CardRenderPosition cardPos) {
        this.cardPos = cardPos;
    }

    /**
     * where the card is.
     * @see CardRenderPosition
     * @return
     */
    public CardRenderPosition getCardPosition() {
        return cardPos;
    }

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
