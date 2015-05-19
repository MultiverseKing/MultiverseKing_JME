package org.multiversekingesapi.card;

import com.simsilica.es.EntityComponent;
import org.multiversekingesapi.card.attribut.CardRenderPosition;
import org.multiversekingesapi.render.AbstractRender;

/**
 * Used by the card render system, Contain where the Card should be rendered on
 * the screen.
 *
 * @author roah
 */
public class CardRenderComponent extends AbstractRender implements EntityComponent {

    private final CardRenderPosition renderPosition;

    public CardRenderComponent(String name, CardRenderPosition renderPosition, RenderType renderType) {
        super(name, renderType, true);
        this.renderPosition = renderPosition;
    }

    public CardRenderComponent(String name, CardRenderPosition renderPosition, RenderType renderType, boolean isVisible) {
        super(name, renderType, isVisible);
        this.renderPosition = renderPosition;
    }

    /**
     * where the card is.
     *
     * @see CardRenderPosition
     * @return
     */
    public CardRenderPosition getRenderPosition() {
        return renderPosition;
    }

    public CardRenderComponent clone(String name) {
        return new CardRenderComponent(name, renderPosition, getRenderType(), isVisible());
    }

    public CardRenderComponent clone(CardRenderPosition renderPosition) {
        return new CardRenderComponent(getName(), renderPosition, getRenderType(), isVisible());
    }
}
