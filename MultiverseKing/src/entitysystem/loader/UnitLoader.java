package entitysystem.loader;

import entitysystem.units.ability.AbilityComponent;
import entitysystem.units.LifeComponent;
import entitysystem.movement.MovementStatsComponent;
import entitysystem.units.CollisionComponent;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 * loader to load entity units from file.
 *
 * @author roah
 */
public class UnitLoader {

    private final MovementStatsComponent uStats;
    private final LifeComponent uLife;
    private final AbilityComponent abilityComp;
    private final CollisionComponent collision;

    UnitLoader(JSONObject data, EntityLoader eLoader) {
        Number speed = (Number) data.get("speed");
        Number movePoint = (Number) data.get("movePoint");
        Number life = (Number) data.get("life");

        uLife = new LifeComponent(life.intValue());
        uStats = new MovementStatsComponent(speed.floatValue(), movePoint.byteValue());
        abilityComp = eLoader.loadAbility(data.get("ability").toString());

        JSONObject collisionObj = (JSONObject) data.get("collision");
        JSONArray keyList = (JSONArray) collisionObj.get("key");
        JSONArray keyLayer = (JSONArray) collisionObj.get("layer");
        HashMap<HexCoordinate, Byte> collisionMap = new HashMap<HexCoordinate, Byte>();
        for (int i = 0; i < keyList.size(); i++) {
            Number layer = (Number) keyLayer.get(i);
            collisionMap.put(new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int((String) keyList.get(i))),
                    layer.byteValue());
        }
        this.collision = new CollisionComponent(collisionMap);
    }

    public CollisionComponent getCollisionComp() {
        return collision;
    }

    /**
     * Stats data component of the unit.
     *
     * @return
     */
    public MovementStatsComponent getuStats() {
        return uStats;
    }

    /**
     * Life component of the unit.
     *
     * @return
     */
    public LifeComponent getuLife() {
        return uLife;
    }

    /**
     * Ability component the unit have.
     *
     * @return
     */
    public AbilityComponent getAbilityComp() {
        return abilityComp;
    }
}
