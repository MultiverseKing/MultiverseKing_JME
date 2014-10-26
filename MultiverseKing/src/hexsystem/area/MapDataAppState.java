package hexsystem.area;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import org.hexgridapi.base.AbstractMapDataAppState;
import org.hexgridapi.base.MapData;

/**
 *
 * @author roah
 */
public class MapDataAppState extends AbstractMapDataAppState {

    public MapDataAppState(MapData mapData) {
        super(mapData);
    }
    
    @Override
    protected void initializeSystem(AppStateManager stateManager, Application app) {
    }

    @Override
    public void isSetEnabled(boolean enabled) {
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void cleanupSystem() {
    }
    
}
