/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;

/**
 * Load the card geometry, card material, needed card and return the card completely generated.
 * They should have shared Material, shared Mesh, only the texture isn't shared.
 * @author roah
 */
public class CardSpatialInitializer {
    private AssetManager assetManager = null;

    public Spatial initialize(String spatial) {
        Spatial cards = assetManager.loadModel("Models/utility/cards.j3o");
        Material mat = assetManager.loadMaterial("Material/cardMaterial.j3m");
        Texture2D texture = (Texture2D) assetManager.loadAsset("Textures/Cards/"+spatial+".png");
        mat.setTexture("ColorMap", texture);
        cards.setMaterial(mat);
        cards.setName(spatial);
        
        return cards;
    }

    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
}
