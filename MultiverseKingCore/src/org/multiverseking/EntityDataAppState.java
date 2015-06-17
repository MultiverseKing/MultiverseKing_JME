package org.multiverseking;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;
import org.multiverseking.loader.JSONLoader;

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
        //@deprecated
        app.getAssetManager().registerLoader(JSONLoader.class, "json", "card");
    }

    public EntityData getEntityData() {
        return entityData;
    }
}
