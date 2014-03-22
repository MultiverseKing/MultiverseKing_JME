package entitysystem.example;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import de.tsajar.es.render.SpatialInitializer;

/**
 *
 * @author Eike Foede
 */
public class ExampleSpatialInitialiser implements SpatialInitializer {

    AssetManager assetManager;

    public ExampleSpatialInitialiser(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public Spatial initialize(String spatial) {
        return assetManager.loadModel(spatial);
    }
}
