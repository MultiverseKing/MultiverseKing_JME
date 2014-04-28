package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import utility.attribut.CardType;
import utility.attribut.CardSubType;
import utility.attribut.Rarity;
import utility.attribut.Faction;

/**
 *
 * @author roah
 */
public class CardPropertiesComponent implements PersistentComponent {
    
    public CardPropertiesComponent() {
    }
    
    private int Level;
    private Faction faction;
    private CardType cardMainType;
    private CardSubType cardSubType;
    private Rarity rarity;
    
    /**
     * Create a new card type component, throw UnsupportedOperationException if the subType didn't match the mainType.
     * WORLD subType == SPELL, SUMMON, TRAP.
     * TITAN subType == AI, EQUIPEMENT, PATHFIND.
     * @param level level needed to use the card.
     * @param faction constraint use.
     * @param cardType constraint use.
     * @param subType constraint use.
     * @param rarity balance...
     */
    public CardPropertiesComponent(int level, Faction faction, CardType cardType, CardSubType subType, Rarity rarity){
        if(cardType == CardType.TITAN) {
            if(subType == CardSubType.SPELL || subType == CardSubType.SUMMON || subType == CardSubType.TRAP){
                throw new UnsupportedOperationException("Only SPELL, SUMMON, TRAP are supported for WORLD card.");
            } else {
                this.cardMainType = cardType;
                this.cardSubType = subType;
            }
        } else if(cardType == CardType.WORLD) {
            if(subType == CardSubType.AI || subType == CardSubType.EQUIPEMENT || subType == CardSubType.PATHFIND){
                throw new UnsupportedOperationException("Only AI, EQUIPEMENT, PATHFIND are supported for TITAN card.");
            } else {
                this.cardMainType = cardType;
                this.cardSubType = subType;
            }
        }
        this.Level = level;
        this.faction = faction;
        this.rarity = rarity;
    }

    public int getLevel() {
        return Level;
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
