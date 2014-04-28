/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.input.Input;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.chainsaw.Main;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * System used to render all card on the screen.
 * @todo Render the deck.
 * @todo Render card on a better way.
 * @todo Render the opponent hand, show how many card the opponent got in hand(opposite side).
 * @todo When set in pause hide all cards. (editor only)
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    /**
     * Mainly manage all card on the current hand.
     */
    private HashMap<EntityId, Card> cards = new HashMap<EntityId, Card>();
    private Screen screen;
    private Hover hover;
    private float zOrder;

    public boolean gotCardsIsEmpty() {
        return cards.isEmpty();
    }

    public Set<EntityId> getCardsKeyset(){
        return cards.keySet();
    }

    Hover getHover() {
        return hover;
    }
    
    @Override
    protected EntitySet initialiseSystem() {
        this.screen = new Screen(app);
        app.getGuiNode().addControl(screen);
        for (Element e : screen.getElementsAsMap().values()) {
            screen.removeElement(e);
        }
        screen.getElementsAsMap().clear();
        hover = new Hover(screen);
        //We check for entity who got the CardRenderComponent
        return entityData.getEntities(CardRenderComponent.class, CardPropertiesComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        String cardName = e.get(CardRenderComponent.class).getCardName();
        Card card;
        card = new Card(screen, true, cardName, cards.size()-1, e.getId());
        cards.put(e.getId(), card);
        screen.addElement(card);
        card.resetHandPosition();
        for(Card c : cards.values()){
            c.setZOrder(c.getZOrder());
        }
    }

    //used ??
    @Override
    protected void updateEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void removeEntity(Entity e) {
        Card card = cards.get(e.getId());
        screen.removeElement(card);
        cards.remove(e.getId());
    }
    
    @Override
    protected void cleanupSystem() {
        hover.removeAllChildren();
        hover = null;
        for(Card card : cards.values()){
            screen.removeElement(card);
        }
        cards.clear();
        app.getGuiNode().removeControl(screen);
        screen = null;
    }
    
    void hasFocus(Card card) {
        zOrder = card.getZOrder();
        screen.updateZOrder(card);
        hover.setProperties(entityData.getComponent(card.getCardEntityUID(), CardPropertiesComponent.class), card.getCardName());
        card.addChild(hover);
    }

    void lostFocus(Card card) {
        hover.removeAllChildren();
        card.removeChild(hover);
        card.setZOrder(zOrder);
    }
}
