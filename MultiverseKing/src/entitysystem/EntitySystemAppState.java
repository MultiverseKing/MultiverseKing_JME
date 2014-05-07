package entitysystem;

import gamestate.GameDataAppState;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import hexsystem.MapData;

/**
 * An abstract AppState to allow EntitySystems to easily use Entity and
 * MapData.
 *
 * @author Eike Foede, roah
 */
public abstract class EntitySystemAppState extends AbstractAppState {

    /**
     * entity System data and bridge to mapData.
     */
    protected ExtendedEntityData entityData;
    /**
     * Entity bounded to this system.
     */
    protected EntitySet entities;
    /**
     * main.
     */
    protected SimpleApplication app;

    /**
     * Hex system Data handler.
     * @return 
     */
    public MapData getMapData() {
        return entityData.getMapData();
    }
    
    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = (SimpleApplication) app;
        this.entityData = (ExtendedEntityData) stateManager.getState(GameDataAppState.class).getEntityData();

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

    /**
     * Activate the system.
     * @return entity used with this system.
     */
    protected abstract EntitySet initialiseSystem();

    /**
     * Called each frame.
     * @param tpf
     */
    protected abstract void updateSystem(float tpf);

    /**
     * Called when an entity is added.
     * @param e entity to add.
     */
    protected abstract void addEntity(Entity e);

    /**
     * Called when an entity got an update.
     * @param e updated entity.
     */
    protected abstract void updateEntity(Entity e);

    /**
     * Called when an entity is removed.
     * @param e removed entity.
     */
    protected abstract void removeEntity(Entity e);

    /**
     * Called when the system is removed, used to clean his mess.
     */
    protected abstract void cleanupSystem();
}
