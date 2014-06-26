package entitysystem.render.utility;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;

/**
 *
 * @author roah
 */
public class SpatialInitializer {

    private final AssetManager assetManager;

    public SpatialInitializer(AssetManager am) {
        this.assetManager = am;
    }

    public Spatial initialize(String name) {
        Spatial model = assetManager.loadModel("Models/Units/" + name + ".j3o");
        model.setShadowMode(RenderQueue.ShadowMode.Inherit);
        return model;
    }
}
