package org.multiverseking.render.utility;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import org.multiverseking.render.AbstractRender.RenderType;

/**
 *
 * @author roah
 */
public class SpatialInitializer {

    private final AssetManager assetManager;

    public SpatialInitializer(AssetManager am) {
        this.assetManager = am;
    }

    public Spatial initialize(String name, RenderType type) {
        Spatial model;
        if(type.equals(RenderType.Debug)){
            model = assetManager.loadModel("org/multiverseking/assets/Models/"+type.toString()+"/" + name + ".j3o");
        } else {
            model = assetManager.loadModel("org/multiverseking/assets/Models/"+type.toString()+"/" + name + "/" + name + ".j3o");
        }
        return model;
    }
}