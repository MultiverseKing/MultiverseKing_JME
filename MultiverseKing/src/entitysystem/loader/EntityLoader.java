package entitysystem.loader;

import entitysystem.card.CardProperties;
import entitysystem.units.ability.AbilityComponent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.misc.IOUtils;
import utility.ElementalAttribut;

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
        String loadPath = path + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new UnitLoader((JSONObject) obj.get("unitsStats"), this);
        }
        return null;
    }

    /**
     * Load the entity elemental attribut from his RenderName,
     *
     * @todo load all component for cards.
     * @param renderName
     * @return
     */
    public CardProperties loadCard(String renderName) {
        String loadPath = path + renderName + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new CardProperties(obj);
        }
        return null;
    }

    public AbilityComponent loadAbility(String name) {
        String loadPath = path + "Ability/" + name + ".card";
        JSONObject obj = getData(loadPath);
        ElementalAttribut eAttribut = ElementalAttribut.valueOf(obj.get("eAttribut").toString());

        JSONObject abilityData = (JSONObject) obj.get("ability");
        Number damage = (Number) abilityData.get("damage");
        Number segment = (Number) abilityData.get("segment");
        Number activationRange = (Number) abilityData.get("activationRange");
        Number effectSize = (Number) abilityData.get("effectSize");
        String description = abilityData.get("description").toString();
        return new AbilityComponent(name, activationRange.byteValue(),
                effectSize.byteValue(), eAttribut, segment.byteValue(), damage.intValue(), description);
    }

    private JSONObject getData(String loadPath) {
        try {
            InputStream is = new FileInputStream(new File(loadPath));
            String s = new String(IOUtils.readFully(is, -1, true));
            JSONObject j = (JSONObject) parser.parse(s);
            return j;
        } catch (IOException ex) {
            Logger.getLogger(EntityLoader.class.getName()).log(Level.SEVERE, "File not found : " + ex.toString(), ex);
        } catch (ParseException ex) {
            Logger.getLogger(EntityLoader.class.getName()).log(Level.SEVERE, "Object can't be parsed : " + ex.toString(), ex);
        }
        return null;
    }
}
