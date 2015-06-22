package org.multiverseking.game.core;

import org.multiverseking.game.exploration.ExplorationSystemGUI;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.appstate.HexGridDefaultApplication;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.GridMouseControlAppState;
import org.hexgridapi.core.geometry.builder.GridParam;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;
import org.multiverseking.EntityDataAppState;
import org.multiverseking.MultiverCoreGUI;
import org.multiverseking.field.exploration.ExplorationSystem;
import org.multiverseking.field.position.HexPositionSystem;
import org.multiverseking.render.RenderSystem;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class KingOfMultiverseMain extends HexGridDefaultApplication implements MultiverCoreGUI {

    public static void main(String[] args) {
        KingOfMultiverseMain app = new KingOfMultiverseMain();
        Logger.getLogger("").setLevel(Level.SEVERE);
        app.start();
    }
    private Screen screen;

    @Override
    public Screen getScreen() {
        return screen;
    }

    @Override
    public void initApp() {
        this.screen = new Screen(this);
        guiNode.addControl(screen);

        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        GridParam param = new GridParam("org/hexgridapi/editor/Textures/HexField/", 
                new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, SquareCoordinate.class, 
                true, false, false, true);
        MapData mapData = new MapData(assetManager, param);
        stateManager.attachAll(
                /**
                 * HexGrid State
                 */
                new MapDataAppState(mapData),
                new GridMouseControlAppState(),
                new HexGridSystem(mapData),
                /**
                 * Entity system State
                 */
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new ExplorationSystem(),
                new ExplorationSystemGUI());
    }
}
