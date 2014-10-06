package entitysystem.card;

import entitysystem.field.Collision;
import entitysystem.loader.EntityLoader;
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
    private final Collision collision;

    public AbilityProperties(JSONObject obj, String name) {
        super(obj, name);
        JSONObject data = (JSONObject) obj.get("ability");
        power = ((Number) data.get("power")).intValue();
        segmentCost = ((Number) data.get("segmentCost")).intValue();
        range = new Vector2Int(data.get("activationRange").toString());
        
        EntityLoader eLoader = new EntityLoader();
        collision = eLoader.importCollision((JSONArray) data.get("hitCollision"));
    }

    public AbilityProperties(int power, int segmentCost, Vector2Int range, Collision collision) {
        super();
        this.power = power;
        this.segmentCost = segmentCost;
        this.range = range;
        this.collision = collision;
    }
    
    public AbilityProperties(CardProperties properties, int power, int segmentCost, Vector2Int range, Collision collision) {
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

    public Collision getCollision() {
        return collision;
    }
}
