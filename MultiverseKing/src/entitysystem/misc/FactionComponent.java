package entitysystem.misc;

import com.simsilica.es.PersistentComponent;
import utility.attribut.Faction;

/**
 * Can be used outside the card system, not specialy belong to it.
 * @author roah
 */
public class FactionComponent implements PersistentComponent {
    private Faction faction;

    /**
     * Create a new faction component to work with all faction related system.
     * Will be used later with the Multiverse/World/Exploration system.
     * @param faction to start with.
     */
    public FactionComponent(Faction faction) {
        this.faction = faction;
    }

    /**
     * @return the faction of this component/entity.
     */
    public Faction getFaction() {
        return faction;
    }    
}
