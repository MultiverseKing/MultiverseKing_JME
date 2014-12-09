package entitysystem.loader;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderComponent.RenderType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author roah
 */
public class PropertiesLoader {
    private final String[] abilityList;
    private final String[] equipementList;
    private final String[] unitList;
    private final String[] titanList;
    private final String[] mapList;

    public PropertiesLoader(AssetManager assetManager) {
        JSONObject data = (JSONObject) assetManager.loadAsset(new AssetKey<>("Data/Properties.json"));
        JSONObject card = (JSONObject) data.get("Card");
        
        JSONArray ability = (JSONArray) card.get(RenderType.Ability.toString());
        abilityList = (String[]) ability.toArray(new String[ability.size()]);
        
        JSONArray equipements = (JSONArray)card.get(RenderType.Equipement.toString());
        equipementList = (String[]) equipements.toArray(new String[equipements.size()]);
        
        JSONArray summon = (JSONArray)card.get(RenderType.Unit.toString());
        unitList = (String[]) summon.toArray(new String[summon.size()]);
        
        JSONArray titan = (JSONArray)card.get(RenderType.Titan.toString());
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

    public String[] getUnitList() {
        return unitList;
    }

    public String[] getTitanList() {
        return titanList;
    }

    public String[] getMapList() {
        return mapList;
    }
}
