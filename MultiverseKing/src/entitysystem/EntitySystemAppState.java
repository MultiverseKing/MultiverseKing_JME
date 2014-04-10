package entitysystem;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import hexsystem.MapData;
import hexsystem.MapDataAppState;

/**
 * An abstract AppState to allow EntitySystems to easily use the Entity- and
 * MapData.
 *
 * @author Eike Foede
 */
public abstract class EntitySystemAppState extends AbstractAppState {

    protected EntityData entityData;
    protected MapData mapData;
    protected EntitySet entities;
    protected SimpleApplication app;

    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app;
        this.entityData = stateManager.getState(EntityDataAppState.class).getEntityData();
        this.mapData = stateManager.getState(MapDataAppState.class).getMapData();

        entities = initialiseSystem();
        for (Entity e : entities) {
            addEntity(e);
        }
    }

    @Override
    public final void update(float tpf) {
        if (entities.applyChanges()) {
            for (Entity e : entities.getAddedEntities()) {
                addEntity(e);
            }
            for (Entity e : entities.getChangedEntities()) {
                updateEntity(e);
            }
            for (Entity e : entities.getRemovedEntities()) {
                removeEntity(e);
            }
        }
        this.updateSystem(tpf);
    }

    @Override
    public final void cleanup() {
        for (Entity e : entities) {
            removeEntity(e);
        }
        this.cleanupSystem();
        entities.release();
        super.cleanup();
    }

    protected abstract EntitySet initialiseSystem();

    protected abstract void updateSystem(float tpf);

    protected abstract void addEntity(Entity e);

    protected abstract void updateEntity(Entity e);

    protected abstract void removeEntity(Entity e);

    protected abstract void cleanupSystem();
}
