package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import utility.attribut.CardPosition;

/**
 * 
 * @author Eike Foede, Roah
 */
public class CardPositionComponent implements PersistentComponent {

    private CardPosition cardPos;

    public CardPositionComponent() {
    }

    public CardPositionComponent(CardPosition cardPos) {
        this.cardPos = cardPos;
    }

    public CardPosition getCardName() {
        return cardPos;
    }
}
