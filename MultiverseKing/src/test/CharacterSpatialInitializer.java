/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import entitysystem.render.SpatialInitializer;

/**
 *
 * @author roah
 */
public class CharacterSpatialInitializer implements SpatialInitializer {

    private AssetManager assetManager = null;

    public Spatial initialize(String spatial) {
        return assetManager.loadModel("Models/Characters/"+spatial+"/"+spatial+".j3o");
    }

    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
    
}
