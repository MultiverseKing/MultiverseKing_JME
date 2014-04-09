package entitysystem.render;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Eike Foede
 */
public interface SpatialInitializer {
    public void setAssetManager(AssetManager assetManager);
    public Spatial initialize(String spatial);
}
