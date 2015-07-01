package org.multiverseking.game.core;

import org.multiverseking.game.exploration.ExplorationSystemGUI;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.AbstractHexGridApplication;
import org.hexgridapi.core.MapParam;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.core.coordinate.SquareCoordinate;
import org.hexgridapi.utility.Vector2Int;
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
public class KingOfMultiverseMain extends AbstractHexGridApplication implements MultiverCoreGUI {

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
        
        MapData mapData = new MapData(assetManager, new String[]{"EARTH", "ICE", "NATURE", "VOLT"});
        RTSCamera rtsCam = new RTSCamera(RTSCamera.KeyMapping.col);
//        rtsCam.setCenter(new Vector3f(20, 15, 18));
//        rtsCam.setRot(120);
        HexGridSystem hexGrid = new HexGridSystem(mapData, rtsCam, "org/hexgridapi/editor/Textures/HexField/");
        
        stateManager.attachAll(
                /**
                 * HexGrid State
                 */
                rtsCam,
                hexGrid,
                new GridMouseControlAppState(),
                /**
                 * Entity system State
                 */
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new ExplorationSystem(),
                new ExplorationSystemGUI());

        MapParam param = new MapParam(SquareCoordinate.class, Vector2Int.ZERO, 12, 1, false, false, true, null);
        hexGrid.setParam(param);
    }
}
