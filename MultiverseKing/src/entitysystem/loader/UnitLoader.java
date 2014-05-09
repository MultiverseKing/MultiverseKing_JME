package entitysystem.loader;

import entitysytem.units.LifeComponent;
import entitysytem.units.UnitStatsComponent;
import org.json.simple.JSONObject;

/**
 * loader to load entity units from file.
 * @author roah
 */
public class UnitLoader {
    private UnitStatsComponent uStats;
    private LifeComponent uLife;

    UnitLoader(JSONObject data) {
        Number speed = (Number) data.get("speed");
        Number movePoint = (Number) data.get("movePoint");
        Number life = (Number) data.get("life");
        
        uLife = new LifeComponent(life.intValue());
        uStats = new UnitStatsComponent(speed.floatValue(), movePoint.byteValue(), (String) data.get("ability"));
    }

    /**
     * Stats data component of the unit.
     * @return 
     */
    public UnitStatsComponent getuStats() {
        return uStats;
    }

    /**
     * Life component of the unit.
     * @return 
     */
    public LifeComponent getuLife() {
        return uLife;
    }
}
