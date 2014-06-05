package entitysystem.card;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import entitysystem.attribut.CardType;
import entitysystem.attribut.Faction;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import utility.ElementalAttribut;

/**
 * @todo generate too much object have to be solved/reduce.)
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
        /**
         * Label used to show the cost needed for the card.
         */
        Label cost = new Label(screen, "playCostHover", new Vector2f(), new Vector2f(60, 45));
        cost.setText("0");
        cost.setFont("Interface/Fonts/Vemana2000.fnt");
        cost.setFontColor(ColorRGBA.White);
        cost.setFontSize(30);
        addChild(cost);
        cost.setPosition(new Vector2f(getDimensions().x*0.75f, getDimensions().y*0.75f));
        
        /**
         * Window used to show the type of the card.
         */
        Window typeIco = new Window(this.screen, "typeIconHover", Vector2f.ZERO, 
                new Vector2f(33,30), 
                Vector4f.ZERO, "Textures/Cards/Icons/CardType/" + properties.getCardSubType().name() + ".png");
        typeIco.removeAllChildren();
        addChild(typeIco);
        typeIco.setPosition(new Vector2f(getDimensions().x*0.06f, getDimensions().y*0.8f));
        
        /**
         * Window used to show the Faction of the card.
         */
        Window factionIco = new Window(this.screen, "factionIconHover", Vector2f.ZERO, 
                new Vector2f(33,30), 
                Vector4f.ZERO, "Textures/Cards/Icons/Faction/" + properties.getFaction().name() + ".png");
        factionIco.removeAllChildren();
        addChild(factionIco);
        factionIco.setPosition(new Vector2f(getDimensions().x*0.71f, getDimensions().y*0.61f));
        
        /**
         * Label used to show the name of the card.
         */
        Label cardNameLabel = new Label(screen, "cardNameLabelHover", new Vector2f(), new Vector2f(300, 45));
        cardNameLabel.setText(cardName);
        addChild(cardNameLabel);
        
        cardNameLabel.setFont("Interface/Fonts/Purisa.fnt");
        cardNameLabel.setPosition(new Vector2f(getDimensions().x*0.2f, -10));
        cardNameLabel.setFontSize(17);
        cardNameLabel.setFontColor(ColorRGBA.White);
    }
    
    public void setFaction(Faction faction){
        if(!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("factionIconHover")){
            Element icon = getElementsAsMap().get("factionIconHover");
            icon.setColorMap("Textures/Cards/Icons/Faction/"+faction.name()+".png");
        }
    }
    
    public void setCardName(String name){
        if(!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("cardNameLabelHover")){
            Element nameLabel = getElementsAsMap().get("cardNameLabelHover");
            if(name.length() > 11){
                nameLabel.setFontSize(14);
            } else {
                nameLabel.setFontSize(17);
            }
            nameLabel.setText(name);
        }
    }
    
    public void setType(CardType type){
        if(!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("typeIconHover")){
            Element icon = getElementsAsMap().get("typeIconHover");
            icon.setColorMap("Textures/Cards/Icons/CardType/"+type.name()+".png");
        }
    }
    
    public void setCastCost(int cost){
        if(!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("playCostHover")){
            Element costLabel = getElementsAsMap().get("playCostHover");
            if (cost == 20) {
                costLabel.setPosition(new Vector2f(getDimensions().x*0.75f-6, getDimensions().y*0.75f));
            } else if (cost == 11) {
                costLabel.setPosition(new Vector2f(getDimensions().x*0.75f, getDimensions().y*0.75f));
            } else if(cost > 9){
                costLabel.setPosition(new Vector2f(getDimensions().x*0.75f-5, getDimensions().y*0.75f));
            } else {
                costLabel.setPosition(new Vector2f(getDimensions().x*0.75f, getDimensions().y*0.75f));
            }
            costLabel.setText(Integer.toString(cost));
        }
    }

    private void updateProperties(CardProperties properties, String cardName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
