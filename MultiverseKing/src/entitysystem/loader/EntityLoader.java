package entitysystem.loader;

import entitysystem.card.CardProperties;
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

/**
 * Master to load Entity from file.
 *
 * @author roah
 */
public class EntityLoader {

    private String path = System.getProperty("user.dir") + "/assets/Data/CardData/";
    private JSONParser parser = new JSONParser();

    /**
     * Load an unit from a file.
     * @param name of the unit to load.
     * @return loaded data or null if the unit not found.
     */
    public UnitLoader loadUnitStats(String name) {
        String loadPath = path + "Units/" + name + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new UnitLoader((JSONObject) obj.get("unitsStats"));
        }
        return null;
    }

    /**
     * Load the entity elemental attribut from his RenderName,
     * @todo load all component for cards.
     * @param renderName
     * @return 
     */
    public CardProperties loadCard(String renderName) {
        String loadPath = path + "Units/" + renderName + ".card";
        JSONObject obj = getData(loadPath);
        if (obj != null) {
            return new CardProperties(obj);
        }
        return null;
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
