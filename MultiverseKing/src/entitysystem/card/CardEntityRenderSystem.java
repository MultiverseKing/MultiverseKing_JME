/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
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
 * @todo When set in pause hide all cards. (editor only)
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState {

    /**
     * Mainly manage all card on the current hand.
     */
    private HashMap<EntityId, Card> cards = new HashMap<EntityId, Card>();
    boolean activeCard = false;
    private Screen screen;
    private Hover hover;
    private float zOrder;
    private Vector2f minCastArea;
    private Vector2f maxCastArea;
    /**
     * Save the card casted on preview so we can put it back if not casted,
     * in case it casted we remove it from cards.
     */
    private Card cardCastPreview;

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
        minCastArea = new Vector2f(screen.getWidth()*0.2f, screen.getHeight()*0.2f);
        maxCastArea = new Vector2f(screen.getWidth()-(screen.getWidth()*0.2f), screen.getHeight()-(screen.getHeight()*0.2f));
        app.getInputManager().addMapping("cancel",  new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addListener(actionListener,"cancel");
        //We check for entity who got the CardRenderComponent
        return entityData.getEntities(CardRenderComponent.class, CardPropertiesComponent.class);
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

    boolean isInCastArea(Card card) {
        if(screen.getMouseXY().x > minCastArea.x && screen.getMouseXY().x < maxCastArea.x && 
                screen.getMouseXY().y > minCastArea.y && screen.getMouseXY().y < maxCastArea.y){
            activeCard = true;
            castPreview(card);
            //Create a new entity and add him a spell component or idono something to make it use of the skill system.
            return true;
        } else {
            return false;
        }
    }

    /**
     * @todo Add the cast effect activation.
     * @param card 
     */
    void castPreview(Card card) {
        Window casted = new Window(screen, "CastDebug", new Vector2f(155, 175), new Vector2f(250, 20));
        casted.setMinDimensions(new Vector2f(200, 26));
        casted.setIgnoreMouse(true);
        casted.setText("        "+card.getCardName() + " preview cast !");
        screen.addElement(casted);
        screen.removeElement(card);
        cardCastPreview = card;
    }

    void castCanceled(){
        screen.removeElement(screen.getElementById("CastDebug"));
        screen.addElement(cardCastPreview);
        activeCard = false;
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (activeCard && name.equals("cancel") && !keyPressed) {
//                screen.removeElement(screen.getElementById("CastDebug"));
                castCanceled();
            }
        }
    };

    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
