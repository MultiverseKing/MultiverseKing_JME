package org.multiverseking.render.utility;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import org.multiverseking.render.AbstractRender;

/**
 *
 * @author roah
 */
public class SpatialInitializer {

    public static final String rootAssetPath = "org/multiverseking/assets";
    private final AssetManager assetManager;
    private final String folderPath;

    public SpatialInitializer(AssetManager manager, String folderPath) {
        this.assetManager = manager;
        this.folderPath = rootAssetPath + folderPath + "/";
    }

    public Spatial initialize(String name) {
        return initialize(name, null);
    }
    
    public Spatial initialize(String name, AbstractRender.RenderType type) {
        Spatial model;
        if (type != null && type.equals(AbstractRender.RenderType.Debug)) {
            model = (Spatial) assetManager.loadModel(folderPath + type.toString() + "/" + name + ".j3o");
        } else if (type != null) {
            model = (Spatial) assetManager.loadModel(folderPath + type.toString() + "/" + name + "/" + name + ".j3o");
        } else {
            model = (Spatial) assetManager.loadModel(folderPath + "/" + name + "/" + name + ".j3o");
        }
        return model;
    }
}
