package entitysystem.render;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author roah
 */
public class CharacterSpatialInitializer implements SpatialInitializer {

    private AssetManager assetManager = null;

    /**
     *
     * @param name
     * @return
     */
    public Spatial initialize(String name) {
        return assetManager.loadModel("Models/Units/" + name + ".j3o");
    }

    /**
     *
     * @param am
     */
    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
}
