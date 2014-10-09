package entitysystem.loader;

import entitysystem.card.CardProperties;
import entitysystem.ability.AbilityComponent;
import entitysystem.card.AbilityProperties;
import entitysystem.field.Collision;
import entitysystem.field.Collision.CollisionData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.IOUtils;
import utility.ElementalAttribut;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 * Master to load Entity from file.
 *
 * @author roah
 */
public class EntityLoader {

    private final String path = System.getProperty("user.dir") + "/assets/Data/CardData";
    private final JSONParser parser = new JSONParser();

    /**
     * Load an unit from a file.
     *
     * @param name of the unit to load.
     * @return loaded data or null if the unit not found.
     */
    public UnitLoader loadUnitStats(String name) {
        String loadPath = path + "/Units/" + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new UnitLoader((JSONObject) obj.get("unitsStats"), this);
        }
        return null;
    }

    /**
     *
     * @param name
     * @return return null if not found.
     */
    public TitanLoader loadTitanStats(String name) {
        String loadPath = path + "/Titan/" + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new TitanLoader(obj, this);
        }
        return null;
    }

    /**
     * Save a card from his Name.
     *
     * @param cardName to save.
     * @return false if file not correctly saved.
     */
    public boolean saveCardProperties(CardProperties card, boolean override) {
        File file;
        String folder = "/" + card.getCardType().toString().toString().substring(0, 1)
                + card.getCardType().toString().toString().substring(1).toLowerCase() + "/";
        file = new File(path + folder + card.getName() + ".card");
        if (file.exists() && !file.isDirectory() && !override) {
            return false;
        } else if (file.isDirectory()) {
            return false;
        }

        JSONObject obj = new JSONObject();
        obj.put("cardType", card.getCardType().toString());
        obj.put("rarity", card.getRarity().toString());
        obj.put("eAttribut", card.getElement().toString());
        obj.put("playCost", card.getPlayCost());
        obj.put("description", card.getPlayCost());

        JSONObject typeData = new JSONObject();
        switch (card.getCardType()) {
            case ABILITY:
                typeData.put("power", ((AbilityProperties) card).getPower());
                typeData.put("castRange", ((AbilityProperties) card).getCastRange().toString());
                typeData.put("segmentCost", ((AbilityProperties) card).getSegmentCost());
                typeData.put("hitCollision", exportCollision(((AbilityProperties) card).getCollision()));
                obj.put("ability", typeData);
                break;
            case EQUIPEMENT:
                break;
            case SUMMON:
                break;
            case TITAN:
                break;
        }

        return setData(file, obj);
    }

    /**
     * Load a card from his Name, and return all of his properties parsed.
     *
     * @param cardName
     * @return null if file not found.
     */
    public CardProperties loadCardProperties(String cardName) {
        String loadPath = null;
        File[] files = new File(path).listFiles();
        continu:
        for (File f : files) {
            if (f.isDirectory()) {
                File[] subFiles = f.listFiles();
                for (File subF : subFiles) {
                    if (subF.isFile() && subF.getName().equals(cardName + ".card")) {
                        loadPath = subF.getPath();
                        break continu;
                    }
                }
            }
        }
        if (loadPath != null) {
            JSONObject obj = getData(loadPath);
            if (obj != null) {
                return new CardProperties(obj, cardName);
            }
            return null;
        } else {
            return null;
        }
    }

    public AbilityComponent loadAbility(String name) {
        if (name.equals("None")) {
            return null;
        }
        String loadPath = path + "/Ability/" + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj == null) {
            return null;
        }
        ElementalAttribut eAttribut = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
        String description = obj.get("description").toString();

        JSONObject abilityData = (JSONObject) obj.get("ability");
        Number power = (Number) abilityData.get("power");
        Number segment = (Number) abilityData.get("segmentCost");
        Collision hitCollision = importCollision((JSONArray) abilityData.get("collision"));

        return new AbilityComponent(name, new Vector2Int(abilityData.get("castRange").toString()), eAttribut,
                segment.byteValue(), power.intValue(), hitCollision, description);
    }

    public JSONArray exportCollision(Collision collision) {
        JSONArray collisionList = new JSONArray();
        for (byte b : collision.getLayers()) {
            JSONObject layer = new JSONObject();
            layer.put("layer", b);
            layer.put("areaRadius", collision.getCollisionLayer(b).getAreaRadius());
            layer.put("key", collision.getCollisionLayer(b).getCoord());
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
            ArrayList<HexCoordinate> collisionCoord = new ArrayList<HexCoordinate>();
            for (int j = 0; j < key.size(); j++) {
                collisionCoord.add(new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int((String) key.get(j))));
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
        } finally {
            return false;
        }
    }

    private JSONObject getData(String loadPath) {
        try {
            File file = new File(loadPath);
            if (!file.exists() || file.isDirectory()) {
                return null;
            }
            InputStream is = new FileInputStream(file);
            String s = new String(IOUtils.readFully(is, -1, true));
            JSONObject j = (JSONObject) parser.parse(s);
            return j;
        } catch (IOException ex) {
            Logger.getLogger(EntityLoader.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } catch (ParseException ex) {
            Logger.getLogger(EntityLoader.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
}
