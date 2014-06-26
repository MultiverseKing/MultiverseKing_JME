package entitysystem.card.utility;

import entitysystem.attribut.SubType;
import entitysystem.attribut.Maintype;
import entitysystem.attribut.Faction;
import entitysystem.attribut.Rarity;
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
     * Used as deck requirement restriction to synergize the background story
     * and art-design to use.
     */
    private final Faction faction;
    /**
     * Used to know the outliner to use. Will be used later on to know wich card
     * can be used to populate GameWorld.
     */
    private final Maintype mainType;
    /**
     * Used to know where the card can be played etc, mainly used stats.
     */
    private final SubType cardType;
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

    // </editor-fold>
    /**
     * Create a new card type component, throw UnsupportedOperationException if
     * the subType didn't match the mainType. WORLD subType == SPELL, SUMMON,
     * TRAP. TITAN subType == AI, EQUIPEMENT, PATHFIND.
     *
     * @param playCost level needed to use the card.
     * @param faction constraint use.
     * @param cardType constraint use.
     * @param rarity balance...
     */
    public CardProperties(JSONObject obj, String name) {
        cardType = SubType.valueOf(obj.get("cardType").toString());
        if (cardType == SubType.SPELL || cardType == SubType.SUMMON || cardType == SubType.TRAP) {
            this.mainType = Maintype.WORLD;
        } else if (cardType == SubType.ABILITY || cardType == SubType.EQUIPEMENT) {
            this.mainType = Maintype.TITAN;
        } else {
            throw new UnsupportedOperationException("This card type isn't Defined on " + this.toString());
        }
        Number tmpValue = (Number) obj.get("playCost");
        this.name = name;
        playCost = tmpValue.intValue();
        faction = Faction.valueOf((String) obj.get("faction"));
        rarity = Rarity.valueOf(obj.get("rarity").toString());
        element = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
        description = (String) obj.get("description");
    }

    /**
     * Constructor used for the editor mode.
     *
     */
    public CardProperties(String name, int playCost, Faction faction, SubType cardType, Rarity rarity, ElementalAttribut element, String description) {
        if (cardType == SubType.SPELL || cardType == SubType.SUMMON || cardType == SubType.TRAP) {
            this.mainType = Maintype.WORLD;
        } else if (cardType == SubType.ABILITY || cardType == SubType.EQUIPEMENT) {
            this.mainType = Maintype.TITAN;
        } else {
            throw new UnsupportedOperationException("This card type isn't Defined on " + this.toString());
        }
        this.name = name;
        this.playCost = playCost;
        this.faction = faction;
        this.cardType = cardType;
        this.rarity = rarity;
        this.element = element;
        this.description = description;
    }
    
    /**
     * Internal use.
     */
    public CardProperties() {
        this.playCost = 0;
        this.faction = null;
        this.mainType = null;
        this.cardType = null;
        this.rarity = null;
        this.element = null;
        this.description = null;
        this.name = null;
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
     * Faction restriction must be meet to use the card.
     *
     * @return Faction restriction to use this card.
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Titan card type can only be used on "Titan", World card type can only be
     * used on "Field". Currently only two type are supported.
     *
     * @return CardMainType the card belong too.
     */
    public Maintype getCardMainType() {
        return mainType;
    }

    /**
     * Card subType properties.
     *
     * @see CardSubType
     * @return
     */
    public SubType getCardSubType() {
        return cardType;
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
    // </editor-fold>
}
