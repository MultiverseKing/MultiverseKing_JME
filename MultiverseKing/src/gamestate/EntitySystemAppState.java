package gamestate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;
import entitysystem.ExtendedEntityData;
import hexsystem.MapData;

/**
 * The EntityData and MapData of the game is stored in this AppState to allow
 * other AppStates to retrieve it.
 *
 * @author Eike Foede, roah
 */
public class EntitySystemAppState extends AbstractAppState {

    private EntityData entityData;

    /**
     *
     */
    public EntitySystemAppState() {
        entityData = new DefaultEntityData();
    }

    /**
     *
     * @param ed
     */
    public EntitySystemAppState(EntityData ed) {
        this.entityData = ed;
    }

    /**
     *
     * @return
     */
    public MapData getMapData() {
        ExtendedEntityData ed = (ExtendedEntityData) entityData;
        return ed.getMapData();
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

    /**
     *
     * @return
     */
    public EntityData getEntityData() {
        return entityData;
    }

    /**
     *
     * @return
     */
    public ExtendedEntityData getExtendedEntityData() {
        return (ExtendedEntityData) entityData;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
