package hexsystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

/**
 * The MapData of the game is stored in this AppState to allow other AppStates
 * to retrieve it.
 *
 * @deprecated Since GameDataAppState got a reference to it.
 * @author Eike Foede
 */
public class MapDataAppState extends AbstractAppState {

    private MapData mapData;

    /**
     *
     * @param mapData
     */
    public MapDataAppState(MapData mapData) {
        this.mapData = mapData;
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
    public MapData getMapData() {
        return mapData;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
