package entitysystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;

/**
 * The EntityData and MapData of the game is stored in this AppState to allow
 * other AppStates to retrieve it.
 *
 * @author Eike Foede, roah
 */
public class EntityDataAppState extends AbstractAppState {

    private EntityData entityData;

    /**
     *
     */
    public EntityDataAppState() {
        entityData = new DefaultEntityData();
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
    }
    
    public EntityData getEntityData() {
        return entityData;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
