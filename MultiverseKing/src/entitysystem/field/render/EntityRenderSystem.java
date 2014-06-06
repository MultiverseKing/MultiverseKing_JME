package entitysystem.field.render;

import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import gamestate.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import java.util.Set;
import utility.Rotation;

/**
 * TODO: Rotation/Orientation; Picking/Raycasting; Comments
 *
 * @author Eike Foede
 */

/*
 * Just use HexPositions, the rare cases where spatial Positions would be used
 * should be handled seperately. Else there could be double data for the
 * position with inconsistencies between the different data.
 */
public class EntityRenderSystem extends EntitySystemAppState implements TileChangeListener {

    private MapData mapData;
    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private CharacterSpatialInitializer spatialInitializer = new CharacterSpatialInitializer();
    private Node renderSystemNode = new Node("RenderSystemNode");

    /**
     * Return the Animation Spatial control of an entity.
     * @param id of the entity.
     * @return AnimControl of the entity.
     */
    public AnimControl getAnimControl(EntityId id) {
        return spatials.get(id).getControl(AnimControl.class);
    }
    
    @Override
    protected EntitySet initialiseSystem() {
        spatialInitializer.setAssetManager(app.getAssetManager());
        app.getRootNode().attachChild(renderSystemNode);
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

    /**
     *
     * @param e
     */
    @Override
    //TODO: Handle if spatial is already generated (shouldn't happen, but in the case it does, this should be handled))
    //some spatial can be generated multiple time...
    public void addEntity(Entity e) {
        Spatial s = spatialInitializer.initialize(e.get(RenderComponent.class).getName());
        spatials.put(e.getId(), s);
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        s.setLocalTranslation(mapData.hexPositionToSpatialPosition(positionComp.getPosition()));
        s.setLocalRotation(Rotation.getQuaternion(positionComp.getRotation()));
        renderSystemNode.attachChild(s);
    }

    /**
     *
     * @param e
     */
    @Override
    //TODO: Check if spatial is found
    protected void updateEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        s.setLocalTranslation(mapData.hexPositionToSpatialPosition(positionComp.getPosition()));
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

//    private Vector3f hexPositionToSpatialPosition(HexCoordinate hexPos) {
//        HexTile tile = getMapData().getTile(hexPos);
//        if (tile != null) {
//            int height = tile.getHeight();
//            Vector3f spat = getMapData().getTileWorldPosition(hexPos);
//            return new Vector3f(spat.x, height, spat.z);
//        } else {
//            System.err.println("There is no Tile on the position " + hexPos.toString());
//            return null;
//        }
//    }

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
}