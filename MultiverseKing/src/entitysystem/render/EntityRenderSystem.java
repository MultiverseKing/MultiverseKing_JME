package entitysystem.render;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import entitysystem.position.RotationComponent;
import java.util.HashMap;
import test.CharacterSpatialInitializer;
import utility.Rotation;
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
public class EntityRenderSystem extends EntitySystemAppState {

    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private CharacterSpatialInitializer spatialInitializer = new CharacterSpatialInitializer();
//    private CubeSpatialInitializer spatialInitializer = new CubeSpatialInitializer();
    private Node renderSystemNode = new Node("RenderSystemNode");

    @Override
    protected EntitySet initialiseSystem() {
        spatialInitializer.setAssetManager(app.getAssetManager());
        app.getRootNode().attachChild(renderSystemNode);
        return entityData.getEntities(HexPositionComponent.class, RenderComponent.class, RotationComponent.class);

    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    //TODO: Handle if spatial is already generated (shouldn't happen, but in the case it does, this should be handled))
    public void addEntity(Entity e) {
        Spatial s = spatialInitializer.initialize(e.get(RenderComponent.class).getModelName());
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
        s.setLocalRotation(Rotation.getQuaternion(e.get(RotationComponent.class).getRotation()));
    }

    @Override
    public void removeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        renderSystemNode.detachChild(s);
        spatials.remove(s);
    }

    @Override
    protected void cleanupSystem() {
        spatials.clear();
        renderSystemNode.removeFromParent();
    }

    private Vector3f hexPositionToSpatialPosition(HexPositionComponent hex) {
        //TODO: Catch empty tile!!
        int height = mapData.getTile(hex.getPosition()).getHeight();
        Vector3f spat = mapData.getTileWorldPosition(hex.getPosition());
        return new Vector3f(spat.x, height, spat.z);
    }
}
