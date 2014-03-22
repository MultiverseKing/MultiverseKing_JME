package entitysystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
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

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.entityData = stateManager.getState(EntityDataAppState.class).getEntityData();
        this.mapData = stateManager.getState(MapDataAppState.class).getMapData();
    }
}
