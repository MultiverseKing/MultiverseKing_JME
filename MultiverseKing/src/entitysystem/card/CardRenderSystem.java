package entitysystem.card;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.animation.AnimationComponent;
import entitysystem.position.HexPositionComponent;
import entitysystem.position.RotationComponent;
import entitysystem.render.RenderComponent;
import entitysytem.Units.UnitsSystem;
import gamestate.Editor.EditorAppState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;
import utility.Rotation;
import utility.attribut.Animation;
import utility.attribut.CardRenderPosition;
import utility.attribut.CardSubType;

/**
 * System used to render all card on the screen.
 *
 * @todo Render card on a better way.
 * @todo Render the opponent hand, show how many card the opponent got in
 * hand(opposite side).
 * @author roah
 */
public class CardRenderSystem extends EntitySystemAppState {

    /**
     * Screen used by the ToneGodGUI to displays cards on the screen.
     */
    private Screen screen;
    /**
     * All card on the current hand.
     */
    private HashMap<EntityId, Card> handCards = new HashMap<EntityId, Card>();
    /**
     * All card on the current field.
     */
    private ArrayList<EntityId> fieldCards = new ArrayList<EntityId>();
    /**
     * Contain cards who have been removed from the field, played, destroyed
     * from the hand or deck, etc...
     */
    private ArrayList<EntityId> outerworldCards = new ArrayList<EntityId>();
    /**
     * Displays all cards in the opponent hand (backfaced).
     */
    private ArrayList<Card> opponentHandWin = new ArrayList<Card>();
    /**
     * Contain all cards in the decks.
     */
    private ArrayList<EntityId> deckCards = new ArrayList<EntityId>();
    /**
     * Diplays the Decks.
     */
    private Window deckWin;
    /**
     * Displays lastPlayed Cards.
     */
    private Window outerworldWin;
    /**
     * Displays a list of cards on the current field.
     */
    private Window fieldWin;
    /**
     * Used to know when a card is activated, when to cast the preview card
     * effect.
     */
    private boolean activeCard = false;
    /**
     * Overlay used when a the mouse is over a card.
     */
    private Hover hover;
    /**
     * Used to put a card on from of other when mose is over it.
     */
    private float zOrder;
    /**
     * Used to know the area on the screen where to drag a card to activate it.
     */
    private Vector2f minCastArea;
    private Vector2f maxCastArea;
    /**
     * Save the card casted on preview so we can put it back if not casted, in
     * case it casted we remove it from cards.
     */
    private Card cardCastPreview;

    /**
     * Check if the player got cards in is hand.
     *
     * @return true if there is card in the player hand.
     */
    public boolean gotCardInHand() {
        return !handCards.isEmpty();
    }

    /**
     *
     * @return all cards entityId on the current player hand.
     */
    public Set<EntityId> getCardsKeyset() {
        return handCards.keySet();
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
        //** See above **//

        hover = new Hover(screen);
        minCastArea = new Vector2f(screen.getWidth() * 0.05f, screen.getHeight() * 0.2f);
        maxCastArea = new Vector2f(screen.getWidth() * 0.90f, screen.getHeight() - (screen.getHeight() * 0.2f));

        app.getInputManager().addMapping("cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addListener(actionListener, "cancel");
        app.getInputManager().addMapping("confirmed", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(actionListener, "confirmed");

        return entityData.getEntities(CardRenderComponent.class, CardPropertiesComponent.class, RenderComponent.class);
    }

    /**
     * @todo Handle Outerworld cards, show permanently a picture of the last
     * card send to the outerworld. when mouse is over the outerworld Zone last
     * cast send to the outerworld show up his stats. When clic on the area
     * cards in the outerworld show up.
     * @todo Handle Deck cards Backfaced bunch of cards, when mouse over, the
     * number of card left in show up, got a timer on it to knwo when next cards
     * will be picked up.
     * @todo Handle Field cards Permanently show a picture of the last played
     * cards (even opponent last played cards if visible) When mouse over, show
     * cards counts on the field (opponent cards is counted but only those the
     * player is able to see), when mouse over show the cards count by type and
     * by possessor. when clic on field area zone, cards who are currently on
     * the field and visible show up, when clic on one of these card, it zoom to
     * the location of the card on the field
     */
    @Override
    protected void addEntity(Entity e) {
        switch (e.get(CardRenderComponent.class).getCardPosition()) {
            case HAND:
                String cardName = e.get(RenderComponent.class).getName();
                Card card;
                card = new Card(screen, true, cardName, handCards.size() - 1, e.getId());
                handCards.put(e.getId(), card);
                screen.addElement(card);
                card.resetHandPosition();
                for (Card c : handCards.values()) {
                    c.setZOrder(c.getZOrder());
                }
                break;
            case DECK:
                deckCards.add(e.getId());
                break;
            case OUTERWORLD:
                outerworldCards.add(e.getId());
                break;
            case FIELD:
                fieldCards.add(e.getId());
                break;
        }
    }

    @Override
    protected void updateEntity(Entity e) {
        //todo//...
    }

    @Override
    protected void removeEntity(Entity e) {
        Card card = handCards.get(e.getId());
        screen.removeElement(card);
        handCards.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
        hover.removeAllChildren();
        hover = null;
        for (Card card : handCards.values()) {
            screen.removeElement(card);
        }
        handCards.clear();
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

    /**
     * @deprecated ?
     */
    private void castConfirmed(HexCoordinate castPosition) {
        /**
         * @todo the check have to be done on another way as, when a system is
         * removed and if this system is used by other system, these system know
         * about the fact that system isn't there anymore.
         */
//        if(app.getStateManager().getState(MovementSystem.class) != null 
//                && app.getStateManager().getState(EntityRenderSystem.class) != null){
//            MovementSystem mSystem = app.getStateManager().getState(MovementSystem.class).getOffsetPos();
//            EntityRenderSystem renderSystem = app.getStateManager().getState(EntityRenderSystem.class).getOffsetPos();
//            if(!mSystem.gotUnitOn(castPosition) && mSystem.isMovingOn(castPosition)){
        entityData.setComponent(cardCastPreview.getCardEntityUID(), new HexPositionComponent(castPosition));
        entityData.setComponent(cardCastPreview.getCardEntityUID(), new CardRenderComponent(CardRenderPosition.FIELD));
        entityData.setComponent(cardCastPreview.getCardEntityUID(), new RotationComponent(Rotation.A));
        entityData.setComponent(cardCastPreview.getCardEntityUID(), new AnimationComponent(Animation.SUMMON));
//            }
//        }
        cardCastPreview = null;
        setActiveCard(false);
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (activeCard && name.equals("cancel") && !keyPressed) {
                castCanceled();
            } else if (activeCard && name.equals("confirmed") && !keyPressed) {
                CardSubType cardType = entityData.getComponent(cardCastPreview.getCardEntityUID(), CardPropertiesComponent.class).getCardSubType();
                HexCoordinate castPosition = app.getStateManager().getState(EditorAppState.class).getOffsetPos();
                /**
                 * We switch over all card type and call the corresponding
                 * system for that card to be activated properly. Then we
                 * confirm the cast in this system. (the entity will be removed
                 * from this system automaticaly if he have to)
                 */
                switch (cardType) {
                    case AI:
                        //todo
                        break;
                    case EQUIPEMENT:
                        //todo
                        break;
                    case PATHFIND:
                        //todo
                        break;
                    case SPELL:
                        //todo
                        break;
                    case SUMMON:
                        app.getStateManager().getState(UnitsSystem.class).canBeSummon(castPosition, entities.getEntity(cardCastPreview.getCardEntityUID()));
                        break;
                    case TRAP:
                        //todo
                        break;
                    default:
                        throw new UnsupportedOperationException("This type isn't implemented !");
                }
                castConfirmed(castPosition);
            }
        }
    };

    private void setActiveCard(boolean isActive) {
        app.getStateManager().getState(EditorAppState.class).setActivecursor(isActive); //To change once defined correctly.
        activeCard = isActive;
        if (!isActive) {
            screen.removeElement(screen.getElementById("CastDebug"));
        }
    }

    /**
     *
     * @todo Get the mouse position to see if on the position of a cards (better
     * than the current hover ?)
     */
    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
