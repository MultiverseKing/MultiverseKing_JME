package org.multiverseking.card;

import org.multiverseking.card.attribut.CardRenderPosition;
import org.multiverseking.loader.CardProperties;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiverseking.EntitySystemAppState;
import org.multiverseking.render.animation.Animation;
import static org.multiverseking.card.attribut.CardRenderPosition.DECK;
import static org.multiverseking.card.attribut.CardRenderPosition.FIELD;
import static org.multiverseking.card.attribut.CardRenderPosition.HAND;
import static org.multiverseking.card.attribut.CardRenderPosition.OUTERWORLD;
import org.multiverseking.render.animation.AnimationComponent;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.appstate.GridMouseControlAppState;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.TileInputListener;
import org.multiverseking.card.gui.Card;
import org.multiverseking.card.gui.Hover;
import org.multiverseking.field.CollisionSystem;
import org.multiverseking.field.component.EAttributComponent;
import org.multiverseking.loader.EntityLoader;
import org.multiverseking.loader.UnitLoader;
import org.multiverseking.render.AbstractRender.RenderType;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 * System used to render all card on the screen.
 *
 * @todo AbstractRender card on a better way.
 * @todo AbstractRender the opponent hand, show how many card the opponent got in
 * hand(opposite side).
 * @author roah
 */
public class CardRenderSystem extends EntitySystemAppState implements TileInputListener {
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
     * @todo AbstractRender it on a better way.
     */
    Window castDebug;
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
     * Save the card castDebug on preview so we can put it back if not casted,
     * otherwise we remove it.
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

        return entityData.getEntities(CardRenderComponent.class);
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
        CardRenderPosition cardPos = e.get(CardRenderComponent.class).getRenderPosition();
        if (cardPos == CardRenderPosition.HAND) {
            String cardName = e.get(CardRenderComponent.class).getName();
            Card card;
            CardProperties properties = new EntityLoader(app).loadCardProperties(cardName, e.get(CardRenderComponent.class).getRenderType());
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
                entityData.removeComponent(e.getId(), CardRenderComponent.class);
            }
        } else {
            modifyCardOnScreen(e.getId(), cardPos, new CardProperties());
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        removeCardFromScreen(e.getId());
    }

    /**
     * Remove a card corresponding to the entity without knowing the
     * CardRenderPosition of it.
     *
     * @param id
     */
    private void removeCardFromScreen(EntityId id) {
        CardRenderPosition[] screenPos = CardRenderPosition.values();
        for (CardRenderPosition cardPos : screenPos) {
            if (modifyCardOnScreen(id, cardPos, null)) {
                return;
            }
        }
    }

    /**
     * Remove/add a card corresponding to the entity when knowing the
     * CardRenderPosition of it.
     *
     * @param id
     * @param screenPos where the card is.
     * @param properties Set to null to remove a card.
     * @return true if the card have to be removed and have been removed.
     */
    private boolean modifyCardOnScreen(EntityId id, CardRenderPosition screenPos, CardProperties properties) {
        switch (screenPos) {
            case DECK:
                if (properties != null) {
                    deckCards.add(id);
                } else {
                    if (deckCards.contains(id)) {
                        deckCards.remove(id);
                        return true;
                    }
                }
                break;
            case FIELD:
                if (properties != null) {
                    fieldCards.add(id);
                } else {
                    if (fieldCards.contains(id)) {
                        fieldCards.remove(id);
                        return true;
                    }
                }
                break;
            case HAND:
                if (properties != null) {
                    Card card = new Card(screen, true, properties.getName(), handCards.size() - 1, id,
                            properties);
                    handCards.put(id, card);
                    screen.addElement(card);
                    card.resetHandPosition();
                    for (Card c : handCards.values()) {
                        c.setZOrder(c.getZOrder());
                    }
                } else {
                    if (handCards.containsKey(id)) {
                        Card card = handCards.get(id);
                        screen.removeElement(card);
                        handCards.remove(id);
                        return true;
                    }
                }
                break;
            case OUTERWORLD:
                if (properties != null) {
                    outerworldCards.add(id);
                } else {
                    if (outerworldCards.contains(id)) {
                        outerworldCards.remove(id);
                        return true;
                    }
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
    }

    /**
     * Called when the mouse is over a card.
     *
     * @param card the mouse is.
     */
    public void hasFocus(Card card) {
        zOrder = card.getZOrder();
        screen.updateZOrder(card);
        hover.setProperties(card.getProperties());
        card.addChild(hover);
    }

    /**
     * Called when a card lost the focus.
     *
     * @param card who lost the focus.
     */
    public void lostFocus(Card card) {
        hover.removeAllChildren();
        card.removeChild(hover);
        card.setZOrder(zOrder);
    }

    public void isInCastArea(Card card) {
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
        cardPreviewCast = card;
        if (activateCard(null)) {
            if (castDebug == null) {
                castDebug = new Window(screen, "CastDebug", new Vector2f(175, 155), new Vector2f(250, 20));
                castDebug.setMinDimensions(new Vector2f(200, 26));
                castDebug.setIgnoreMouse(true);
            }
            castDebug.setText("        " + card.getCardName() + " preview cast !");
            screen.addElement(castDebug);
            screen.removeElement(card);

            //Register the input for the card system
            app.getStateManager().getState(GridMouseControlAppState.class).registerTileInputListener(this);
            app.getInputManager().addListener(cardInputListener, "Cancel");
        }
    }

    private void closePreview() {
        activateCard(null);
        cardPreviewCast = null;
        screen.removeElement(screen.getElementById(castDebug.getUID()));
        //Remove the input for the card system
        app.getStateManager().getState(GridMouseControlAppState.class).removeTileInputListener(this);
        app.getInputManager().removeListener(cardInputListener);
    }

    private void castCanceled() {
        screen.addElement(cardPreviewCast);
        cardPreviewCast.setZOrder(zOrder);
        closePreview();
    }

    private void activateCard(HexCoordinate castCoord, RenderType type) {
        switch (type) {
            case Ability:
                break;
            case Core:
                break;
            case Debug:
                break;
            case Environment:
                break;
            case Equipement:
                break;
            case Titan:
                break;
            case Unit:
                CardRenderComponent cardRender = entities.getEntity(cardPreviewCast.getCardEntityUID()).get(CardRenderComponent.class);
                String name = cardRender.getName();
                UnitLoader unitLoader = new EntityLoader(app).loadUnitStats(name);
                if (unitLoader != null) {
                    /* @todo How this is suppose to work without render Comp !? */
                    entityData.setComponents(cardPreviewCast.getCardEntityUID(),
//                            new HexPositionComponent(castCoord, Rotation.A),
                            cardRender.clone(CardRenderPosition.FIELD),
                            new AnimationComponent(Animation.SUMMON),
                            new EAttributComponent(cardPreviewCast.getProperties().getElement()),
                            unitLoader.getCollisionComponent(),
                            unitLoader.getInitialStatsComponent());
                }
                break;
            default:
                throw new UnsupportedOperationException(type.name() + " isn't a supported cardType.");
        }
    }

    /**
     * Activate the HexMapInput on pulse Mode if (event == null &&
     * cardPreviewCast != null), Desactivate the HexMapInput pulseMode if (event
     * != null && cardPreview != null)
     *
     * @param event result when a leftMouse event happen on hexMap.
     */
    private boolean activateCard(MouseInputEvent event) {
        /**
         * If a card is currently in Casting Preview we check if it can be
         * casted, No card is currently activated so we switch over all card
         * type to know what preview to activate. (the entity will be removed
         * from this system automaticaly if he have to)
         */
        if (cardPreviewCast != null) {
            if (event == null) {
                /**
                 * We activate the pulse Mode, if not activated the cast is
                 * canceled.
                 */
                if (!app.getStateManager().getState(GridMouseControlAppState.class).setCursorPulseMode(this)) {
                    return false;
                }
            } else if (cardPreviewCast.getProperties().getRenderType().equals(RenderType.Titan)) {
                /**
                 * We check if the collision system is currently running, if
                 * it's not the card will be directly casted.
                 */
                CollisionSystem collisionSystem = app.getStateManager().getState(CollisionSystem.class);
                if (collisionSystem != null) {
                    if (collisionSystem.isValidPosition(event.getPosition(),
                            cardPreviewCast.getProperties().getRenderType())) {
                        activateCard(event.getPosition(), cardPreviewCast.getProperties().getRenderType());
                        closePreview();
                    } else {
                        castCanceled();
                    }
                } else {
                    activateCard(event.getPosition(), cardPreviewCast.getProperties().getRenderType());
                    closePreview();
                }
            }
        }
        return true;
    }
    private ActionListener cardInputListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Cancel") && !keyPressed) {
                castCanceled();
            }
        }
    };

    @Override
    public void onMouseAction(MouseInputEvent event) {
        if(event.getType().equals(MouseInputEventType.LMB)){
            activateCard(event);
        }
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
     * hide all card on the current System.
     *
     * @todo Extends to affect the whole system GUI.
     */
    public void hideCards() {
        showCards(null, false);
    }

    /**
     * Show all card on the current System.
     *
     * @todo Extends to affect the whole system GUI.
     */
    public void showCards() {
        showCards(null, true);
    }

    /**
     * Show/hide card on the selected position in the system, Set to null to
     * show/hide all cards.
     *
     * @param show Set to true to show, false to hide.
     * @param screenPosition
     */
    public void showCards(CardRenderPosition screenPosition, boolean show) {
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
                        if (show) {
                            card.show();
                        } else {
                            card.hide();
                        }
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
