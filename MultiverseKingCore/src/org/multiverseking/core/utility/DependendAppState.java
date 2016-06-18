package org.multiverseking.core.utility;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author roah
 */
public abstract class DependendAppState extends AbstractAppState {

    private final Class<? extends AppState>[] states;
    protected SimpleApplication app;
    
    public DependendAppState(Class<? extends AppState>... states) {
        this.states = states;
    }
    
    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        this.app = (SimpleApplication) app;
        /**
         * Initialise all other system needed.
         */
        loadDependency(true);
        initializeSystem(stateManager);
    }
    
    public abstract void initializeSystem(AppStateManager stateManager);

    private void loadDependency(boolean isLoading) {
        AppState state;
        for (Class c : states) {
            state = getState(isLoading, c);
            if (isLoading && state != null) {
                app.getStateManager().attach(state);
            } else if (!isLoading && state != null) {
                app.getStateManager().detach(state);
            }
        }
    }

    private <T extends AppState> AppState getState(boolean enable, Class<? extends AppState> classType) {
        AppState state = app.getStateManager().getState(classType);
        if (enable && state == null) {
            try {
                return classType.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!enable && state != null) {
            return state;
        }
        return null;
    }

    @Override
    public final void cleanup() {
        loadDependency(false);
        cleanupSystem();
    }

    public abstract void cleanupSystem();
}
