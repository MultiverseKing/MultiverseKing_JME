/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.asset.AssetManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 * Load the card geometry, card material, needed card and return the card completely generated.
 * They should have shared Material, shared Mesh, only the texture isn't shared.
 * @author roah
 */
public class CardInitializer {
    private AssetManager assetManager = null;
    private Screen screen = null;
    Window cardHover;

    public ButtonAdapter initialize(String cardName) {
        
        ButtonAdapter card = new ButtonAdapter(screen, cardName, new Vector2f(300f, 250f), new Vector2f(200f, 300f), Vector4f.ZERO , "Textures/Cards/"+cardName+"_256px.png"){

            @Override
            public void setHasFocus(boolean hasFocus) {
                super.setHasFocus(hasFocus);
                if(hasFocus){
                    cardHover.show();
                } else {
                    cardHover.hide();
                }
            }
            
        };

        card.removeEffect(Effect.EffectEvent.Hover);
        card.removeEffect(Effect.EffectEvent.Press);
        card.setIsResizable(false);
        card.setIsMovable(true);
        card.centerToParent();
        
        card.addChild(cardHover);
        cardHover.centerToParent();
        cardHover.hide();

        
        return card;
    }

    public void Init(AssetManager am, Screen screen) {
        this.assetManager = am;
        this.screen = screen;
        
        cardHover = new Window(screen, "cardHover", new Vector2f(300f, 250f), new Vector2f(200f, 300f), Vector4f.ZERO , "Textures/Cards/cardHover.png");
        cardHover.setIsResizable(false);
        cardHover.getDragBar().setIsVisible(false);
        cardHover.setIsMovable(false);
        cardHover.setIgnoreMouse(true);
    }

    void cleanup() {
        screen.removeElement(cardHover);
        cardHover = null;
    }
}
