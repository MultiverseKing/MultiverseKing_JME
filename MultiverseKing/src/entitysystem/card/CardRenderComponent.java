package entitysystem.card;

import com.simsilica.es.PersistentComponent;

/**
 * 
 * @author Eike Foede, Roah
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
