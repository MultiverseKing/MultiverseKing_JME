package entitysystem.render;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author roah
 */
public class VFXSpatialInitializer implements SpatialInitializer {

    private AssetManager assetManager = null;

    public Spatial initialize(String name) {
        return assetManager.loadModel("Scenes/VFX/" + name + ".j3o");
    }

    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
}
