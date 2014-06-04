package entitysystem.card;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import utility.ElementalAttribut;

/**
 * @todo generate too much object have to be solved/reduce.
 * @see tonegodgui update it was planned to be implemented on the core GUI.
 * @author roah
 */
public class Hover extends Window {
    
    /**
     * Generate a new Hover window to put on top of the card Artwork,
     * Contain the properties of the card.
     * @param screen
     */
    public Hover(ElementManager screen) {
        this(screen, new Vector2f(300f, 250f), Vector2f.ZERO);
    }

    public Hover(ElementManager screen, Vector2f position, Vector2f dimension) {
        super(screen, "cardPropertiesHover", position, dimension, Vector4f.ZERO,
                "Textures/Cards/"+ElementalAttribut.NULL.name()+".png");
        this.removeAllChildren();
        this.setIgnoreMouse(true);
    }
    
    /**
     * @todo
     */
    public void setProperties(CardProperties properties, String cardName) {
        if(getElementsAsMap().isEmpty()){
            initProperties(properties, cardName);
        } else {
            updateProperties(properties, cardName);
        }
    }
    
    private void initProperties(CardProperties properties, String cardName){
        Texture tex = app.getAssetManager().loadTexture("Textures/PlatformerGUIText/Individual/" + properties.getPlayCost() + ".png");
        Window level = new Window(this.screen, "playCostHover", Vector2f.ZERO, 
                new Vector2f(tex.getImage().getWidth()*0.68f, tex.getImage().getHeight()*0.68f), 
                Vector4f.ZERO, "Textures/PlatformerGUIText/Individual/" + properties.getPlayCost() + ".png");
        level.removeAllChildren();
        addChild(level);
        level.setPosition(new Vector2f(getDimensions().x*0.75f, getDimensions().y*0.79f));
//        level.scale(1.8f);
        
//        float posY = this.getPosition().y-this.getHeight()-20;
        
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
    
    /**
     * Used to store and delete the additional cost windows
     */
    Window[] costWindows = null;
    
    public void setCastCost(int cost){
        if(!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("playCostHover")){
            if(costWindows != null){
                for(Window w : costWindows){
                    if(w != null){
                        w.removeFromParent();
                    }
                }
                costWindows = null;
            }
            Window costWin = (Window) getElementsAsMap().get("playCostHover");
            if(cost > 9){
                String value = Integer.toString(cost);
                byte i = 0;
                costWindows = new Window[value.toCharArray().length];
                for(Character c : value.toCharArray()){
                    Texture tex = app.getAssetManager().loadTexture(
                            "Textures/PlatformerGUIText/Individual/" + c.toString() + ".png");
                    if(i == 0){
                        costWin.setDimensions(new Vector2f(tex.getImage().getWidth()*0.5f, tex.getImage().getHeight()*0.5f));
                        costWin.setPosition(new Vector2f(getDimensions().x*0.8f-12, getDimensions().y*0.8f));
                        costWin.getMaterial().setTexture("ColorMap", tex);
                        i++;
                    } else {
                        costWindows[i-1] = new Window(this.screen, Vector2f.ZERO, 
                                new Vector2f(tex.getImage().getWidth()*0.5f, tex.getImage().getHeight()*0.5f), 
                Vector4f.ZERO, "Textures/PlatformerGUIText/Individual/" + c.toString() + ".png");
                        costWindows[i-1].removeAllChildren();
                        addChild(costWindows[i-1]);
                        costWindows[i-1].setPosition(new Vector2f(getDimensions().x*0.8f+2, getDimensions().y*0.8f));
                        i++;
                    }
                }
            } else {
                Texture tex = app.getAssetManager().loadTexture(
                            "Textures/PlatformerGUIText/Individual/" + cost + ".png");
                costWin.setDimensions(tex.getImage().getWidth()*0.68f, tex.getImage().getHeight()*0.68f);
                costWin.setPosition(new Vector2f(getDimensions().x*0.75f, getDimensions().y*0.79f));
                costWin.getMaterial().setTexture("ColorMap", tex);
            }
        }
    }
    
    private void updateProperties(CardProperties properties, String cardName){
        getElementsAsMap().get("hoverCardCost").setColorMap("Textures/PlatformerGUIText/Individual/" + properties.getPlayCost() + ".png");
    }
    
    private void setGlowing(float tpf) {
        Window glow = new Window(this.screen, Vector2f.ZERO, new Vector2f(11, 17), Vector4f.ZERO, "Textures/Cards/cardHoverTest.png");
        glow.removeAllChildren();
        this.addChild(glow);
    }
}
