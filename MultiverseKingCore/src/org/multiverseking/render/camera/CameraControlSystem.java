package org.multiverseking.render.camera;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.geometry.buffer.BufferPositionProvider;
import org.hexgridapi.core.geometry.buffer.HexGridBuffer;
import org.multiverseking.utility.system.EntitySystemAppState;
import org.multiverseking.utility.system.SubSystem;
import org.multiverseking.render.RenderSystem;

/**
 * Used to bound the camera, or {@link HexGridBuffer} to the player position.
 * Make the grid being generated around the tracked character.
 * Make the camera being lock to the tracked character.
 *
 * @author roah
 */
public class CameraControlSystem extends EntitySystemAppState implements SubSystem, BufferPositionProvider {

    private RTSCamera camera;
    private EntityId trackedEntity;
    private Spatial trackedSpatial;
    private RenderSystem renderSystem;
    // Set this to false to free the camera from the tracked character
    private boolean lockCamera = true; 

    @Override
    protected EntitySet initialiseSystem() {
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        renderSystem.registerSubSystem(this);
        camera = app.getStateManager().getState(RTSCamera.class);
        updateProvider(true);
        return entityData.getEntities(CameraTrackComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        if (trackedSpatial == null && !entities.isEmpty()) {
            trackedSpatial = renderSystem.getSpatial(trackedEntity);
        }
        // this part bound the camera position to the character position
        if (lockCamera && trackedSpatial != null) {
            camera.setCenter(trackedSpatial.getLocalTranslation());
        }
    }

    @Override
    protected void addEntity(Entity e) {
        setTrackingTo(e.getId());
    }

    @Override
    protected void updateEntity(Entity e) {
        setTrackingTo(e.getId());
    }

    @Override
    protected void removeEntity(Entity e) {
        setTrackingTo(null);
    }

    private void setTrackingTo(EntityId id) {
        if (id != null) {
            if (trackedEntity != null && !trackedEntity.equals(id)) {
                entityData.removeComponent(trackedEntity, CameraTrackComponent.class);
            } else if (trackedEntity != null) {
                return;
            }
            trackedEntity = id;
            trackedSpatial = renderSystem.getSpatial(id);
        } else {
            trackedEntity = null;
            trackedSpatial = null;
        }
    }

    @Override
    protected void cleanupSystem() {
        renderSystem.removeSubSystem(this, true);
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this);
    }

    @Override
    public Vector3f getBufferPosition() {
        return trackedSpatial.getLocalTranslation();
    }

    @Override
    public void resetToOriginPosition(Vector3f pos) {
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateProvider(enabled);
    }
    
    private void updateProvider(boolean enable) {
        if(!enable) {
            app.getStateManager().getState(AbstractHexGridAppState.class)
                    .setBufferPositionProvider(app.getStateManager().getState(RTSCamera.class));
        } else {
            app.getStateManager().getState(AbstractHexGridAppState.class)
                    .setBufferPositionProvider(this);
        }
    }
}
