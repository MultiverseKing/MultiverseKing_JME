/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    private HashMap<String, ButtonAdapter> cards = new HashMap<String, ButtonAdapter>();
    private CardInitializer cardInitializer = new CardInitializer();
    private Screen screen;

    @Override
    protected EntitySet initialiseSystem() {
        this.screen = new Screen(app);
        app.getGuiNode().addControl(screen);
        cardInitializer.Init(app.getAssetManager(), screen);
        //We check for entity who got the CardRenderComponent
        return entityData.getEntities(CardRenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        ButtonAdapter card = cardInitializer.initialize(e.get(CardRenderComponent.class).getCardName());
        cards.put(e.get(CardRenderComponent.class).getCardName(), card);
        screen.addElement(card);
    }

    @Override
    protected void updateEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        ButtonAdapter card = cards.get(e.get(CardRenderComponent.class).getCardName());
        screen.removeElement(card);
        cards.remove(e.get(CardRenderComponent.class).getCardName());
    }
    
    @Override
    protected void cleanupSystem() {
        cardInitializer.cleanup();
        cardInitializer = null;
        for(ButtonAdapter card : cards.values()){
            screen.removeElement(card);
        }
        cards.clear();
        app.getGuiNode().removeControl(screen);
        screen = null;
    }
}
