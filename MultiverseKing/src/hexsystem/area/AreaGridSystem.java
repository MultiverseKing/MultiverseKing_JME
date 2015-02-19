package hexsystem.area;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.queue.RenderQueue;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.core.appstate.AbstractHexGridAppState;
import org.hexgridapi.core.MapData;

/**
 *
 * @author roah
 */
public class AreaGridSystem extends AbstractHexGridAppState {

    public AreaGridSystem(MapData mapData, boolean debugMode) {
        super(mapData, debugMode);
    }

    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        ((MultiverseMain) app).getRootNode().attachChild(gridNode);
        gridNode.setShadowMode(RenderQueue.ShadowMode.Receive);
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    public void cleanupSystem() {
    }
}
