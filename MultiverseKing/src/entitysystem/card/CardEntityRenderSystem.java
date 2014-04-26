/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import java.util.Set;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * System used to render all card on the screen.
 * @todo Render the deck.
 * @todo Render card on a better way.
 * @todo Render the opponent hand, show how many card the opponent got in hand(opposite side).
 * @todo When set in pause hide all cards.
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    /**
     * All card on the current hand.
     */
    private HashMap<EntityId, Card> cards = new HashMap<EntityId, Card>();
    /**
     * list used to know wich card is duplicated.
     */
    private CardInitializer cardInitializer = new CardInitializer();
    private Screen screen;
    private Window hover;

    public boolean gotCardsIsEmpty() {
        return cards.isEmpty();
    }

    public Set<EntityId> getCardsKeyset(){
        return cards.keySet();
    }
    
    @Override
    protected EntitySet initialiseSystem() {
        this.screen = new Screen(app);
        app.getGuiNode().addControl(screen);
        for (Element e : screen.getElementsAsMap().values()) {
            screen.removeElement(e);
        }
        screen.getElementsAsMap().clear();
        hover = cardInitializer.getHover(screen);
        //We check for entity who got the CardRenderComponent
        return entityData.getEntities(CardRenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        String cardName = e.get(CardRenderComponent.class).getCardName();
        Card card;
        card = cardInitializer.initialize(screen, cardName, cards.size()-1, e.getId().toString());
        cards.put(e.getId(), card);
        screen.addElement(card);
        card.resetHandPosition();
    }

    //used ??
    @Override
    protected void updateEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        Card card = cards.get(e.getId());
        screen.removeElement(card);
        cards.remove(e.getId()); //to move
    }
    
    @Override
    protected void cleanupSystem() {
        cardInitializer = null;
        hover = null;
        for(Card card : cards.values()){
            screen.removeElement(card);
        }
        cards.clear();
        app.getGuiNode().removeControl(screen);
        screen = null;
    }

    void lastAffectedCard(Card card) {
        card.resetHandPosition();
    }

    void hasFocus(Card card) {
        card.addChild(hover);
    }

    void lostFocus(Card card) {
        card.removeChild(hover);
    }
}
