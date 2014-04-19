/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    private HashMap<String, Window> cards = new HashMap<String, Window>();
    private CardInitializer spatialInitializer = new CardInitializer();

    @Override
    protected EntitySet initialiseSystem() {
        spatialInitializer.setAssetManager(app.getAssetManager());

        //Create a new screen to be used as a container for all card in the hand of the player
        //todo a screen killer ?
        
        //We check for entity who got the CardRenderComponent
        return entityData.getEntities(CardRenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        Window card;
        card = spatialInitializer.initialize(e.get(CardRenderComponent.class).getCardName());
        cards.put(e.get(CardRenderComponent.class).getCardName(), card);
        handCardScreen.addElement(card);
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

    @Override
    protected void cleanupSystem() {
        cards.clear();
        handCardScreen.
        app.getGuiNode().removeControl(handCardScreen);
    }
}
