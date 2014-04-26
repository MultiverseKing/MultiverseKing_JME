/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 * Load the card geometry, card material, needed card and return the card completely generated.
 * They should have shared Material, shared Mesh, only the texture isn't shared.
 * @author roah
 */
public class CardInitializer {

    public Card initialize(Screen screen, String cardName, int handPosition, String UID) {
        Card card = new Card(screen, true, cardName, handPosition, UID);
        card.resetHandPosition();
        return card;
    }

    public Window getHover(Screen screen) {
        Window hover = new Window(screen, "hover", new Vector2f(300f, 250f), Vector2f.ZERO, Vector4f.ZERO , "Textures/Cards/cardHover.png");
        hover.getDragBar().setIsVisible(false);
        hover.setIgnoreMouse(true);
        return hover;
    }
}
