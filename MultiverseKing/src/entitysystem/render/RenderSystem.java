package entitysystem.render;

import com.jme3.animation.AnimControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import tonegod.gui.core.Screen;
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
public class RenderSystem extends EntitySystemAppState implements TileChangeListener {

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
    
    public void addControl(EntityId id, Control control) {
        if (spatials.containsKey(id)) {
            spatials.get(id).addControl(control);
        }
    }

    @Override
    protected EntitySet initialiseSystem() {
        app.getRootNode().attachChild(renderSystemNode);
        renderSystemNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive); //<< diseable this to remove the shadow.
        spatialInitializer = new SpatialInitializer(app.getAssetManager());
//        initializeMenus();
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        mapData.registerTileChangeListener(this);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
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
        RenderComponent renderComp = e.get(RenderComponent.class);
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
        String eName = (String) (e.get(RenderComponent.class).getName() + e.getId().toString());
        if (s == null) {
            s = spatialInitializer.initialize(e.get(RenderComponent.class).getName());
            s.setName(eName);
            spatials.put(e.getId(), s);
            renderSystemNode.attachChild(s);
        } else if (!eName.equals(s.getName())) {
            s = spatialInitializer.initialize(e.get(RenderComponent.class).getName());
            s.setName(eName);
            renderSystemNode.detachChild(spatials.get(e.getId()));
            spatials.put(e.getId(), s);
            renderSystemNode.attachChild(s);
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
        renderSystemNode.detachChild(s);
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
