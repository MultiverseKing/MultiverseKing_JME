package org.hexgridapi.core.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import org.hexgridapi.core.MapData;

/**
 * Tiles data manager AppState.
 *
 * @author roah
 */
public abstract class AbstractMapDataAppState implements AppState {

    /**
     *
     */
    protected final MapData mapData;
    private boolean initialized = false;
    private boolean enabled = true;

    public AbstractMapDataAppState(MapData mapData) {
        this.mapData = mapData;
    }

    public MapData getMapData() {
        return mapData;
    }

    public final void initialize(AppStateManager stateManager, Application app) {
        initializeSystem(stateManager, app);
        initialized = true;
    }

    protected void initializeSystem(AppStateManager stateManager, Application app){
    }

    public boolean isInitialized() {
        return initialized;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        isSetEnabled(enabled);
    }

    public void isSetEnabled(boolean enabled){   
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public void stateAttached(AppStateManager stateManager) {
    }

    public void stateDetached(AppStateManager stateManager) {
    }

    public final void update(float tpf) {
        if (enabled && initialized) {
            updateSystem(tpf);
        }
    }

    /**
     * Only trigger if enabled && initialized.
     */
    protected void updateSystem(float tpf){
        
    }

    public void render(RenderManager rm) {
    }

    public void postRender() {
    }

    public final void cleanup() {
        cleanupSystem();
        mapData.Cleanup();
        enabled = false;
    }

    public void cleanupSystem(){
    }
}
