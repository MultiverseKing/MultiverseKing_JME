package entitysystem.render;

import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.tonegod.ToneControl;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import java.util.Set;
import tonegod.gui.controls.menuing.Menu;
import utility.Rotation;
import utility.SimpleMath;

/**
 * TODO: Rotation/Orientation; Picking/Raycasting; Comments
 *
 * @author Eike Foede
 */

/*
 * Just use HexPositions, the rare cases where Spatial Positions would be used
 * should be handled seperately. Else there could be double data for the
 * position with inconsistencies between the different data.
 */
public class CoreRenderSystem extends EntitySystemAppState implements TileChangeListener {

    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private SpatialInitializer spatialInitializer;
    private Node renderSystemNode = new Node("RenderSystemNode");
    private MapData mapData;

    /**
     * Return the Animation Spatial control of an entity, if any.
     *
     * @param id of the entity.
     * @return null if no anim control.
     */
    public AnimControl getAnimControl(EntityId id) {
        if (spatials.containsKey(id)) {
            return spatials.get(id).getControl(AnimControl.class);
        } else {
            return null;
        }
    }
    
    public String getSpatialName(EntityId id){
        return spatials.get(id).getName();
    }
    
    public void addSystemNode(Node systemNode){
        renderSystemNode.attachChild(systemNode);
    }
    
    public boolean addSpatialToSubSystem(EntityId id, String systemNode){
        if(spatials.get(id) != null){
            ((Node)renderSystemNode.getChild(systemNode)).attachChild(spatials.get(id));
            return true;
        }
        return false;
    }
    
    public void removeSpatialFromSubSystem(EntityId id){
        if(spatials.get(id) != null){
            renderSystemNode.attachChild(spatials.get(id));
        }
    }

    @Override
    protected EntitySet initialiseSystem() {
        app.getRootNode().attachChild(renderSystemNode);
        renderSystemNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive); //<< diseable this to remove the shadow.
        spatialInitializer = new SpatialInitializer(app.getAssetManager());
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        mapData.registerTileChangeListener(this);
        return entityData.getEntities(CoreRenderComponent.class, HexPositionComponent.class);
    }

    /**
     *
     * @param tpf
     */
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void addEntity(Entity e) {
        CoreRenderComponent renderComp = e.get(CoreRenderComponent.class);
        Spatial s = spatialInitializer.initialize(renderComp.getName());
        s.setName(renderComp.getName() + e.getId().toString());
        spatials.put(e.getId(), s);
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        s.setLocalTranslation(mapData.convertTileToWorldPosition(positionComp.getPosition()));
        s.setLocalRotation(Rotation.getQuaternion(positionComp.getRotation()));
        renderSystemNode.attachChild(s);
    }
    

    @Override
    protected void updateEntity(Entity e) {
        /**
         * Update the spatial if it need to.
         */
        Spatial s = spatials.get(e.getId());
        String eName = (String) (e.get(CoreRenderComponent.class).getName() + e.getId().toString());
        if (s == null) {
            s = spatialInitializer.initialize(e.get(CoreRenderComponent.class).getName());
            s.setName(eName);
            spatials.put(e.getId(), s);
            renderSystemNode.attachChild(s);
        } else if (!eName.equals(s.getName())) {
            s = spatialInitializer.initialize(e.get(CoreRenderComponent.class).getName());
            s.setName(eName);
            Node parent = spatials.get(e.getId()).getParent();
            spatials.get(e.getId()).removeFromParent();
            spatials.put(e.getId(), s);
            parent.attachChild(s);
            s.setName(eName);
        }
        /**
         * Update the translation and position if they need to. Rotation always
         * updated currently.
         */
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        Vector3f vect = SimpleMath.substractAbs(positionComp.getPosition().convertToWorldPosition(), spatials.get(e.getId()).getLocalTranslation());
        vect.y = 0;
        if (vect.equals(Vector3f.ZERO)) {
            s.setLocalTranslation(mapData.convertTileToWorldPosition(positionComp.getPosition()));
        }
        s.setLocalRotation(Rotation.getQuaternion(positionComp.getRotation()));
        
    }

    /**
     *
     * @param e
     */
    @Override
    public void removeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        if(s.getControl(ToneControl.class) != null){
            entityData.removeComponent(e.getId(), RenderGUIComponent.class);
            renderSystemNode.detachChild(s.getParent());
        } else {
            renderSystemNode.detachChild(s);
        }
        spatials.remove(e.getId());
    }

    

    /**
     *
     */
    @Override
    protected void cleanupSystem() {
        spatials.clear();
        renderSystemNode.removeFromParent();
    }

    /**
     *
     * @param event
     */
    public void tileChange(TileChangeEvent event) {
        Set<EntityId> key = spatials.keySet();
        for (EntityId id : key) {
            if (entityData.getComponent(id, HexPositionComponent.class).getPosition().equals(event.getTilePos())) {
                Vector3f currentLoc = spatials.get(id).getLocalTranslation();
                spatials.get(id).setLocalTranslation(currentLoc.x, event.getNewTile().getHeight(), currentLoc.z);
            }
        }
    }

    public void addControl(EntityId id, ToneControl control, Menu currentMenu) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
