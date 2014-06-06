package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 * 
 * @author roah
 */
public class InfluenceComponent implements PersistentComponent{
    private final int influenceArea;

    /**
     * Size of the influence area arround the unit where you are able to summon or control unit. 
     * @param influenceArea 
     */
    public InfluenceComponent(int influenceArea) {
        this.influenceArea = influenceArea;
    }

    /**
     * Size of the influence area arround the unit where you are able to summon or control unit.
     * @return 
     */
    public int getInfluenceArea() {
        return influenceArea;
    }
}
