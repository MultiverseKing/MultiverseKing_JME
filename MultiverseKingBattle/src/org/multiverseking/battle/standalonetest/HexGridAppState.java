package org.multiverseking.battle.standalonetest;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.geometry.buffer.BufferPositionProvider;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;

/**
 *
 * @author roah
 */
public class HexGridAppState extends AbstractHexGridAppState {

    HexGridAppState(MapData mapData, BufferPositionProvider positionProvider, String texturePath) {
        super(mapData, positionProvider, texturePath);
    }

    @Override
    public void initializeSystem(AppStateManager stateManager, Application app) {
        app.getInputManager().addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        app.getStateManager().attach(new GridMouseControlAppState());
    }

    @Override
    public void updateSystem(float tpf) {
    }

    @Override
    public void cleanupSystem() {
        hexGridAPINode.removeFromParent();
    }
}
