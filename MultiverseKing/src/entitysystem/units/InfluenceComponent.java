package entitysystem.units;

import entitysystem.ExtendedComponent;

/**
 * 
 * @author roah
 */
public class InfluenceComponent implements ExtendedComponent{
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

    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
