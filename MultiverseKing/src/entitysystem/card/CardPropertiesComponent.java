package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import utility.attribut.CardType;
import utility.attribut.CardSubType;
import utility.attribut.Rarity;
import utility.attribut.Faction;

/**
 * Contain the properties of the card to show up when used by the
 * CardRendersystem,
 *
 * @author roah
 */
public class CardPropertiesComponent implements PersistentComponent {

    private final int supply;
    private final Faction faction;
    private final CardType cardMainType;
    private final CardSubType cardSubType;
    private final Rarity rarity;

    /**
     * Create a new card type component, throw UnsupportedOperationException if
     * the subType didn't match the mainType. WORLD subType == SPELL, SUMMON,
     * TRAP. TITAN subType == AI, EQUIPEMENT, PATHFIND.
     *
     * @param supply level needed to use the card.
     * @param faction constraint use.
     * @param cardType constraint use.
     * @param rarity balance...
     */
    public CardPropertiesComponent(int supply, Faction faction, CardSubType cardType, Rarity rarity) {
        if (cardType == CardSubType.SPELL || cardType == CardSubType.SUMMON || cardType == CardSubType.TRAP) {
            this.cardMainType = CardType.WORLD;
        } else if (cardType == CardSubType.AI || cardType == CardSubType.EQUIPEMENT || cardType == CardSubType.PATHFIND) {
            this.cardMainType = CardType.TITAN;
        } else {
            throw new UnsupportedOperationException("This card type isn't Defined on " + this.toString());
        }
        this.cardSubType = cardType;
        this.supply = supply;
        this.faction = faction;
        this.rarity = rarity;
    }

    /**
     * SupplyRequirement is needed to use card.
     * @return SupplyRequirement needed for this card.
     */
    public int getSupplyRequirement() {
        return supply;
    }

    /**
     * Faction restriction must be meet to use the card.
     * @return Faction restriction to use this card.
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Titan card type can only be used on "Titan",
     * World card type can only be used on "Field".
     * Currently only two type are supported.
     * @return CardType the card belong too.
     */
    public CardType getCardMainType() {
        return cardMainType;
    }

    /**
     * Card subType properties.
     * @see CardSubType
     * @return
     */
    public CardSubType getCardSubType() {
        return cardSubType;
    }

    /**
     * card Rarity.
     * @see Rarity
     * @return
     */
    public Rarity getRarity() {
        return rarity;
    }
}
