package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import entitysystem.attribut.CardRenderPosition;

/**
 * Used by the card render system, Contain where the Card should be rendered on
 * the screen.
 *
 * @author roah
 */
public class CardRenderComponent implements PersistentComponent {

    private final CardRenderPosition renderPosition;
    private final String name;

    /**
     * Define where the card will be generated/ rendered on the screen.
     *
     * @param renderPosition
     */
    public CardRenderComponent(CardRenderPosition renderPosition, String name) {
        this.name = name;
        this.renderPosition = renderPosition;
    }

    /**
     * where the card is.
     *
     * @see CardRenderPosition
     * @return
     */
    public CardRenderPosition getRenderPosition() {
        return renderPosition;
    }

    public String getName() {
        return name;
    }
    
    public CardRenderComponent clone(String name){
        return new CardRenderComponent(renderPosition, name);
    }
    
    public CardRenderComponent clone(CardRenderPosition renderPosition){
        return new CardRenderComponent(renderPosition, name);
    }
}
