package hexsystem.area;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.base.AbstractHexGridAppState;
import org.hexgridapi.base.MapData;

/**
 *
 * @author roah
 */
public class AreaGridSystem extends AbstractHexGridAppState {
    
    public AreaGridSystem(MapData mapData) {
        super(mapData, "Textures/HexField/");
    }
    
    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        ((MultiverseMain)app).getRootNode().attachChild(gridNode);
    }

    @Override
    public void isSetEnabled(boolean enabled) {
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    public void cleanupSystem() {
    }
    
}
