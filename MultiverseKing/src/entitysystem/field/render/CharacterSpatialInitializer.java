package entitysystem.field.render;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author roah
 */
public class CharacterSpatialInitializer implements SpatialInitializer {

    private AssetManager assetManager = null;

    public Spatial initialize(String name) {
        return assetManager.loadModel("Models/Units/" + name + ".j3o");
    }

    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
}
