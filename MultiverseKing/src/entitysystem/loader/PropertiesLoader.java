package entitysystem.loader;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author roah
 */
public class PropertiesLoader {
    private final String[] abilityList;
    private final String[] equipementList;
    private final String[] summonList;
    private final String[] titanList;
    private final String[] mapList;

    public PropertiesLoader(JSONObject data) {
        JSONObject card = (JSONObject) data.get("Card");
        
        JSONArray ability = (JSONArray) card.get("ability");
        abilityList = (String[]) ability.toArray(new String[ability.size()]);
        
        JSONArray equipements = (JSONArray)card.get("equipement");
        equipementList = (String[]) equipements.toArray(new String[equipements.size()]);
        
        JSONArray summon = (JSONArray)card.get("summon");
        summonList = (String[]) summon.toArray(new String[summon.size()]);
        
        JSONArray titan = (JSONArray)card.get("titan");
        titanList = (String[]) titan.toArray(new String[titan.size()]);
        
        JSONArray map = (JSONArray) data.get("Map");
        mapList = (String[]) map.toArray(new String[map.size()]);
    }

    public String[] getAbilityList() {
        return abilityList;
    }

    public String[] getEquipementList() {
        return equipementList;
    }

    public String[] getSummonList() {
        return summonList;
    }

    public String[] getTitanList() {
        return titanList;
    }

    public String[] getMapList() {
        return mapList;
    }
}
