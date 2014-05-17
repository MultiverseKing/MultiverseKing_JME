package entitysystem.render;

import com.jme3.animation.AnimControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.CoreDataAppState;
import entitysystem.position.HexPositionComponent;
import hexsystem.HexTile;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import java.util.Set;
import utility.HexCoordinate;
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
public class VFXRenderSystem extends CoreDataAppState implements TileChangeListener {

    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private VFXSpatialInitializer VFXInitializer = new VFXSpatialInitializer();
    private Node VFXNode = new Node("VFXNode");

    @Override
    protected EntitySet initialiseSystem() {
        VFXInitializer.setAssetManager(app.getAssetManager());
        app.getRootNode().attachChild(VFXNode);
        getMapData().registerTileChangeListener(this);
        return entityData.getEntities(VFXComponent.class, HexPositionComponent.class);
    }
    
    
    @Override
    public void addEntity(Entity e) {
        Spatial s = VFXInitializer.initialize(e.get(RenderComponent.class).getName());
        spatials.put(e.getId(), s);
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        s.setLocalTranslation(getMapData().hexPositionToSpatialPosition(positionComp.getPosition()));
        s.setLocalRotation(Rotation.getQuaternion(positionComp.getRotation()));
        VFXNode.attachChild(s);
    }

    @Override
    protected void updateSystem(float tpf) {
    }
    
    @Override
    protected void updateEntity(Entity e) {
        
    }

    /**
     *
     * @param e
     */
    @Override
    public void removeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        VFXNode.detachChild(s);
        spatials.remove(e.getId());
    }

    /**
     *
     */
    @Override
    protected void cleanupSystem() {
        spatials.clear();
        VFXNode.removeFromParent();
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
}
