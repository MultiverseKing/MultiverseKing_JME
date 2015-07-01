package org.multiverseking.loader;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetKey;
import org.multiverseking.ability.AbilityComponent;
import org.multiverseking.field.Collision;
import org.multiverseking.field.Collision.CollisionData;
import org.multiverseking.render.AbstractRender.RenderType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.coordinate.HexCoordinate.Coordinate;
import org.hexgridapi.utility.Vector2Int;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.multiverseking.utility.ElementalAttribut;

/**
 * Master to load Entity from file.
 *
 * @author roah
 * @deprecated use j3o
 */
public class EntityLoader {

//    private final String path = System.getProperty("user.dir") + "/assets/Data/CardData";
    private final String path = "/assets/Data/CardData";
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
        return new UnitLoader((JSONObject) getData(name, RenderType.Unit).get(RenderType.Unit.toString()+"Stats"), this);
    }

    /**
     *
     * @param name
     * @return return null if not found.
     */
    public TitanLoader loadTitanStats(String name) {
        return new TitanLoader(getData(name, RenderType.Titan), this);
    }

    /**
     * Save a card from his Name.
     *
     * @param cardName to save.
     * @return false if file not correctly saved.
     */
    public boolean saveCardProperties(RenderType type, JSONObject typeData, CardProperties card, boolean override) {
        File file;
        String folder = "/" + card.getRenderType().toString() + "/";
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
        obj.put("playCost", card.getPlayCost());
        obj.put("description", card.getDescription());

        obj.put(type.toString(), typeData);
        return setData(file, obj);
    }

    public boolean saveAbility(AbilityProperties abilityProperties, boolean override) {
        JSONObject typeData = new JSONObject();
        typeData.put("power", abilityProperties.getPower());
        typeData.put("castRange", abilityProperties.getCastRange().toString());
        typeData.put("segmentCost", abilityProperties.getSegmentCost());
        typeData.put("collision", exportCollision(abilityProperties.getCollision()));

        return saveCardProperties(RenderType.Ability, typeData, abilityProperties, override);
    }

    /**
     * Load a card from his Name, and return all of his properties parsed.
     *
     * @param cardName
     * @return null if file not found.
     */
    public CardProperties loadCardProperties(String cardName, RenderType type) {
        return new CardProperties(getData(cardName, type), cardName, type);
    }

    public AbilityComponent loadAbility(String name) {
        if (name.equals("None")) {
            return null;
        }
        JSONObject obj = getData(name, RenderType.Ability);
        if (obj != null) {
            ElementalAttribut eAttribut = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
            String description = obj.get("description").toString();

            JSONObject abilityData = (JSONObject) obj.get(RenderType.Ability.toString());
            Number power = (Number) abilityData.get("power");
            Number segment = (Number) abilityData.get("segmentCost");
            Collision hitCollision = importCollision((JSONArray) abilityData.get("collision"));

            return new AbilityComponent(name, Vector2Int.fromString(abilityData.get("castRange").toString()), eAttribut,
                    segment.byteValue(), power.intValue(), hitCollision, description);
        }
        return null;
    }

    public JSONArray exportCollision(Collision collision) {
        JSONArray collisionList = new JSONArray();
        for (byte b : collision.getLayers()) {
            JSONObject layer = new JSONObject();
            layer.put("layer", b);
            layer.put("areaRadius", collision.getCollisionLayer(b).getAreaRadius());
            JSONArray key = new JSONArray();
            for (HexCoordinate coord : collision.getCollisionLayer(b).getCoord()) {
                key.add(coord.toOffset().toString());
            }
            layer.put("key", key);
            collisionList.add(layer);
        }
        return collisionList;
    }

    public Collision importCollision(JSONArray collisionData) {
        Collision collision = new Collision();
        for (int i = 0; i < collisionData.size(); i++) {
            JSONObject value = (JSONObject) collisionData.get(i);
            Number layer = (Number) value.get("layer");
            Number areaRange = (Number) value.get("areaRadius");
            JSONArray key = (JSONArray) value.get("key");
            ArrayList<HexCoordinate> collisionCoord = new ArrayList<>();
            for (int j = 0; j < key.size(); j++) {
                collisionCoord.add(new HexCoordinate(Coordinate.OFFSET, Vector2Int.fromString((String) key.get(j))));
            }
            collision.addLayer(layer.byteValue(), collision.new CollisionData(areaRange.byteValue(), collisionCoord));
        }
        return collision;
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

    private JSONObject getData(String name, RenderType type) {
        return (JSONObject) app.getAssetManager().loadAsset(new AssetKey<>(getFolder(type) + name + ".card"));
    }

    private String getFolder(RenderType type) {
        return "org/multiverseking/assets/Data/CardData/" + type.toString() + "/";
    }
}
