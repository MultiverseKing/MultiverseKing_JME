package test;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import entitysystem.render.SpatialInitializer;

/**
 *
 * @author Eike Foede
 */
public class ExampleSpatialInitialiser implements SpatialInitializer {

    AssetManager assetManager;

    public Spatial initialize(String name) {
        return assetManager.loadModel(name);
    }

    public void setAssetManager(AssetManager assetManager) {

        this.assetManager = assetManager;
    }
}
