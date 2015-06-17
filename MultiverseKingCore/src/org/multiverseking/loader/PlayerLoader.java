package org.multiverseking.loader;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.json.simple.JSONObject;

/**
 *
 * @author roah
 * @deprecated using j3o
 */
public class PlayerLoader {

    private PlayerLoader() {
    }


    private static class Holder {

        private final static PlayerLoader instance = new PlayerLoader();
    }

    public static PlayerLoader getInstance(AssetManager manager) {
        if (!initialized) {
            initialize(manager);
        }
        return Holder.instance;
    }
    /**
     */
    private static boolean initialized = false;
    private static int level;
    private static String blessedTitan;
    private static HexCoordinate lastSavedPosition;

    private static void initialize(AssetManager manager) {
        JSONObject data = (JSONObject) manager.loadAsset(new AssetKey<>("Data/PlayerProperties.json"));
        level = ((Number) data.get("Level")).intValue();
        blessedTitan = (String) data.get("BlessedTitan");
        lastSavedPosition = new HexCoordinate(HexCoordinate.Coordinate.OFFSET, ((Number) data.get("SavedPosX")).intValue(), 
                ((Number) data.get("SavedPosY")).intValue());
    }

    public int getLevel() {
        return level;
    }
    
    public String getBlessedTitan() {
        return blessedTitan;
    }

    public HexCoordinate getLastSavedPosition() {
        return lastSavedPosition;
    }
}
