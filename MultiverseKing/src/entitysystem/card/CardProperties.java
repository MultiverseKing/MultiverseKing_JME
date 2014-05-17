package entitysystem.card;

import entitysystem.attribut.CardSubType;
import entitysystem.attribut.CardMainType;
import entitysystem.attribut.Faction;
import entitysystem.attribut.Rarity;
import entitysytem.units.EAttributComponent;
import org.json.simple.JSONObject;
import utility.ElementalAttribut;

/**
 * Contain the properties of the card to show up when used by the
 * CardRendersystem,
 *
 * @author roah
 */
public class CardProperties {

    private final int playCost;
    private final Faction faction;
    private final CardMainType cardMainType;
    private final CardSubType cardSubType;
    private final Rarity rarity;
    private final ElementalAttribut element;

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
    public CardProperties(JSONObject obj) {
        cardSubType = CardSubType.valueOf(obj.get("cardSubType").toString());
        if (cardSubType == CardSubType.SPELL || cardSubType == CardSubType.SUMMON || cardSubType == CardSubType.TRAP) {
            this.cardMainType = CardMainType.WORLD;
        } else if (cardSubType == CardSubType.ABILITY || cardSubType == CardSubType.EQUIPEMENT) {
            this.cardMainType = CardMainType.TITAN;
        } else {
            throw new UnsupportedOperationException("This card type isn't Defined on " + this.toString());
        }
        Number tmpValue = (Number) obj.get("playCost");
        playCost = tmpValue.intValue();
        faction = Faction.valueOf((String) obj.get("faction"));
        rarity = Rarity.valueOf(obj.get("rarity").toString());
        element = ElementalAttribut.valueOf(obj.get("eAttribut").toString());
    }

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
    public CardMainType getCardMainType() {
        return cardMainType;
    }

    /**
     * Card subType properties.
     *
     * @see CardSubType
     * @return
     */
    public CardSubType getCardSubType() {
        return cardSubType;
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
}
