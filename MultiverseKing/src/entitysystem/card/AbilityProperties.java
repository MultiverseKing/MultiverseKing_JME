package entitysystem.card;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class AbilityProperties extends CardProperties {

    private final int power;
    private final int segmentCost;
    private final Vector2Int range;
    private final HashMap<Byte, ArrayList> collision;

    public AbilityProperties(JSONObject obj, String name) {
        super(obj, name);
        JSONObject data = (JSONObject) obj.get("ability");
        power = ((Number) data.get("power")).intValue();
        segmentCost = ((Number) data.get("segmentCost")).intValue();
        range = new Vector2Int(data.get("activationRange").toString());
        
        collision = new HashMap<Byte, ArrayList>();
        JSONArray col = (JSONArray) data.get("hitCollision");
        for (Object o : col) {
            byte layer = ((Number) ((JSONObject) o).get("layer")).byteValue();
            ArrayList<Vector2Int> key = new ArrayList<Vector2Int>();

            for (Object k : ((JSONArray) ((JSONObject) o).get("key"))) {
                key.add(new Vector2Int(k.toString()));
            }
            collision.put(layer, key);
        }
    }

    public AbilityProperties(int power, int segmentCost, Vector2Int range, HashMap<Byte, ArrayList> collision) {
        super();
        this.power = power;
        this.segmentCost = segmentCost;
        this.range = range;
        this.collision = collision;
    }
    
    public AbilityProperties(CardProperties properties, int power, int segmentCost, Vector2Int range, HashMap<Byte, ArrayList> collision) {
        super(properties.getName(), properties.getPlayCost(), properties.getCardType(), 
                properties.getRarity(), properties.getElement(), properties.getDescription());
        this.power = power;
        this.segmentCost = segmentCost;
        this.range = range;
        this.collision = collision;
    }

    public AbilityProperties(CardProperties properties, AbilityProperties ability){
        this(properties, ability.getPower(), ability.getSegmentCost(), ability.getRange(), ability.getCollision());
    }
    
    public AbilityProperties() {
        super();
        this.power = 0;
        this.segmentCost = 0;
        this.range = null;
        this.collision = null;
    }

    public int getPower() {
        return power;
    }

    public int getSegmentCost() {
        return segmentCost;
    }

    public Vector2Int getRange() {
        return range;
    }

    public HashMap<Byte, ArrayList> getCollision() {
        return collision;
    }
}
