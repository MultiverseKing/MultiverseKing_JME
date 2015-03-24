package test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.RenderManager;
import core.HexMapSystem;
import java.util.logging.Level;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.MapDataAppState;

/**
 * test
 *
 * @author normenhansen, Roah
 */
public class TestMain extends SimpleApplication {
    
    public static void main(String[] args) {
        TestMain app = new TestMain();
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        app.start();
    }
//    private Screen screen;
    private RTSCamera rtsCam;

//    public Screen getScreen() {
//        return screen;
//    }

    public RTSCamera getRtsCam() {
        return rtsCam;
    }

    @Override
    public void simpleInitApp() {
        //Init general input 
        super.inputManager.clearMappings();
        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        //Create a new screen for tonegodGUI to work with.
//        screen = new Screen(this);
//        guiNode.addControl(screen);
        
        rtsCam = new DefaultParam(this, false).getCam();
        
        //init the Entity && Hex System
        initSystem();

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * @todo Init system only when needed.
     */
    public void initSystem() {
        MapData mapData = new MapData(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, assetManager, MapData.GhostMode.GHOST);
        stateManager.attach(new MapDataAppState(mapData));
        stateManager.attach(new HexMapSystem(mapData, assetManager, getRootNode()));
    }
}
