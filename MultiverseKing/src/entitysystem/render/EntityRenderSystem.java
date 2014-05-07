package entitysystem.render;

import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import entitysystem.position.RotationComponent;
import hexsystem.HexTile;
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

    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private CharacterSpatialInitializer spatialInitializer = new CharacterSpatialInitializer();
//    private CubeSpatialInitializer spatialInitializer = new CubeSpatialInitializer();
    private Node renderSystemNode = new Node("RenderSystemNode");

    public AnimControl getAnimControl(EntityId id) {
        return spatials.get(id).getControl(AnimControl.class);
    }
    
    @Override
    protected EntitySet initialiseSystem() {
        spatialInitializer.setAssetManager(app.getAssetManager());
        app.getRootNode().attachChild(renderSystemNode);
        mapData.registerTileChangeListener(this);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class, RotationComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    //TODO: Handle if spatial is already generated (shouldn't happen, but in the case it does, this should be handled))
    //some spatial can be generated multiple time...
    public void addEntity(Entity e) {
        Spatial s = spatialInitializer.initialize(e.get(RenderComponent.class).getName());
        spatials.put(e.getId(), s);
        s.setLocalTranslation(hexPositionToSpatialPosition(e.get(HexPositionComponent.class)));
        s.setLocalRotation(Rotation.getQuaternion(e.get(RotationComponent.class).getRotation()));
        renderSystemNode.attachChild(s);
    }

    @Override
    //TODO: Check if spatial is found
    protected void updateEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        s.setLocalTranslation(hexPositionToSpatialPosition(e.get(HexPositionComponent.class)));
        mapData.setLogicTileInUse(e.get(HexPositionComponent.class).getPosition(), true);
        s.setLocalRotation(Rotation.getQuaternion(e.get(RotationComponent.class).getRotation()));
    }

    @Override
    public void removeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        renderSystemNode.detachChild(s);
        spatials.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
        spatials.clear();
        renderSystemNode.removeFromParent();
    }

    private Vector3f hexPositionToSpatialPosition(HexPositionComponent hex) {
        HexTile tile = mapData.getTile(hex.getPosition());
        if(tile != null){
            int height = tile.getHeight();
            Vector3f spat = mapData.getTileWorldPosition(hex.getPosition());
            return new Vector3f(spat.x, height, spat.z);
        } else {
            System.err.println("There is no Tile on the position " + hex.toString());
            return null;
        }
    }

    public void tileChange(TileChangeEvent event) {
        Set<EntityId> key = spatials.keySet();
        for(EntityId id : key){
            if(entityData.getComponent(id, HexPositionComponent.class).getPosition().equals(event.getTilePos())){
                Vector3f currentLoc = spatials.get(id).getLocalTranslation();
                spatials.get(id).setLocalTranslation(currentLoc.x, event.getNewTile().getHeight(), currentLoc.z);
            }
        }
    }
}
