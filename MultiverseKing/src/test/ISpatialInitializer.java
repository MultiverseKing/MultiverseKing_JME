package test;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Eike Foede
 */
public interface ISpatialInitializer {

    public void setAssetManager(AssetManager assetManager);

    public Spatial initialize(String spatial);
}
