package entitysystem.card;

import entitysystem.attribut.Rarity;
import entitysystem.render.RenderComponent.RenderType;
import org.json.simple.JSONObject;
import utility.ElementalAttribut;

/**
 * Contain the properties of the card to show up when used by the
 * CardRendersystem,
 *
 * @author roah
 */
public class CardProperties {
    // <editor-fold defaultstate="collapsed" desc="Used Variable">

    /**
     * Used to know how much ressource is needed to play the card.
     */
    private final int playCost;
    /**
     * Used to know where the card can be played etc, mainly used stats.
     */
    private final RenderType renderType;
    /**
     * Used to know the amount of time the player can have/play this card.
     * Balance stats mainly.
     */
    private final Rarity rarity;
    /**
     * Used for the elemental interaction.
     */
    private final ElementalAttribut element;
    /**
     * Used to show the card description text.
     */
    private final String description;
    /**
     * Used to show the card name.
     */
    private final String name;
    /**
     * Used to know the img to load for the card.
     */
    private final String visual;

    // </editor-fold>
    /**
     * Create a new card type component.
     *
     * @param playCost level needed to use the card.
     * @param faction constraint use.
     * @param cardType constraint use.
     * @param rarity balance...
     */
    public CardProperties(JSONObject obj, String name, RenderType renderType) {
        this.name = name;
        this.renderType = renderType;
        if(renderType == RenderType.Titan){
            playCost = 0;
        } else {
            Number tmpValue = (Number) obj.get("playCost");
            playCost = tmpValue.intValue();
        }
        visual = (String) obj.get("visual");
        rarity = Rarity.valueOf(obj.get("rarity").toString());
        element = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
        description = (String) obj.get("description");
    }

    /**
     * Constructor used for the editor mode.
     */
    public CardProperties(String name, String visual, int playCost, RenderType renderType, Rarity rarity, ElementalAttribut element, String description) {
        if(renderType == RenderType.Titan){
            this.playCost = 0;
        } else {
            this.playCost = playCost;
        }
        this.name = name;
        this.renderType = renderType;
        this.rarity = rarity;
        this.element = element;
        this.description = description;
        this.visual = visual;
    }
    
    /**
     * Internal use.
     */
    public CardProperties() {
        this.playCost = 0;
        this.renderType = null;
        this.rarity = null;
        this.element = null;
        this.description = null;
        this.name = null;
        this.visual = null;
    }

    // <editor-fold defaultstate="collapsed" desc="Getter">
    /**
     * Play Cost to use the card.
     *
     * @return playCost needed for this card.
     */
    public int getPlayCost() {
        return playCost;
    }

    /**
     * Card subType properties.
     *
     * @see CardSubType
     * @return
     */
    public RenderType getRenderType() {
        return renderType;
    }

    /**
     * card Rarity.
     *
     * @see Rarity
     * @return
     */
    public Rarity getRarity() {
        return rarity;
    }

    /**
     * The element this card entity belong to.
     *
     * @return
     */
    public ElementalAttribut getElement() {
        return element;
    }

    /**
     * The description Text belong to this card.
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * The name to use for this card.
     *
     * @return
     */
    public String getName() {
        return name;
    }
    /**
     * The card img texture to use for this card.
     *
     * @return
     */
    public String getVisual() {
        return visual;
    }
    // </editor-fold>
}
