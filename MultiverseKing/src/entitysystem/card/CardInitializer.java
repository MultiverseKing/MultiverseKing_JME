/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.asset.AssetManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;

/**
 * Load the card geometry, card material, needed card and return the card completely generated.
 * They should have shared Material, shared Mesh, only the texture isn't shared.
 * @author roah
 */
public class CardInitializer {
    private AssetManager assetManager = null;
    private MultiverseMain main = null;

    public Window initialize(String cardName) {
        Image img = (Image) assetManager.loadAsset("Textures/Cards/"+cardName+"_256px.png");
        Window cardWin = new Window(main.getScreen(), cardName, new Vector2f(150f, 150f), new Vector2f(150f, 400f), new Vector4f(0f, 0f, img.getHeight(), img.getWidth()), "Textures/Cards/"+cardName+"_256px.png");
        return cardWin;
    }

    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
}
