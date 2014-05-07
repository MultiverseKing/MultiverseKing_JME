package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import utility.attribut.CardType;
import utility.attribut.CardSubType;
import utility.attribut.Rarity;
import utility.attribut.Faction;

/**
 * Contain the properties of the card to show up when used by the CardRendersystem,
 * @param cardSubType is used to know what to do when activated.
 * @param supply is used to know if the card can be activated.
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

    public int getLevel() {
        return supply;
    }

    public Faction getFaction() {
        return faction;
    }

    public CardType getCardMainType() {
        return cardMainType;
    }

    public CardSubType getCardSubType() {
        return cardSubType;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
