package hexsystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 * The MapData of the game is stored in this AppState to allow other
 * AppStates to retrieve it.
 * @author Eike Foede
 */
public class MapDataAppState extends AbstractAppState {

    private MapData mapData;

    public MapDataAppState(MapData mapData) {
        this.mapData = mapData;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }

    public MapData getMapData() {
        return mapData;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
