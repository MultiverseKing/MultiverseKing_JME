package entitysystem.render;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author Eike Foede
 */
public interface SpatialInitializer {

    /**
     *
     * @param assetManager
     */
    public void setAssetManager(AssetManager assetManager);

    /**
     *
     * @param spatial
     * @return
     */
    public Spatial initialize(String spatial);
}
