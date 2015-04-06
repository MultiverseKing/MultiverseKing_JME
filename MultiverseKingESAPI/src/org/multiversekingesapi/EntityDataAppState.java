package org.multiversekingesapi;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;
import org.multiversekingesapi.utility.JSONLoader;

/**
 * The EntityData of the game is stored in this AppState to allow
 * other AppStates to retrieve it.
 *
 * @author Eike Foede, roah
 */
public class EntityDataAppState extends AbstractAppState {

    private EntityData entityData;
    
    public EntityDataAppState() {
        entityData = new DefaultEntityData();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        app.getAssetManager().registerLoader(JSONLoader.class, "json", "card");
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
