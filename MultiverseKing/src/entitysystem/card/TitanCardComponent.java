package entitysystem.card;

import entitysystem.card.attribut.TitanSubCardType;
import com.simsilica.es.PersistentComponent;

/**
 * 
 * @author roah
 */
public class TitanCardComponent implements PersistentComponent {
    private TitanSubCardType titanSubCardType;

    /**
     * Create a new type and sub-type component.
     * @param type World or Titan.
     * @param subType depend on the cardType.
     */
    public TitanCardComponent(TitanSubCardType titanSubCardType) {
        this.titanSubCardType = titanSubCardType;
    }

    /**
     * @return the main type of the card.
     */
    public TitanSubCardType getSubType() {
        return titanSubCardType;
    }
}
