/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * System used to render all card on the screen.
 * @todo Render the deck.
 * @todo Current position of the card in the hand.
 * @todo Render card on a better way, more structured.
 * @todo Render the opponent hand, show how many card the opponent got in hand(opposite side).
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    private HashMap<String, Card> cards = new HashMap<String, Card>();
    private CardInitializer cardInitializer = new CardInitializer();
    private Screen screen;
    private Window hover;
    
    public HashMap<String, Card> getCards() {
        return cards;
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
        Card card = cardInitializer.initialize(screen, e.get(CardRenderComponent.class).getCardName(), cards.size()-1);
        cards.put(e.get(CardRenderComponent.class).getCardName(), card);
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
        Card card = cards.get(e.get(CardRenderComponent.class).getCardName());
        screen.removeElement(card);
        cards.remove(e.get(CardRenderComponent.class).getCardName());
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
