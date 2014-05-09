package entitysystem.card;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;

/**
 * @todo generate too much object have to be solved/reduce.
 * @see tonegodgui update it was planned to be implemented on the core GUI.
 * @author roah
 */
public class Hover extends Window {

    /**
     * Generate a new Hover window to put on top of the card Artwork,
     * Contain the properties of the card.
     * @param screen where the card is.
     */
    public Hover(ElementManager screen) {
        super(screen, "hover", new Vector2f(300f, 250f), Vector2f.ZERO, Vector4f.ZERO, "Textures/Cards/cardHover.png");
        this.removeAllChildren();
        this.setIgnoreMouse(true);
    }

    /**
     * @todo
     */
    void setProperties(CardProperties properties, String cardName) {
//        float posY = this.getPosition().y-this.getHeight()-20;
        Window level = new Window(this.screen, Vector2f.ZERO, new Vector2f(11, 17), Vector4f.ZERO, "Textures/PlatformerGUIText/Individual/" + properties.getPlayCost() + ".png");
        level.removeAllChildren();
        this.addChild(level);
        level.setPosition(new Vector2f(5, 99));
//        level.getDragBar().hide();

//        Window name = new Window(this.screen, Vector2f.ZERO, new Vector2f(this.getDimensions().x, 15));
//        name.removeAllChildren();
//        this.addChild(name);
//        name.centerToParent();
//        name.setPosition(new Vector2f(level.getPosition().x, 20));
//        name.setText(cardName);
//        name.hideWindow();
//        screen.updateZOrder(name);
////        name.getDragBar().hide();

//        setGlowing();
    }

    private void setGlowing(float tpf) {
        Window glow = new Window(this.screen, Vector2f.ZERO, new Vector2f(11, 17), Vector4f.ZERO, "Textures/Cards/cardHoverTest.png");
        glow.removeAllChildren();
        this.addChild(glow);
    }
}
