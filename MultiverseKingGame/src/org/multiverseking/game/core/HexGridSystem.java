package org.multiverseking.game.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.appstate.AbstractHexGridAppState;

/**
 *
 * @author roah
 */
public class HexGridSystem extends AbstractHexGridAppState {

    public HexGridSystem(MapData mapData) {
        super(mapData);
    }

    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        ((SimpleApplication)app).getRootNode().attachChild(hexGridAPINode);
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    public void cleanupSystem() {
        hexGridAPINode.removeFromParent();
    }
}
