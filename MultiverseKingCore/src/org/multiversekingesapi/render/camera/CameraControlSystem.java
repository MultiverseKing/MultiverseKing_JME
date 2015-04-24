package org.multiversekingesapi.render.camera;

import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.RTSCamera;
import org.multiversekingesapi.EntitySystemAppState;
import org.multiversekingesapi.SubSystem;
import org.multiversekingesapi.render.RenderComponent;
import org.multiversekingesapi.render.RenderSystem;

/**
 *
 * @author roah
 */
public class CameraControlSystem extends EntitySystemAppState implements SubSystem {
    private final RTSCamera camera;
    private EntityId trackedEntity;
    private Spatial trackedSpatial;
    private RenderSystem renderSystem;

    public CameraControlSystem(RTSCamera camera) {
        this.camera = camera;
    }
    
    @Override
    protected EntitySet initialiseSystem() {
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        renderSystem.registerSubSystem(this);
        return entityData.getEntities(RenderComponent.class, CameraTrackComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        if(trackedSpatial == null && !entities.isEmpty()) {
            trackedSpatial = renderSystem.getSpatial(trackedEntity);
        }
        if(trackedSpatial != null){
            camera.setCenter(trackedSpatial.getLocalTranslation());
        }
    }

    @Override
    protected void addEntity(Entity e) {
        trackedEntity = e.getId();
        trackedSpatial = renderSystem.getSpatial(e.getId());
    }

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
        if(e.get(CameraTrackComponent.class) != null){
            entityData.removeComponent(e.getId(), CameraTrackComponent.class);
        }
        trackedEntity = null;
        trackedSpatial = null;
    }

    @Override
    protected void cleanupSystem() {
        renderSystem.removeSubSystem(this, true);
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this);
    }
    
}
