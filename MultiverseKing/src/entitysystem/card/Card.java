package entitysystem.card;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.simsilica.es.EntityId;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.effects.Effect;

/**
 * Card show on the screen.
 *
 * @author roah
 */
public class Card extends ButtonAdapter {

    private final String cardName;
    private final EntityId UID;
    private final float rescaleValue;
    private final Vector2f cardSize;
    private int isRescale = 1;
    private int handPosition;
    private CardProperties cardProperties;

    /**
     * The name of this cards.
     */
    String getCardName() {
        return cardName;
    }

    /**
     * EntityId this card belong to.
     */
    EntityId getCardEntityUID() {
        return this.UID;
    }

    public CardProperties getProperties() {
        return cardProperties;
    }
    

    /**
     * Create a new card for the specifiate entity, rescaled down.
     * if rescale == true, scale factor == 2.5f.
     *
     * @param screen used to render the card (tonegodGUI)
     * @param rescale should this cards rescaled down.
     * @param cardName Name to use for the card.
     * @param handPosition position to put the card in the player hand.
     * @param UID Entity this card belong to.
     */
    public Card(ElementManager screen, boolean rescale, String cardName, int handPosition, EntityId UID, CardProperties cardProperties) {
        super(screen, UID.toString(), Vector2f.ZERO, new Vector2f(200f / (2.5f * (rescale ? 1 : 0)), 
                300f / (2.5f * (rescale ? 1 : 0))), Vector4f.ZERO, "Textures/Cards/" + cardName + ".png");
        this.rescaleValue = 2.5f; //if you change this change it in the super constructor above.
        this.isRescale = (rescale ? 1 : 0);
        this.cardSize = new Vector2f(200f / (rescaleValue * isRescale), 300f / (rescaleValue * isRescale));
        this.handPosition = handPosition;
        this.cardName = cardName;
        this.UID = UID;
        this.cardProperties = cardProperties;
        
        this.removeEffect(Effect.EffectEvent.Hover);
        this.removeEffect(Effect.EffectEvent.Press);
        this.setIsResizable(false);
        this.setIsMovable(true);
    }

    /**
     * Put the card to his initiale position in the hand.
     */
    void resetHandPosition() {
        setPosition(new Vector2f(220f + ((cardSize.x - 20) * handPosition), screen.getHeight() - this.getHeight() - 20));
    }

    /**
     * Called when the mouse is over the card.
     * @param hasFocus true if over.
     */
    @Override
    public void setHasFocus(boolean hasFocus) {
        super.setHasFocus(hasFocus);
        if (hasFocus) {
            app.getStateManager().getState(CardRenderSystem.class).hasFocus(this);
        } else {
            app.getStateManager().getState(CardRenderSystem.class).lostFocus(this);
        }
    }

    /**
     * Called when the left Mouse is pressed over this card.
     * @param evt ??
     * @param toggled ??
     */
    @Override
    public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
        super.onButtonMouseLeftDown(evt, toggled);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Called when the left mouse is released (over this card?).
     * @param evt ??
     * @param toggled ??
     */
    @Override
    public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        super.onButtonMouseLeftUp(evt, toggled);
        CardRenderSystem renderSystem = app.getStateManager().getState(CardRenderSystem.class);
        renderSystem.isInCastArea(this);
        resetHandPosition();
    }

    /**
     *
     * @return current hand position of the card.
     */
    int getHandPosition() {
        return this.handPosition;
    }

    /**
     * Change the current player handPosition of the card.
     *
     * @param handPosition
     */
    void sethandPosition(int handPosition) {
        this.handPosition = handPosition;
    }

    /**
     * Used when adding the Hover so it fit the cardSize.
     * @param child
     */
    @Override
    public void addChild(Element child) {
        super.addChild(child);
        child.setDimensions(cardSize);
        child.centerToParent();
    }
}
