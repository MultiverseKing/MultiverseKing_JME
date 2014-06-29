package entitysystem.card.utility;

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
 * @todo generate too much object have to be solved/reduce.) tonegodgui update 
 * plan to implemented something of this order.
 * @todo The scale/position of Icon isn't correct when card is rescaled
 * @author roah
 */
public class Hover extends Window {

    /**
     * Generate a new Hover window to put on top of the card Artwork, Contain
     * the properties of the card.
     *
     * @param screen
     */
    public Hover(ElementManager screen) {
        this(screen, new Vector2f(300f, 250f), Vector2f.ZERO);
    }

    public Hover(ElementManager screen, Vector2f position, Vector2f dimension) {
        super(screen, "cardPropertiesHover", position, dimension, Vector4f.ZERO,
                "Textures/Cards/" + ElementalAttribut.NULL.name() + ".png");
        this.removeAllChildren();
        this.setIgnoreMouse(true);
    }

    /**
     * @todo
     */
    public void setProperties(CardProperties properties) {
        if (getElementsAsMap().isEmpty()) {
            initProperties(properties);
        } else {
            updateProperties(properties);
        }
    }

    private void initProperties(CardProperties properties) {
        /**
         * Label used to show the cost needed for the card.
         */
        Label cost = new Label(screen, "playCostHover", new Vector2f(), new Vector2f(60, 45));
        cost.setText("0");
        cost.setFont("Interface/Fonts/Vemana2000.fnt");
        cost.setFontColor(ColorRGBA.White);
        cost.setFontSize(30);
        addChild(cost);
        cost.setPosition(new Vector2f(getDimensions().x * 0.75f, getDimensions().y * 0.75f));
        cost.setIgnoreMouse(true);

        /**
         * Window used to show the type of the card.
         */
        Window typeIco = new Window(this.screen, "typeIconHover", Vector2f.ZERO,
                new Vector2f(33, 30),
                Vector4f.ZERO, "Textures/Cards/Icons/CardType/" + properties.getCardSubType().name() + ".png");
        typeIco.removeAllChildren();
        addChild(typeIco);
        typeIco.setPosition(new Vector2f(getDimensions().x * 0.06f, getDimensions().y * 0.8f));
        typeIco.setIgnoreMouse(true);

        /**
         * Window used to show the Faction of the card.
         */
        Window factionIco = new Window(this.screen, "factionIconHover", Vector2f.ZERO,
                new Vector2f(33, 30),
                Vector4f.ZERO, "Textures/Cards/Icons/Faction/" + properties.getFaction().name() + ".png");
        factionIco.removeAllChildren();
        addChild(factionIco);
        factionIco.setPosition(new Vector2f(getDimensions().x * 0.71f, getDimensions().y * 0.61f));
        factionIco.setIgnoreMouse(true);

        /**
         * Label used to show the name of the card.
         */
        Label cardNameLabel = new Label(screen, "cardNameLabelHover", new Vector2f(), new Vector2f(300, 45));
        cardNameLabel.setText(properties.getName());
        addChild(cardNameLabel);

        cardNameLabel.setFont("Interface/Fonts/Purisa.fnt");
        cardNameLabel.setPosition(new Vector2f(getDimensions().x * 0.18f, -10));
        cardNameLabel.setFontSize(17);
        cardNameLabel.setFontColor(ColorRGBA.White);
        cardNameLabel.setIgnoreMouse(true);
    }

    public void setFaction(Faction faction) {
        if (!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("factionIconHover")) {
            Element icon = getElementsAsMap().get("factionIconHover");
            icon.setColorMap("Textures/Cards/Icons/Faction/" + faction.name() + ".png");
            icon.setIgnoreMouse(true);
        }
    }

    public void setCardName(String name) {
        if (!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("cardNameLabelHover")) {
            Element nameLabel = getElementsAsMap().get("cardNameLabelHover");
            if (name.length() < 9) {
                nameLabel.setFontSize(17);
                nameLabel.setPosition(new Vector2f(getDimensions().x * 0.18f, -10));
            } else if(name.length() > 11){
                nameLabel.setFontSize(14);
                nameLabel.setPosition(new Vector2f(getDimensions().x * 0.14f, -10));
            } else {
                nameLabel.setFontSize(15);
                nameLabel.setPosition(new Vector2f(getDimensions().x * 0.16f, -10));
            }
            nameLabel.setText(name);
//            nameLabel.setIgnoreMouse(true);
        }
    }

    public void setSubType(CardType type) {
        if (!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("typeIconHover")) {
            Element icon = getElementsAsMap().get("typeIconHover");
            icon.setColorMap("Textures/Cards/Icons/CardType/" + type.name() + ".png");
//            icon.setIgnoreMouse(true);
        }
    }

    public void setCastCost(int cost) {
        if (!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("playCostHover")) {
            Element costLabel = getElementsAsMap().get("playCostHover");
            if (cost == 20) {
                costLabel.setPosition(new Vector2f(getDimensions().x * 0.75f - 6, getDimensions().y * 0.75f));
            } else if (cost == 11) {
                costLabel.setPosition(new Vector2f(getDimensions().x * 0.75f, getDimensions().y * 0.75f));
            } else if (cost > 9) {
                costLabel.setPosition(new Vector2f(getDimensions().x * 0.75f - 5, getDimensions().y * 0.75f));
            } else {
                costLabel.setPosition(new Vector2f(getDimensions().x * 0.75f, getDimensions().y * 0.75f));
            }
            costLabel.setText(Integer.toString(cost));
//            costLabel.setIgnoreMouse(true);
        }
    }

    public void setEAttribut(ElementalAttribut eAttribut){
        getMaterial().setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/Cards/" + eAttribut.name() + ".png"));
    }
    
    private void updateProperties(CardProperties properties) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
