package entitysystem.card;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import static entitysystem.attribut.CardRenderPosition.DECK;
import static entitysystem.attribut.CardRenderPosition.FIELD;
import static entitysystem.attribut.CardRenderPosition.HAND;
import static entitysystem.attribut.CardRenderPosition.OUTERWORLD;
import entitysystem.attribut.CardType;
import entitysystem.loader.EntityLoader;
import entitysystem.field.render.RenderComponent;
import entitysystem.field.CollisionSystem;
import entitysystem.field.EAttributComponent;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.field.render.AnimationComponent;
import entitysystem.loader.UnitLoader;
import hexsystem.HexMapMouseInput;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;
import utility.Rotation;

/**
 * System used to render all card on the screen.
 *
 * @todo Render card on a better way.
 * @todo Render the opponent hand, show how many card the opponent got in
 * hand(opposite side).
 * @author roah
 */
public class CardSystem extends EntitySystemAppState implements HexMapInputListener {
    // <editor-fold defaultstate="collapsed" desc="Used Variable">

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
     * Overlay used when a the mouse is over a card.
     */
    private Hover hover;
    /**
     * Show properties of a card currenty previewed.
     *
     * @todo Render it on a better way.
     */
    Window isCastedDebug;
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
     * Save the card isCastedDebug on preview so we can put it back if not
     * isCastedDebug, in case it isCastedDebug we remove it from cards.
     */
    private Card cardPreviewCast;

    // </editor-fold>
    @Override
    protected EntitySet initialiseSystem() {
        this.screen = new Screen(app);
        app.getGuiNode().addControl(screen);

        hover = new Hover(screen);
        minCastArea = new Vector2f(screen.getWidth() * 0.05f, screen.getHeight() * 0.2f);
        maxCastArea = new Vector2f(screen.getWidth() * 0.90f, screen.getHeight() - (screen.getHeight() * 0.2f));

        return entityData.getEntities(CardRenderComponent.class, RenderComponent.class);
    }

    @Override
    protected void addEntity(Entity e) {
        addCardToScreen(e);
    }

    /**
     * Add a new card corresponding to the entity on the screen.
     *
     * @param e
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
    private void addCardToScreen(Entity e) {
        switch (e.get(CardRenderComponent.class).getCardPosition()) {
            case HAND:
                String cardName = e.get(RenderComponent.class).getName();
                Card card;
                CardProperties properties = new EntityLoader().loadCardProperties(cardName);
                if (properties != null) {
                    card = new Card(screen, true, cardName, handCards.size() - 1, e.getId(),
                            properties);
                    handCards.put(e.getId(), card);
                    screen.addElement(card);
                    card.resetHandPosition();
                    for (Card c : handCards.values()) {
                        c.setZOrder(c.getZOrder());
                    }
                } else {
                    System.err.println("Card files cannot be locate. " + cardName);
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
    protected void removeEntity(Entity e) {
        removeCardFromScreen(e.getId(), e.get(CardRenderComponent.class).getCardPosition());
    }

    /**
     * Remove a card corresponding to the entity without knowing the
     * CardRenderPosition of it.
     *
     * @param id
     */
    private void removeCardFromScreen(EntityId id) {
        CardRenderPosition[] screenPos = CardRenderPosition.values();
        for (CardRenderPosition p : screenPos) {
            if (removeCardFromScreen(id, p)) {
                return;
            }
        }
    }

    /**
     * Remove a card corresponding to the entity when knowing the
     * CardRenderPosition of it.
     *
     * @param id
     * @param screenPos
     * @return true if the card have been removed.
     */
    private boolean removeCardFromScreen(EntityId id, CardRenderPosition screenPos) {
        switch (screenPos) {
            case DECK:
                if (deckCards.contains(id)) {
                    deckCards.remove(id);
                    return true;
                }
                break;
            case FIELD:
                if (fieldCards.contains(id)) {
                    fieldCards.remove(id);
                    return true;
                }
                break;
            case HAND:
                if (handCards.containsKey(id)) {
                    Card card = handCards.get(id);
                    screen.removeElement(card);
                    handCards.remove(id);
                    return true;
                }
                break;
            case OUTERWORLD:
                if (outerworldCards.contains(id)) {
                    outerworldCards.remove(id);
                    return true;
                }
                break;
            default:
                throw new UnsupportedOperationException(screenPos
                        + " CardPosition isn't supported by " + this.getClass().getName());
        }
        return false;
    }

    @Override
    protected void updateEntity(Entity e) {
        removeCardFromScreen(e.getId());
        addCardToScreen(e);
    }

    /**
     * @todo Get the mouse position to see if on the position of a cards (better
     * than the current hover ?)
     */
    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Called when the mouse is over a card.
     *
     * @param card the mouse is.
     */
    void hasFocus(Card card) {
        zOrder = card.getZOrder();
        screen.updateZOrder(card);
        hover.setProperties(card.getProperties(), card.getCardName());
        card.addChild(hover);
    }

    /**
     * Called when a card lost the focus.
     *
     * @param card who lost the focus.
     */
    void lostFocus(Card card) {
        hover.removeAllChildren();
        card.removeChild(hover);
        card.setZOrder(zOrder);
    }

    void isInCastArea(Card card) {
        if (screen.getMouseXY().x > minCastArea.x && screen.getMouseXY().x < maxCastArea.x
                && screen.getMouseXY().y > minCastArea.y && screen.getMouseXY().y < maxCastArea.y
                && cardPreviewCast == null) {
            castPreview(card);
        }
    }

    /**
     * @todo Add the cast effect activation on the field.
     * @param card
     */
    private void castPreview(Card card) {
        if (isCastedDebug == null) {
            isCastedDebug = new Window(screen, "CastDebug", new Vector2f(155, 155), new Vector2f(250, 20));
            isCastedDebug.setMinDimensions(new Vector2f(200, 26));
            isCastedDebug.setIgnoreMouse(true);
        }
        isCastedDebug.setText("        " + card.getCardName() + " preview cast !");
        screen.addElement(isCastedDebug);
        screen.removeElement(card);
        cardPreviewCast = card;
        setActiveCard(true);

        //Register the input for the card system
        app.getStateManager().getState(HexMapMouseInput.class).registerTileInputListener(this);
        app.getInputManager().addListener(cardInputListener, "Cancel");
    }

    private void castCanceled() {
        screen.addElement(cardPreviewCast);
        setActiveCard(false);
        cardPreviewCast.setZOrder(zOrder);
        cardPreviewCast = null;
        //Remove the input for the card system
        app.getStateManager().getState(HexMapMouseInput.class).removeTileInputListener(this);
        app.getInputManager().removeListener(cardInputListener);
    }

    private void castConfirmed() {
        cardPreviewCast = null;
        setActiveCard(false);
        //Remove the input for the card system
        app.getStateManager().getState(HexMapMouseInput.class).removeTileInputListener(this);
        app.getInputManager().removeListener(cardInputListener);
    }

    private void initializeCastedCard(HexCoordinate castCoord, CardType type) {
        String name = entityData.getComponent(cardPreviewCast.getCardEntityUID(), RenderComponent.class).getName();
        UnitLoader unitLoader = new EntityLoader().loadUnitStats(name);
        if (unitLoader != null) {
            entityData.setComponents(cardPreviewCast.getCardEntityUID(),
                    new HexPositionComponent(castCoord, Rotation.A),
                    new CardRenderComponent(CardRenderPosition.FIELD),
                    new AnimationComponent(Animation.SUMMON),
                    new EAttributComponent(cardPreviewCast.getProperties().getElement()),
                    unitLoader.getCollisionComp(), //Collision Comp
                    unitLoader.getuLife(), //life component
                    unitLoader.getuStats(), //stats component
                    unitLoader.getAbilityComp());
        }
    }

    private void setActiveCard(boolean isActive) {
        if (isActive && cardPreviewCast != null) {

            /**
             * We switch over all card Main type to know what preview to
             * activate.
             */
            switch (cardPreviewCast.getProperties().getCardMainType()) {
                case TITAN:
                    //todo
                    break;
                case WORLD:
                    app.getStateManager().getState(HexMapMouseInput.class).setCursorOnPulseMode(isActive, this);
                    break;
                default:
                    throw new UnsupportedOperationException("This type isn't implemented !");
            }
        } else {
            app.getStateManager().getState(HexMapMouseInput.class).setCursorOnPulseMode(isActive, this);
            screen.removeElement(screen.getElementById(isCastedDebug.getUID()));
        }
    }
    private ActionListener cardInputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Cancel") && !keyPressed) {
                castCanceled();
            }
        }
    };

    public void leftMouseActionResult(HexMapInputEvent event) {
        //If a card is currently in Casting Preview we check his type then the collision before casting it
        /**
         * If a card is currently in Casting Preview we check his Main Type,
         * then we check if it can be casted and finaly we activate that card
         * properly. (the entity will be removed from this system automaticaly
         * if he have to)
         */
        if (cardPreviewCast != null) {
            switch (cardPreviewCast.getProperties().getCardMainType()) {
                case TITAN:
                    //todo : if the player want to use the field and not fast selection menu. TITAN card
                    break;
                case WORLD:
                    /**
                     * We check if the collision system is currently running, if
                     * it's not the card will be directly casted.
                     */
                    CollisionSystem collisionSystem = app.getStateManager().getState(CollisionSystem.class);
                    if (collisionSystem != null) {
                        if (collisionSystem.canBeCast(event.getEventPosition(),
                                cardPreviewCast.getProperties().getCardSubType())) {
                            initializeCastedCard(event.getEventPosition(), cardPreviewCast.getProperties().getCardSubType());
                            castConfirmed();
                        } else {
                            castCanceled();
                        }
                    } else {
                        initializeCastedCard(event.getEventPosition(), cardPreviewCast.getProperties().getCardSubType());
                        castConfirmed();
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("This type isn't implemented or supported !");
            }
        }
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        //Unused
    }

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

    public void hideCards() {
        hideCards(null);
    }

    /**
     * Hide card on the selected position in the system, Set to null to hide all
     * cards.
     *
     * @param screenPosition
     */
    public void hideCards(CardRenderPosition screenPosition) {
        if (screenPosition == null) {
            if (!handCards.isEmpty()) {
                for (Card card : handCards.values()) {
                    card.hide();
                }
            }
            /**
             * @todo: other position.
             */
            return;
        }
        switch (screenPosition) {
            case DECK:
                /**
                 * @todo
                 */
                break;
            case FIELD:
                /**
                 * @todo
                 */
                break;
            case HAND:
                if (!handCards.isEmpty()) {
                    for (Card card : handCards.values()) {
                        card.hide();
                    }
                }
                break;
            case OUTERWORLD:
                /**
                 * @todo
                 */
                break;
        }
    }

    public void showCards() {
        showCards(null);
    }

    /**
     * Show card on the selected position in the system, Set to null to show all
     * cards.
     *
     * @param screenPosition
     */
    public void showCards(CardRenderPosition screenPosition) {
        if (screenPosition == null) {
            if (!handCards.isEmpty()) {
                for (Card card : handCards.values()) {
                    card.show();
                }
            }
            /**
             * @todo: other position.
             */
            return;
        }
        switch (screenPosition) {
            case DECK:
                /**
                 * @todo
                 */
                break;
            case FIELD:
                /**
                 * @todo
                 */
                break;
            case HAND:
                if (!handCards.isEmpty()) {
                    for (Card card : handCards.values()) {
                        card.show();
                    }
                }
                break;
            case OUTERWORLD:
                /**
                 * @todo
                 */
                break;
        }
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
}
