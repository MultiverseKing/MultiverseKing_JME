package org.multiverseking.core.utility;

import org.multiverseking.core.EntityDataAppState;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * An abstract AppState to allow EntitySystems to easily use Entity.
 *
 * @author Eike Foede
 */
public abstract class EntitySystemAppState extends AbstractAppState {

    /**
     * entity System data.
     */
    protected EntityData entityData;
    /**
     * Entity bounded to this system.
     */
    protected EntitySet entities;
    /**
     * main.
     */
    protected SimpleApplication app;

    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app;
        EntityDataAppState tmp = stateManager.getState(EntityDataAppState.class);

        this.entityData = tmp.getEntityData();

        entities = initialiseSystem();
        entities.stream().forEach((e) -> {
            addEntity(e);
        });
    }

    @Override
    public final void update(float tpf) {
        if (entities.applyChanges()) {
            entities.getChangedEntities().stream().forEach((e) -> {
                updateEntity(e);
            });
            entities.getAddedEntities().stream().forEach((e) -> {
                addEntity(e);
            });
            entities.getRemovedEntities().stream().forEach((e) -> {
                removeEntity(e);
            });
        }
        this.updateSystem(tpf);
    }

    @Override
    public final void cleanup() {
        entities.stream().forEach((e) -> {
            removeEntity(e);
        });
        this.cleanupSystem();
        entities.release();
        super.cleanup();
    }

    /**
     * Activate the system.
     *
     * @return entity used with this system.
     */
    protected abstract EntitySet initialiseSystem();

    /**
     * Called each frame.
     *
     * @param tpf
     */
    protected abstract void updateSystem(float tpf);

    /**
     * Called when an entity is added.
     *
     * @param e entity to add.
     */
    protected abstract void addEntity(Entity e);

    /**
     * Called when an entity got an update.
     *
     * @param e updated entity.
     */
    protected abstract void updateEntity(Entity e);

    /**
     * Called when an entity is removed.
     *
     * @param e removed entity.
     */
    protected abstract void removeEntity(Entity e);

    /**
     * Called when the system is removed, used to clean his mess.
     */
    protected abstract void cleanupSystem();
}
