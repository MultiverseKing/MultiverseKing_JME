package entitysystem.card;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import entitysystem.position.PositionComponent;
import entitysystem.position.RotationComponent;
import entitysystem.render.RenderComponent;
import gamestate.Editor.EditorAppState;
import java.util.HashMap;
import java.util.Set;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.Rotation;
import utility.attribut.CardPosition;

/**
 * System used to render all card on the screen.
 *
 * @todo Render the deck.
 * @todo Render card on a better way.
 * @todo Render the opponent hand, show how many card the opponent got in
 * hand(opposite side).
 * @todo When set in pause hide all cards. (editor only?)
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState {

    /**
     * Mainly manage all card on the current hand.
     */
    private HashMap<EntityId, Card> cards = new HashMap<EntityId, Card>();
    private boolean activeCard = false;
    private Screen screen;
    private Hover hover;
    private float zOrder;
    private Vector2f minCastArea;
    private Vector2f maxCastArea;
    /**
     * Save the card casted on preview so we can put it back if not casted, in
     * case it casted we remove it from cards.
     */
    private Card cardCastPreview;

    public boolean gotCardsIsEmpty() {
        return cards.isEmpty();
    }

    public Set<EntityId> getCardsKeyset() {
        return cards.keySet();
    }

    Hover getHover() {
        return hover;
    }

    @Override
    protected EntitySet initialiseSystem() {
        this.screen = new Screen(app);
        app.getGuiNode().addControl(screen);

        /**
         * Used to resolve the current issue with tonegod
         */
        for (Element e : screen.getElementsAsMap().values()) {
            screen.removeElement(e);
        }
        screen.getElementsAsMap().clear();
        //**

        hover = new Hover(screen);
        minCastArea = new Vector2f(screen.getWidth() * 0.2f, screen.getHeight() * 0.2f);
        maxCastArea = new Vector2f(screen.getWidth() - (screen.getWidth() * 0.2f), screen.getHeight() - (screen.getHeight() * 0.2f));

        app.getInputManager().addMapping("cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addListener(actionListener, "cancel");
        app.getInputManager().addMapping("confirmed", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(actionListener, "confirmed");
        
        return entityData.getEntities(CardPositionComponent.class, CardPropertiesComponent.class, RenderComponent.class);
    }

    @Override
    protected void addEntity(Entity e) {
        String cardName = e.get(RenderComponent.class).getName();
        Card card;
        card = new Card(screen, true, cardName, cards.size() - 1, e.getId());
        cards.put(e.getId(), card);
        screen.addElement(card);
        card.resetHandPosition();
        for (Card c : cards.values()) {
            c.setZOrder(c.getZOrder());
        }
    }

    @Override
    protected void updateEntity(Entity e) {
        //todo show the new position of the card on the screen.
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
        for (Card card : cards.values()) {
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

    void isInCastArea(Card card) {
        if (screen.getMouseXY().x > minCastArea.x && screen.getMouseXY().x < maxCastArea.x
                && screen.getMouseXY().y > minCastArea.y && screen.getMouseXY().y < maxCastArea.y) {
            activeCard = true;
            castPreview(card);
        }
    }

    /**
     * @todo Add the cast effect activation.
     * @param card
     */
    private void castPreview(Card card) {
        Window casted = new Window(screen, "CastDebug", new Vector2f(155, 175), new Vector2f(250, 20));
        casted.setMinDimensions(new Vector2f(200, 26));
        casted.setIgnoreMouse(true);
        casted.setText("        " + card.getCardName() + " preview cast !");
        screen.addElement(casted);
        screen.removeElement(card);
        cardCastPreview = card;
        setActiveCard(true);
    }

    private void castCanceled() {
        
        screen.addElement(cardCastPreview);
        setActiveCard(false);
    }

    private void castConfirmed() {
        entityData.setComponent(cardCastPreview.getCardEntityUID(), new CardPositionComponent(CardPosition.FIELD));
        entityData.setComponent(cardCastPreview.getCardEntityUID(), 
                new HexPositionComponent(app.getStateManager().getState(EditorAppState.class).getOffsetPos()));
        entityData.setComponent(cardCastPreview.getCardEntityUID(), new RotationComponent(Rotation.A));
        cardCastPreview = null;
        setActiveCard(false);
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (activeCard && name.equals("cancel") && !keyPressed) {
                castCanceled();
            } else if (activeCard && name.equals("confirmed") && !keyPressed) {
                castConfirmed();
            }
        }
    };

    private void setActiveCard(boolean isActive){
        app.getStateManager().getState(EditorAppState.class).setActivecursor(isActive); //To change once defined correctly.
        activeCard = isActive;
        if(!isActive){
            screen.removeElement(screen.getElementById("CastDebug"));
        }
    }
    
    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
