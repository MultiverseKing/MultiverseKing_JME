package entitysystem.card;

import com.simsilica.es.PersistentComponent;

/**
 *TODO: Comments
 * @author Eike Foede
 */
public class CardRenderComponent implements PersistentComponent {

    private String cardName;

    public CardRenderComponent() {
    }

    public CardRenderComponent(String cardName) {
        this.cardName = cardName;
    }

    public String getCardName() {
        return cardName;
    }
}
