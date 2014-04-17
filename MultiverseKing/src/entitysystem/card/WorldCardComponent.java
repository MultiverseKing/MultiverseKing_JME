package entitysystem.card;

import entitysystem.card.attribut.WorldSubCardType;
import com.simsilica.es.PersistentComponent;

/**
 * 
 * @author roah
 */
public class WorldCardComponent implements PersistentComponent {
    private WorldSubCardType worldSubCardType;

    /**
     * Create a new type and sub-type card component.
     * @param subType 
     */
    public WorldCardComponent(WorldSubCardType worldSubCardType) {
        this.worldSubCardType = worldSubCardType;
    }

    /**
     * @return the subType of the card.
     */
    public WorldSubCardType getSubType() {
        return worldSubCardType;
    }
}
