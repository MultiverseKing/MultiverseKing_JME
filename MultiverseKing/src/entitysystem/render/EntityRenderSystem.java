package entitysystem.render;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import java.util.HashMap;
import java.util.Map.Entry;
import test.CubeSpatialInitializer;

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
public class EntityRenderSystem extends EntitySystemAppState {

    private EntitySet hexPositionEntities;
    //private EntitySet spatialPositionEntities;
    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private SpatialInitializer spatialInitializer = new CubeSpatialInitializer();
    private Node renderSystemNode = new Node("RenderSystemNode");

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        hexPositionEntities = entityData.getEntities(HexPositionComponent.class, RenderComponent.class);
        //spatialPositionEntities = entityData.getEntities(SpatialPositionComponent.class, RenderComponent.class);
        spatialInitializer.setAssetManager(app.getAssetManager());
        ((SimpleApplication) app).getRootNode().attachChild(renderSystemNode);

        //If there are already entities in the EntitySet
        for (Entity e : hexPositionEntities) {
            addEntity(e);
        }
    }

    @Override
    public void update(float tpf) {
        if (hexPositionEntities.applyChanges()) {
            //Add Entities
            for (Entity e : hexPositionEntities.getAddedEntities()) {
                addEntity(e);
            }
            //Change Entities
            for (Entity e : hexPositionEntities.getChangedEntities()) {
                changeEntity(e);
            }
            //Remove Entities
            for (Entity e : hexPositionEntities.getRemovedEntities()) {
                removeEntity(e);
            }
        }
    }

    //TODO: Handle if spatial is already generated (shouldn't happen, but in the case it does, this should be handled))
    private void addEntity(Entity e) {
        Spatial s = spatialInitializer.initialize(e.get(RenderComponent.class).getModelName());
        spatials.put(e.getId(), s);
        s.setLocalTranslation(hexPositionToSpatialPosition(e.get(HexPositionComponent.class)));
        renderSystemNode.attachChild(s);
    }

    //TODO: Check if spatial was found
    private void changeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        s.setLocalTranslation(hexPositionToSpatialPosition(e.get(HexPositionComponent.class)));
    }

    private void removeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        renderSystemNode.detachChild(s);
        spatials.remove(s);
    }

    private Vector3f hexPositionToSpatialPosition(HexPositionComponent hex) {
        //TODO: Catch empty tile!!
        int height = mapData.getTile(hex.getPosition()).getHeight();
        Vector3f spat = mapData.getTileWorldPosition(hex.getPosition());
        return new Vector3f(spat.x, height, spat.z);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        hexPositionEntities.release();
        //spatialPositionEntities.release();
        for (Entry<EntityId, Spatial> e : spatials.entrySet()) {
            renderSystemNode.detachChild(e.getValue());
        }
        spatials.clear();
        renderSystemNode.removeFromParent();
    }
}
