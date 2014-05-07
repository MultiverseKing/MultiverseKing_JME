package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import utility.attribut.CardRenderPosition;

/**
 * Used by the card render system, Contain where the Card should be rendered on
 * the screen.
 *
 * @author Roah
 */
public class CardRenderComponent implements PersistentComponent {

    private CardRenderPosition cardPos;

    public CardRenderComponent(CardRenderPosition cardPos) {
        this.cardPos = cardPos;
    }

    public CardRenderPosition getCardPosition() {
        return cardPos;
    }
}
