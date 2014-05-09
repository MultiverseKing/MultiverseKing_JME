package entitysystem.card;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.render.RenderComponent;
import entitysytem.units.UnitsFieldSystem;
import gamestate.Editor.EditorAppState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

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
     * Save the card isCastedDebug on preview so we can put it back if not
     * isCastedDebug, in case it isCastedDebug we remove it from cards.
     */
    private Card cardPreviewCast;

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

        return entityData.getEntities(CardRenderComponent.class, RenderComponent.class);
    }

    /**
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
    @Override
    protected void addEntity(Entity e) {
        switch (e.get(CardRenderComponent.class).getCardPosition()) {
            case HAND:
                String cardName = e.get(RenderComponent.class).getName();
                Card card;
                card = new Card(screen, true, cardName, handCards.size() - 1, e.getId(), getEntityLoader().loadCard(e.get(RenderComponent.class).getName()));
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
                && screen.getMouseXY().y > minCastArea.y && screen.getMouseXY().y < maxCastArea.y) {
            castPreview(card);
        }
    }
    /**
     * Show when a card is currenty previewed, show the cardName.
     *
     * @todo Render it on a better way.
     */
    Window isCastedDebug;

    /**
     * @todo Add the cast effect activation on the field.
     * @param card
     */
    private void castPreview(Card card) {
        if (isCastedDebug == null) {
            isCastedDebug = new Window(screen, "CastDebug", new Vector2f(155, 175), new Vector2f(250, 20));
            isCastedDebug.setMinDimensions(new Vector2f(200, 26));
            isCastedDebug.setIgnoreMouse(true);
        }
        isCastedDebug.setText("        " + card.getCardName() + " preview cast !");
        screen.addElement(isCastedDebug);
        screen.removeElement(card);
        cardPreviewCast = card;
        setActiveCard(true);
    }

    private void castCanceled() {
        screen.addElement(cardPreviewCast);
        cardPreviewCast = null;
        setActiveCard(false);
        cardPreviewCast.setZOrder(zOrder);
    }

    private void castConfirmed() {
        cardPreviewCast = null;
        setActiveCard(false);
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (activeCard && name.equals("cancel") && !keyPressed) {
                castCanceled();
            } else if (activeCard && name.equals("confirmed") && !keyPressed) {
                CardProperties properties = cardPreviewCast.getProperties();
                HexCoordinate castPosition = app.getStateManager().getState(EditorAppState.class).getOffsetPos();
                /**
                 * We switch over all card main type and call the corresponding
                 * system for that card to be activated properly. Then we
                 * confirm the cast in this system. (the entity will be removed
                 * from this system automaticaly if he have to)
                 */
                switch (properties.getCardMainType()) {
                    case TITAN:
                        //todo : if the player want to use the field and not fast selection menu. TITAN card
                        break;
                    case WORLD:
                        if (app.getStateManager().getState(UnitsFieldSystem.class).canBeCast(castPosition, cardPreviewCast.getCardEntityUID(), properties)) {
                            castConfirmed();
                        } else {
                            castCanceled();
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException("This type isn't implemented or supported !");
                }
            }
        }
    };

    private void setActiveCard(boolean isActive) {
        activeCard = isActive;
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
                    app.getStateManager().getState(EditorAppState.class).setActivecursor(isActive);//to change and but on a better place
                    break;
                default:
                    throw new UnsupportedOperationException("This type isn't implemented !");
            }
        } else {
            app.getStateManager().getState(EditorAppState.class).setActivecursor(isActive);//to change and but on a better place
            screen.removeElement(screen.getElementById(isCastedDebug.getUID()));
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
