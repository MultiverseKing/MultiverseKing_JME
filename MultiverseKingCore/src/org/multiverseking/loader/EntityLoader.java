package org.multiverseking.loader;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.coordinate.HexCoordinate.Coordinate;
import org.hexgridapi.utility.Vector2Int;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.multiverseking.ability.ActionAbility;
import org.multiverseking.field.collision.CollisionData;
import org.multiverseking.render.AbstractRender.RenderType;
import org.multiverseking.render.animation.Animation;
import org.multiverseking.utility.ElementalAttribut;

/**
 * Master to load Entity from file.
 * @todo need refactor
 * @author roah
 */
public class EntityLoader {

//    private final String path = System.getProperty("user.dir") + "/assets/Data/CardData";
    private final String path = "Data/CardData";
    private final SimpleApplication app;

    public EntityLoader(SimpleApplication app) {
        this.app = app;
    }

    SimpleApplication getApplication() {
        return app;
    }

    /**
     * Load an unit from a file.
     *
     * @param name of the unit to load.
     * @return loaded data or null if the unit not found.
     */
    public UnitLoader loadUnitStats(String name) {
        return new UnitLoader((JSONObject) getData(name, RenderType.UNIT.toString()).get(RenderType.UNIT.toString()+"Stats"), this);
    }

    /**
     *
     * @param name
     * @return return null if not found.
     */
    public TitanLoader loadTitanStats(String name) {
        return new TitanLoader(getData(name, RenderType.TITAN.toString()), this);
    }

    /**
     * Save a card from his Name.
     *
     * @param cardName to save.
     * @return false if file not correctly saved.
     */
    public boolean saveCardProperties(String type, JSONObject typeData, CardProperties card, boolean override) {
        File file;
        String folder = "/" + type + "/";
        file = new File(path + folder + card.getName() + ".card");
        if (file.exists() && !file.isDirectory() && !override) {
            return false;
        } else if (file.isDirectory()) {
            return false;
        }

        JSONObject obj = new JSONObject();
        obj.put("visual", card.getVisual());
        obj.put("rarity", card.getRarity().toString());
        obj.put("eAttribut", card.getElement().toString());
        obj.put("description", card.getDescription());

        obj.put(type, typeData);
        return setData(file, obj);
    }

    public boolean saveActionAbility(AbilityProperties abilityProperties, boolean override) {
        JSONObject data = new JSONObject();
        data.put("power", abilityProperties.getPower());
        data.put("castRange", abilityProperties.getCastRange().toString());
        data.put("cost", abilityProperties.getCost());
        data.put("animation", abilityProperties.getAnimation());
        data.put("castRange", exportCollision(abilityProperties.getCastRange()));
        data.put("effectRange", exportCollision(abilityProperties.getEffectRange()));

        return saveCardProperties("Ability", data, abilityProperties, override);
    }

    /**
     * Load a card from his Name, and return all of his properties parsed.
     *
     * @param cardName
     * @param type
     * @return null if file not found.
     */
    public CardProperties loadCardProperties(String cardName, RenderType type) {
        return new CardProperties(getData(cardName, type.toString()), cardName, type);
    }

    public ActionAbility loadActionAbility(String name) {
        if (name == null || name.equals("None")) {
            return null;
        }
        JSONObject obj = getData(name, "Ability");
        if (obj != null) {
            ElementalAttribut eAttribut = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
            String description = obj.get("description").toString();
            
            JSONObject data = (JSONObject) obj.get("ability");
            int power = ((Number) data.get("power")).intValue();
            int cost = ((Number) data.get("cost")).intValue();
            Animation animation = Animation.valueOf((String) data.get("animation"));
            int collisionLayer = ((Number) ((JSONObject) data.get("castRange")).get("layer")).intValue();
            CollisionData castRange = importCollision((JSONObject) data.get("castRange"));
            CollisionData effectRange = importCollision((JSONObject) data.get("effectRange"));

            return new ActionAbility(name, animation, power, cost, eAttribut, description, castRange, effectRange);
        }
        return null;
    }

    //@todo
    public JSONObject exportCollision(CollisionData collisionData) {
        JSONObject exportData = new JSONObject();
        exportData.put("layer", collisionData.getLayer());
        exportData.put("type", collisionData.getType());
        switch(collisionData.getType()) {
            case SELF:
                return exportData;
            case CUSTOM:
                JSONArray position = new JSONArray();
                position.addAll(Arrays.asList(collisionData.getPosition()));
                exportData.put("position", position);
                return exportData;
            default:
                exportData.put("min", collisionData.getMin());
                exportData.put("max", collisionData.getMax());
                return exportData;
        }
    }

    public CollisionData importCollision(JSONObject collisionData) {
        CollisionData.Type type = CollisionData.Type.valueOf((String)collisionData.get("type"));
        int layer = ((Number)collisionData.get("layer")).intValue();
        switch(type) {
            case SELF:
                return new CollisionData(layer);
            case CUSTOM:
                JSONArray key = (JSONArray) collisionData.get("key");
                HexCoordinate[] collisionCoord = new HexCoordinate[key.size()];
                for (int j = 0; j < key.size(); j++) {
                    collisionCoord[j] = new HexCoordinate(Coordinate.OFFSET, Vector2Int.fromString((String) key.get(j)));
                }
                return new CollisionData(layer, collisionCoord);
            default:
                return new CollisionData(layer, type, 
                        ((Number)collisionData.get("minRange")).intValue(), 
                        ((Number)collisionData.get("maxRange")).intValue());
        }
    }

    private boolean setData(File f, JSONObject obj) {
        FileWriter file;
        try {
            file = new FileWriter(f);
            try {
                file.write(obj.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                file.flush();
                file.close();
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(EntityLoader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private JSONObject getData(String name, String type) {
        return (JSONObject) app.getAssetManager().loadAsset(new AssetKey<>(getFolder(type) + name + ".card"));
    }

    private String getFolder(String type) {
        return "Data/CardData/" + type.toString() + "/";
    }
}
