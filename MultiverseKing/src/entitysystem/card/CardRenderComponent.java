package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import utility.attribut.CardRenderPosition;

/**
 * Used by the card render system, Contain where the Card should be rendered on
 * the screen.
 *
 * @author roah
 */
public class CardRenderComponent implements PersistentComponent {

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
}
