package org.multiverseking.loader;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.multiverseking.render.RenderComponent;

/**
 *
 * @author roah
 */
public class GameProperties {

    private GameProperties() {
    }

    private static class Holder {

        private final static GameProperties instance = new GameProperties();
    }

    public static void setUpdate(){
        update = true;
    }
    
    public static GameProperties getInstance(AssetManager assetManager) {
        if(update){
            GameProperties.update(assetManager);
            update = false;
        }
        return Holder.instance;
    }
    /**
     * index : 0 = ability, 1 = equipement, 2 = unit, 3 = titan, 4 = map.
     */
    private static String[][] dataList = new String[5][];
    private static boolean update = true;

    private static void update(AssetManager assetManager) {
        JSONObject data = (JSONObject) assetManager.loadAsset(new AssetKey<>("Data/GameProperties.json"));
        JSONObject card = (JSONObject) data.get("Card");

        // @todo
//        updateList((byte) 0, (JSONArray) card.get(RenderComponent.RenderType.Ability.toString()));
//        updateList((byte) 1, (JSONArray) card.get(RenderComponent.RenderType.Equipement.toString()));
        updateList((byte) 2, (JSONArray) card.get(RenderComponent.RenderType.UNIT.toString()));
        updateList((byte) 3, (JSONArray) card.get(RenderComponent.RenderType.TITAN.toString()));
        updateList((byte) 4, (JSONArray) data.get("Map"));
    }

    private static void updateList(byte inspectedList, JSONArray list) {
        if (dataList[inspectedList] == null || dataList[inspectedList].length != list.size()) {
            dataList[inspectedList] = (String[]) list.toArray(new String[list.size()]);
        } else {
            for (byte i = 0; i < dataList[0].length; i++) {
                if (!dataList[inspectedList][i].equals((String) list.get(i))) {
                    dataList[inspectedList][i] = (String) list.get(i);
                }
            }
        }
    }

    public String[] getAbilityList() {
        return dataList[0];
    }

    public String[] getEquipementList() {
        return dataList[1];
    }

    public String[] getUnitList() {
        return dataList[2];
    }

    public String[] getTitanList() {
        return dataList[3];
    }

    public String[] getMapList() {
        return dataList[4];
    }
}
