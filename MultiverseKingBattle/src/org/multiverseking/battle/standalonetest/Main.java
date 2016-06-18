package org.multiverseking.battle.standalonetest;

import com.jme3.renderer.RenderManager;
import java.util.logging.Level;
import org.hexgridapi.core.AbstractHexGridApplication;
import org.hexgridapi.core.MapParam;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.coordinate.SquareCoordinate;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.utility.Vector2Int;
import org.multiverseking.battle.core.BattleSystemTest;
import org.multiverseking.core.MultiverseCoreState;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import tonegod.gui.core.Screen;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends AbstractHexGridApplication implements MultiverseCoreGUI {

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        Main app = new Main();
        app.start();
    }
    
    private RTSCamera rtsCam;
    private HexGridAppState hexGridState;
    private Screen screen;

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void initApp() {
        // initialise hexgrid
        RTSCamera.KeyMapping keyMapping = RTSCamera.KeyMapping.col;
        screen = new Screen(this);
        getGuiNode().addControl(screen);
        rtsCam = new RTSCamera(keyMapping);
        MapData mapData = new MapData(assetManager, new String[]{"EARTH", "ICE", "NATURE", "VOLT"});
        hexGridState = new HexGridAppState(mapData, rtsCam, "org/hexgridapi/assets/Textures/HexField/");
        rootNode.attachChild(hexGridState.getGridNode());
        
        MapParam param = new MapParam(SquareCoordinate.class, new Vector2Int(3, 4),
                7, 1, false, false, 722093121, null);
        hexGridState.setParam(param);
        
//        EntityDataAppState entityDataAppState = new EntityDataAppState();
//        stateManager.attachAll(rtsCam, hexGridState, entityDataAppState,
//                new RenderSystem(), new HexPositionSystem(), 
//                new RenderDebugSystem(), new BattleSystemTest());
        stateManager.attachAll(rtsCam, hexGridState, 
                new MultiverseCoreState(keyMapping, BattleSystemTest.class));

        setDisplayFps(false);
        setDisplayStatView(false);
    }

    @Override
    public Screen getScreen() {
        return screen;
    }
}
