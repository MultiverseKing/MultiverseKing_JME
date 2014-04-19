/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    private HashMap<String, Window> cards = new HashMap<String, Window>();
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
        Window card;
        card = cardInitializer.initialize(e.get(CardRenderComponent.class).getCardName());
        cards.put(e.get(CardRenderComponent.class).getCardName(), card);
        screen.addElement(card);
    }

    @Override
    protected void updateEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
//        if(spatials.containsKey(e.getId())){
//            cardRenderSystemNode.detachChildNamed(e.get(CardRenderComponent.class).getCardName());
//            spatials.remove(e.getId());
//        } else {
//            System.err.println(e.get(CardRenderComponent.class).getCardName()+" does not exist in the Render System Node.");
//        }
    }

//    @Override
//    public void stateDetached(AppStateManager stateManager) {
//        super.stateDetached(stateManager);
//        for (Window card : cards.values()) {
//            screen.removeElement(card);
//        }
//    }

    @Override
    protected void cleanupSystem() {
        for(Window card : cards.values()){
            screen.removeElement(card);
        }
        cards.clear();
        app.getGuiNode().removeControl(screen);
    }
}
