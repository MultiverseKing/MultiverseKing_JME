package entitysytem.units;

/**
 * 
 * @author roah
 */
public class InfluenceComponent {
    private int influenceArea;

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
