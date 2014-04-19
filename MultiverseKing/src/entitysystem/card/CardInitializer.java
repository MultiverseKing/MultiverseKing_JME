/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture2D;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 * Load the card geometry, card material, needed card and return the card completely generated.
 * They should have shared Material, shared Mesh, only the texture isn't shared.
 * @author roah
 */
public class CardInitializer {
    private AssetManager assetManager = null;
    private Screen screen = null;

    public Window initialize(String cardName) {
        Texture2D img = (Texture2D) assetManager.loadTexture("Textures/Cards/"+cardName+"_256px.png");
        Window cardWin = new Window(screen, cardName, new Vector2f(300f, 250f), new Vector2f(200f, 300f), new Vector4f(14f, 14f, 14f, 14f), "Textures/Cards/"+cardName+"_256px.png");
        cardWin.setIsResizable(false);
        cardWin.getDragBar().setIsVisible(false);
        cardWin.setIsMovable(true);
        return cardWin;
    }

    public void Init(AssetManager am, Screen screen) {
        this.assetManager = am;
        this.screen = screen;
    }
}
