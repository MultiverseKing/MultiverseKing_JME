package kingofmultiverse;

import org.multiversekingesapi.utility.JSONLoader;
import test.Player;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import org.multiversekingesapi.EntityDataAppState;
import editor.EditorSystem;
import hexsystem.area.MapDataAppState;
import javax.swing.JFrame;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.MapData;
import test.EditorMain;
import tonegod.gui.core.Screen;
import org.multiversekingesapi.utility.ElementalAttribut;

/**
 * test
 *
 * @author normenhansen, Roah
 */
public class MultiverseMainEditor extends EditorMain {

    public MultiverseMainEditor(JFrame rootWindow) {
        super(rootWindow);
    }
    
    private Screen screen;

    public Screen getScreen() {
        return screen;
    }

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
//        String userHome = System.getProperty("user.dir") + "/assets/Data/MapData/";
//        assetManager.registerLocator(userHome, ChunkDataLoader.class);
//        assetManager.registerLoader(ChunkDataLoader.class, "chk");
//        
//        assetManager.registerLocator(userHome, MapDataLoader.class);
//        assetManager.registerLoader(MapDataLoader.class, "map");
        
//        assetManager.registerLocator(System.getProperty("user.dir") + "/assets/Data/", JSONLoader.class);
        assetManager.registerLoader(JSONLoader.class, "json", "card");
        
        //Init general input 
//        super.inputManager.clearMappings();
//        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
//        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        //Create a new screen for tonegodGUI to work with.
        screen = new Screen(this);
        guiNode.addControl(screen);
        
//        rtsCam = new GlobalParameter(this, false).getCam();
        
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

    // <editor-fold defaultstate="collapsed" desc="Put on Standby, Exploration mode Stuff.">
    /**
     * Use to generate a character who can move on the field, this will be used
     * for Exploration mode configuration, jme terrain is called with it.
     *
     * @deprecated No need for the physic system.
     */
    private void initPlayer() {
        Player player = new Player();
        stateManager.attach(player);
    }

    private Spatial instanciatePlayer() {
        Spatial player = assetManager.loadModel("Models/Characters/Berserk/export.j3o");
        player.setName("Player");

        player.setLocalTranslation(new Vector3f(0, HexSetting.GROUND_HEIGHT * HexSetting.FLOOR_OFFSET, 0));
        rootNode.attachChild(player);
        return player;
    }
    // </editor-fold>

    /**
     * @todo Init system only when needed.
     */
    public void initSystem() {
        MapData mapData = new MapData(ElementalAttribut.values(), assetManager);
//        MapData mapData = new MapData(assetManager);

        stateManager.attach(new EntityDataAppState());
        stateManager.attach(new EditorSystem());
    }
}
