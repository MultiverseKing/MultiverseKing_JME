package org.multiverseking.loader;

import com.jme3.app.SimpleApplication;
import org.hexgridapi.utility.Vector2Int;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.multiverseking.field.Collision;
import org.multiverseking.render.RenderComponent;

/**
 *
 * @author roah
 * @deprecated handled by the vfx system
 */
public class AbilityProperties extends CardProperties {

    private final int power;
    private final int segmentCost;
    private final Vector2Int range;
    private final Collision collision;

    //@todo
    public AbilityProperties(JSONObject obj, String name, SimpleApplication app) {
//        super(obj, name, RenderComponent.RenderType.Ability);
        JSONObject data = (JSONObject) obj.get("ability");
        power = ((Number) data.get("power")).intValue();
        segmentCost = ((Number) data.get("segmentCost")).intValue();
        range = Vector2Int.fromString(data.get("activationRange").toString());

        EntityLoader eLoader = new EntityLoader(app);
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
        super(properties.getName(), properties.getVisual(), properties.getPlayCost(), properties.getRenderType(),
                properties.getRarity(), properties.getElement(), properties.getDescription());
        this.power = power;
        this.segmentCost = segmentCost;
        this.range = range;
        this.collision = collision;
    }

    public AbilityProperties(CardProperties properties, AbilityProperties ability) {
        this(properties, ability.getPower(), ability.getSegmentCost(), ability.getCastRange(), ability.getCollision());
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

    public Vector2Int getCastRange() {
        return range;
    }

    public Collision getCollision() {
        return collision;
    }
}
