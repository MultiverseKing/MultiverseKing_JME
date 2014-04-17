/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import entitysystem.card.attribut.CardRarity;
import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class CardRarityComponent implements PersistentComponent {
    private CardRarity rarity;

    /**
     * Create a new rarity component to work with the card system.
     * @param level to start with.
     */
    public CardRarityComponent(CardRarity rarity) {
        this.rarity = rarity;
    }

    /**
     * @return the rarity of this component/card/entity.
     */
    public CardRarity getCardRarity() {
        return rarity;
    } 
}
