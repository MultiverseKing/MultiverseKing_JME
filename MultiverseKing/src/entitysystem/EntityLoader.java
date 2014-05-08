package entitysystem;

import entitysytem.Units.UnitStatsComponent;
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
 *
 * @author roah
 */
public class EntityLoader {

    private String path = System.getProperty("user.dir") + "/assets/Data/CardData/";
    private JSONParser parser = new JSONParser();

    public UnitStatsComponent loadUnitStats(String name) {
        String loadPath = path + "Units/" + name + ".card";
        JSONObject obj = getData(loadPath);
        ElementalAttribut element = (ElementalAttribut) obj.get("element");
        obj = (JSONObject) obj.get("unitsStats");

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
