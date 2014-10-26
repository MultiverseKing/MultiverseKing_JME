package entitysystem.render.utility;

import com.jme3.asset.AssetManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import entitysystem.render.RenderComponent.Type;

/**
 *
 * @author roah
 */
public class SpatialInitializer {

    private final AssetManager assetManager;

    public SpatialInitializer(AssetManager am) {
        this.assetManager = am;
    }

    public Spatial initialize(String name, Type type) {
        Spatial model = assetManager.loadModel("Models/"+type.toString()+"/" + name + ".j3o");
        model.setShadowMode(RenderQueue.ShadowMode.Inherit);
        return model;
    }
}
