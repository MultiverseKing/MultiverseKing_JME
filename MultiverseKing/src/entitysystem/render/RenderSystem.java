package de.tsajar.es.render;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.PositionComponent;
import entitysystem.position.RotationComponent;
import entitysystem.render.RenderComponent;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Comments
 * @author Eike Foede
 */
public class RenderSystem extends EntitySystemAppState {

    private Map<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private Node renderNode;
    EntitySet renderData;
    SpatialInitializer initializer;

    public RenderSystem(SpatialInitializer initializer) {
        this.initializer = initializer;
    }

    private void initializeSpatial(Entity e) {

        Spatial s = initializer.initialize(e.get(RenderComponent.class).getModelName());
        setEntityId(s, e.getId().getId());
        s.setLocalTranslation(e.get(PositionComponent.class).getPosition());
        s.setLocalRotation(e.get(RotationComponent.class).getRotation());
        spatials.put(e.getId(), s);
        renderNode.attachChild(s);
    }

    /*
     * TODO: Alternatively to setting the Id recursively maybe provide a
     * function getEntityId(Spatial) which goes upwards until it finds a parent
     * with an EntityId?
     */
    private void setEntityId(Spatial s, long id) {

        s.setUserData("EntityId", id);

        if (Node.class.isAssignableFrom(s.getClass())) {

            for (Spatial c : ((Node) s).getChildren()) {
                setEntityId(c, id);
            }
        }
    }

    private void updateSpatial(Entity e) {
        Spatial s = spatials.get(e.getId());

        s.setLocalTranslation(e.get(PositionComponent.class).getPosition());
        s.setLocalRotation(e.get(RotationComponent.class).getRotation());
    }

    private void cleanupSpatial(Entity e) {
        Spatial s = spatials.get(e.getId());
        renderNode.detachChild(s);
        spatials.remove(e.getId());
    }

    /**
     * Returns the spatial of this EntityId if exists, else null.
     *
     * @deprecated Unclean, systems shouldn't depend on this!!! atm
     * RigidBodyControlSystem does.
     * @param id
     * @return
     */
    public Spatial getSpatial(EntityId id) {
        return spatials.get(id);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        renderNode = new Node("EntityRenderNode");
        ((SimpleApplication) app).getRootNode().attachChild(renderNode);
        renderData = this.entityData.getEntities(RenderComponent.class, PositionComponent.class, RotationComponent.class);
        for (Entity e : renderData) {
            initializeSpatial(e);
        }
    }

    @Override
    public void update(float tpf) {
        renderData.applyChanges();
        for (Entity e : renderData.getAddedEntities()) {
            initializeSpatial(e);
        }
        for (Entity e : renderData.getChangedEntities()) {
            updateSpatial(e);
        }
        for (Entity e : renderData.getRemovedEntities()) {
            cleanupSpatial(e);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        renderData.release();
        for (Spatial s : spatials.values()) {
            renderNode.detachChild(s);
        }
        spatials.clear();
    }
}
