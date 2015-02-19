package entitysystem.card;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import entitysystem.attribut.Rarity;
import entitysystem.render.RenderComponent.RenderType;
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
        this(screen, new Vector2f(300f, 250f), new Vector2f());
    }

    public Hover(ElementManager screen, Vector2f position, Vector2f dimension) {
        super(screen, "cardPropertiesHover", position, dimension, Vector4f.ZERO,
                "Textures/Cards/backgrounds/" + Rarity.COMMON.toString().toLowerCase() + ".png");
        this.removeAllChildren();
        this.setIgnoreMouse(true);
    }

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
        setSubType(properties.getRenderType());

        /**
         * Window used to show the Faction of the card.
         */
        setEAttribut(properties.getElement());

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

    public void setEAttribut(ElementalAttribut eAttribut) {
        Element eWin;
        if (getElementsAsMap().containsKey("elementalIconHover")) {
            eWin = getElementsAsMap().get("elementalIconHover");
            eWin.setColorMap("Textures/Icons/EAttributs/" + eAttribut.name() + ".png");
            eWin.setIgnoreMouse(true);
        } else {
            eWin = new Window(this.screen, "elementalIconHover", new Vector2f(), new Vector2f(),
                    Vector4f.ZERO, "Textures/Icons/EAttributs/" + eAttribut.name() + ".png");
            eWin.removeAllChildren();
            addChild(eWin);
            eWin.setIgnoreMouse(true);
        }

        eWin.setPosition(new Vector2f(getDimensions().x * 0.72f, getDimensions().y * 0.615f));
        eWin.setDimensions(getDimensions().x * 0.2f, getDimensions().y * 0.15f);
        switch (eAttribut) {
            case EARTH:
//                eWin.setDimensions(getDimensions().x * 0.2f, getDimensions().y * 0.2f);
                break;
            case ICE:
//                eWin.setDimensions(getDimensions().x * 0.22f, getDimensions().y * 0.22f);
                break;
            case NATURE:
//                eWin.setDimensions(getDimensions().x * 0.2f, getDimensions().y * 0.2f);
                break;
            case VOLT:
//                eWin.setDimensions(getDimensions().x * 0.2f, getDimensions().y * 0.2f);
                break;
            case WATER:
//                eWin.setPosition(new Vector2f(getDimensions().x * 0.72f, getDimensions().y * 0.61f));
//                eWin.setDimensions(getDimensions().x * 0.2f, getDimensions().x * 0.2f);
                break;
            default:
                throw new UnsupportedOperationException(eAttribut + " Is not a supported type.");
        }
    }

    public void setCardName(String name) {
        if (!getElementsAsMap().isEmpty() && getElementsAsMap().containsKey("cardNameLabelHover")) {
            Element nameLabel = getElementsAsMap().get("cardNameLabelHover");
            if (name.length() < 9) {
                nameLabel.setFontSize(17);
                nameLabel.setPosition(new Vector2f(getDimensions().x * 0.18f, -10));
            } else if (name.length() > 11) {
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

    public void setSubType(RenderType type) {
        Element icon;
        if (getElementsAsMap().containsKey("typeIconHover")) {
            icon = getElementsAsMap().get("typeIconHover");
            icon.setColorMap("Textures/Icons/CardType/" + type.toString() + ".png");
        } else {
            icon = new Window(this.screen, "typeIconHover", new Vector2f(), new Vector2f(),
                    Vector4f.ZERO, "Textures/Icons/CardType/" + type.toString() + ".png");
            icon.removeAllChildren();
            addChild(icon);
            icon.setIgnoreMouse(true);
        }

        icon.setPosition(new Vector2f(getDimensions().x * 0.075f, getDimensions().y * 0.8f));
        icon.setDimensions(getDimensions().x * 0.22f, getDimensions().y * 0.15f);
        switch (type) {
            case Ability:
                break;
            case Core:
                break;
            case Debug:
                break;
            case Environment:
                break;
            case Equipement:
                break;
            case Titan:
                break;
            case Unit:
                icon.setPosition(new Vector2f(getDimensions().x * 0.079f, getDimensions().y * 0.8f));
                icon.setDimensions(getDimensions().x * 0.21f, getDimensions().y * 0.15f);
                break;
            default:
                throw new UnsupportedOperationException(type + " is not a supported type.");
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

    public void setRarity(Rarity rarity) {
        getMaterial().setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/Cards/backgrounds/" + rarity.name().toLowerCase() + ".png"));
    }

    private void updateProperties(CardProperties properties) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
