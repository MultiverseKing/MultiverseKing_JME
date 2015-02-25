package org.hexgridapi.core.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.MapData;

/**
 * This appState handle the connection between "tile data" and "visual tile".
 *
 * @author roah
 */
public abstract class AbstractHexGridAppState extends HexGrid implements AppState {

    /**
     * Is initilized at the first initilisation.
     */
    private boolean initialized = false;
    /**
     * If enabled the update will run.
     */
    private boolean enabled = true;

    /**
     *
     * @param mapData tile dataHandler of the grid.
     * @param enableGhostTile is inexisting tile should be generated as ghost ?
     * @param debugMode generate the grid on wireframe
     */
    public AbstractHexGridAppState(MapData mapData, Node rootNode, boolean debugMode) {
        super(mapData, rootNode, debugMode);
    }

    public final void initialize(AppStateManager stateManager, Application app) {
//        mapData.registerChunkChangeListener(this);
//        mapData.registerTileChangeListener(this);
//        ((Node)app.getViewPort().getScenes().get(0)).attachChild(gridNode);
        initializeSystem(stateManager, app);
        initialized = true;
    }

    public abstract void initializeSystem(AppStateManager stateManager, Application app);

    public boolean isInitialized() {
        return initialized;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

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

    @Override
    public final void cleanup() {
//        gridNode.removeFromParent();
        cleanupSystem();
        initialized = false;
        enabled = false;
        super.cleanup();
    }

    public abstract void cleanupSystem();
}
