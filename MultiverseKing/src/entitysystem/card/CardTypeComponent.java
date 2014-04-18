package entitysystem.card;

import com.simsilica.es.PersistentComponent;
import entitysystem.card.attribut.CardMainType;
import entitysystem.card.attribut.CardSubType;

/**
 * Used to regroup all card type entity.
 * @author roah
 */
public class CardTypeComponent implements PersistentComponent{
    private CardMainType cardMainType;
    private CardSubType cardSubType;

    public CardTypeComponent() {
    }
    
    /**
     * Create a new card type component, throw UnsupportedOperationException if the subType didn't match the mainType.
     * WORLD subType == SPELL, SUMMON, TRAP.
     * TITAN subType == AI, EQUIPEMENT, PATHFIND.
     * @param cardMainType 
     * @param cardSubType 
     */
    public CardTypeComponent(CardMainType cardMainType, CardSubType cardSubType){
        if(cardMainType == CardMainType.WORLD) {
            if(cardSubType != CardSubType.SPELL || cardSubType != CardSubType.SUMMON || cardSubType != CardSubType.TRAP){
                throw new UnsupportedOperationException("Only SPELL, SUMMON, TRAP are supported for WORLD card.");
            } else {
                this.cardMainType = cardMainType;
                this.cardSubType = cardSubType;
            }
        } else if(cardMainType == CardMainType.TITAN) {
            if(cardSubType != CardSubType.AI || cardSubType != CardSubType.EQUIPEMENT || cardSubType != CardSubType.PATHFIND){
                throw new UnsupportedOperationException("Only AI, EQUIPEMENT, PATHFIND are supported for TITAN card.");
            } else {
                this.cardMainType = cardMainType;
                this.cardSubType = cardSubType;
            }
        }
    }
    
    public CardMainType getMainType(){
        return cardMainType;
    }
    
    public CardSubType getSubType(){
        return cardSubType;
    }
}
