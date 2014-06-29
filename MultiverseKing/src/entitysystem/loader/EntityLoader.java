package entitysystem.loader;

import entitysystem.card.utility.CardProperties;
import entitysystem.ability.AbilityComponent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final String path = System.getProperty("user.dir") + "/assets/Data/CardData/";
    private final JSONParser parser = new JSONParser();

    /**
     * Load an unit from a file.
     *
     * @param name of the unit to load.
     * @return loaded data or null if the unit not found.
     */
    public UnitLoader loadUnitStats(String name) {
        String loadPath = path + "Units/" + name + ".card";
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
    public TitanLoader loadTitanStats(String name){
        String loadPath = path + "Titan/" + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new TitanLoader(obj, this);
        }
        return null;
    }
    
    /**
     * Load a card from his Name, and return all of his properties parsed.
     *
     * @param cardName
     * @return null if file not found.
     */
    public CardProperties loadCardProperties(String cardName) {
        String loadPath = null;
        File[] files = new File(System.getProperty("user.dir") + "/assets/Data/CardData").listFiles();
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
        String loadPath = path + "Ability/" + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj == null) {
            return null;
        }
        ElementalAttribut eAttribut = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
        String description = obj.get("description").toString();

        JSONObject abilityData = (JSONObject) obj.get("ability");
        Number damage = (Number) abilityData.get("power");
        Number segment = (Number) abilityData.get("segmentCost");
        Number activationRange = (Number) abilityData.get("activationRange");
        HashMap<Byte, ArrayList> hitCollision = getCollision((JSONArray) abilityData.get("hitCollision"));

        return new AbilityComponent(name, activationRange.byteValue(), eAttribut,
                segment.byteValue(), damage.intValue(), hitCollision,
                (Boolean) abilityData.get("castFromSelf"), description);
    }

    public HashMap<Byte, ArrayList> getCollision(JSONArray collisionData) {
        HashMap<Byte, ArrayList> collisionList = new HashMap<Byte, ArrayList>(2);
        for (int i = 0; i < collisionData.size(); i++) {
            JSONObject value = (JSONObject) collisionData.get(i);
            Number layer = (Number) value.get("layer");
            JSONArray key = (JSONArray) value.get("key");
            collisionList.put(layer.byteValue(), new ArrayList<HexCoordinate>());
            for (int j = 0; j < key.size(); j++) {
                collisionList.get(layer.byteValue()).add(new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int((String) key.get(j))));
            }
        }
        return collisionList;
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
