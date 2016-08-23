package org.multiverseking.loader;

import com.jme3.app.SimpleApplication;
import org.json.simple.JSONObject;
import org.multiverseking.field.collision.CollisionData;
import org.multiverseking.render.AbstractRender;
import org.multiverseking.render.animation.Animation;

/**
 * 
 * @author roah
 * @deprecated until the card system is implemented seem to be ovekill with entityLoader
 */
public class AbilityProperties extends CardProperties {

    private final int power;
    private final int cost;
    private final int collisionLayer;
    private final Animation animation;
    private final CollisionData castRange;
    private final CollisionData effectRange;
    
    public AbilityProperties(JSONObject obj, String name, SimpleApplication app) {
        super(obj, name, AbstractRender.RenderType.ABILITY);
        JSONObject data = (JSONObject) obj.get("ability");
        power = ((Number) data.get("power")).intValue();
        cost = ((Number) data.get("cost")).intValue();
        animation = Animation.valueOf((String) data.get("animation"));

        EntityLoader eLoader = new EntityLoader(app);
        collisionLayer = ((Number) ((JSONObject) data.get("castRange")).get("layer")).intValue();
        castRange = eLoader.importCollision((JSONObject) data.get("castRange"));
        effectRange = eLoader.importCollision((JSONObject) data.get("effectRange"));
    }

    public AbilityProperties(CardProperties properties, int power, int segmentCost, int collisionLayer,
            Animation animation, CollisionData castRange, CollisionData effectRange) {
        super(properties.getName(), properties.getVisual(), properties.getRenderType(),
                properties.getRarity(), properties.getElement(), properties.getDescription());
        this.power = power;
        this.cost = segmentCost;
        this.collisionLayer = collisionLayer;
        this.animation = animation;
        this.castRange = castRange;
        this.effectRange = effectRange;
    }

    public int getPower() {
        return power;
    }

    public int getCost() {
        return cost;
    }

    public int getCollisionLayer() {
        return collisionLayer;
    }

    public CollisionData getCastRange() {
        return castRange;
    }

    public CollisionData getEffectRange() {
        return effectRange;
    }

    public Animation getAnimation() {
        return animation;
    }
}
