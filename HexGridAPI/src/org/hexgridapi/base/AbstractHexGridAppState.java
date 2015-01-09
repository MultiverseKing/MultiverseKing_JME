package org.hexgridapi.base;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

/**
 * This appState handle the connection between "tile data" and "visual tile".
 * @author roah
 */
public abstract class AbstractHexGridAppState extends HexGridManager implements AppState {

    /**
     * Is initilized at the first initilisation.
     */
    private boolean initialized = false;
    /**
     * If enabled the update will run.
     */
    private boolean enabled = true;
    protected MapData mapData;

    public AbstractHexGridAppState(MapData mapData, String texturePath, boolean debugMode) {
        super(mapData, debugMode);
        this.texturePath = texturePath;
        this.mapData = mapData;
    }

    public AbstractHexGridAppState(MapData mapData, Node gridNode, boolean debugMode) {
        super(mapData, gridNode, debugMode);
        this.mapData = mapData;
    }
    
    public final void initialize(AppStateManager stateManager, Application app) {
        mapData.registerChunkChangeListener(this);
        mapData.registerTileChangeListener(this);
        initializeSystem(stateManager, app);
        initialized = true;
    }

    public abstract void initializeSystem(AppStateManager stateManager, Application app);

    public boolean isInitialized() {
        return initialized;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        isSetEnabled(enabled);
    }

    public abstract void isSetEnabled(boolean enabled);
    
    public boolean isEnabled() {
        return enabled;
    }

    public final void update(float tpf) {
        if (initialized && enabled) {
            updateSystem(tpf);
        }
    }

    public abstract void updateSystem(float tpf);

    public void stateAttached(AppStateManager stateManager) {
    }

    public void stateDetached(AppStateManager stateManager) {
    }

    public void render(RenderManager renderManager) {
    }

    public void postRender() {
    }

    public final void cleanup() {
        mapData.removeChunkChangeListener(this);
        mapData.removeTileChangeListener(this);
        gridNode.removeFromParent();
        cleanupSystem();
        enabled = false;
    }

    public abstract void cleanupSystem();
}
