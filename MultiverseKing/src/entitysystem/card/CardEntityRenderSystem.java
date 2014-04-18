/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.card;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;

/**
 *
 * @author roah
 */
public class CardEntityRenderSystem extends EntitySystemAppState{

    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private CardSpatialInitializer spatialInitializer = new CardSpatialInitializer();
    private Node cardRenderSystemNode = new Node("cardRenderSystemNode");
    
    @Override
    protected EntitySet initialiseSystem() {
        spatialInitializer.setAssetManager(app.getAssetManager());
        //@todo the node should be always in front of the camera.
        app.getRootNode().getChild("camera").getParent().attachChild(cardRenderSystemNode);
        //We check for entity who got the CardRenderComponent
        return entityData.getEntities(CardRenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    //TODO: Handle if spatial is already generated (shouldn't happen, but in the case it does, this should be handled))
    protected void addEntity(Entity e) {
        Spatial s;
        //We check if this card isn't generated already, if so, we clone the spatial and assign it
        s = cardRenderSystemNode.getChild(e.get(CardRenderComponent.class).getCardName());
        if(s != null){
            s = s.clone();
        } else {
            s = spatialInitializer.initialize(e.get(CardRenderComponent.class).getCardName());
        }
        spatials.put(e.getId(), s);
        cardRenderSystemNode.attachChild(s);
    }

    @Override
    protected void updateEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        if(spatials.containsKey(e.getId())){
            cardRenderSystemNode.detachChildNamed(e.get(CardRenderComponent.class).getCardName());
            spatials.remove(e.getId());
        } else {
            System.err.println(e.get(CardRenderComponent.class).getCardName()+" does not exist in the Render System Node.");
        }
    }

    @Override
    protected void cleanupSystem() {
        spatials.clear();
        cardRenderSystemNode.removeFromParent();
    }
    
}
