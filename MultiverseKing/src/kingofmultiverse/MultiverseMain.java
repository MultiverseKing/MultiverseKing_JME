package kingofmultiverse;

import utility.JSONLoader;
import test.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import entitysystem.EntityDataAppState;
import editor.EditorSystem;
import hexsystem.area.MapDataAppState;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.hexgridapi.base.HexSetting;
import org.hexgridapi.base.MapData;
import org.hexgridapi.utility.ElementalAttribut;
import tonegod.gui.core.Screen;

/**
 * test
 *
 * @author normenhansen, Roah
 */
public class MultiverseMain extends SimpleApplication {
    
    public static void main(String[] args) {
        MultiverseMain app = new MultiverseMain();
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        app.start();
    }
    private Screen screen;
    private RTSCamera rtsCam;

    public Screen getScreen() {
        return screen;
    }

    public RTSCamera getRtsCam() {
        return rtsCam;
    }

    @Override
    public void simpleInitApp() {
//        String userHome = System.getProperty("user.dir") + "/assets/Data/MapData/";
//        assetManager.registerLocator(userHome, ChunkDataLoader.class);
//        assetManager.registerLoader(ChunkDataLoader.class, "chk");
//        
//        assetManager.registerLocator(userHome, MapDataLoader.class);
//        assetManager.registerLoader(MapDataLoader.class, "map");
        
//        assetManager.registerLocator(System.getProperty("user.dir") + "/assets/Data/", JSONLoader.class);
        assetManager.registerLoader(JSONLoader.class, "json", "card");
        
        //Init general input 
        super.inputManager.clearMappings();
        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        //Create a new screen for tonegodGUI to work with.
        screen = new Screen(this);
        guiNode.addControl(screen);
        
        rtsCam = new GameParameter(this, false).getCam();
        
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

        stateManager.attachAll(
                new EntityDataAppState(),
                new MapDataAppState(mapData)
                //                new HexMapMouseSystem(),
                //                new RenderSystem(),
                //                new MovementSystem(),
                //                new CardRenderSystem(),
                //                new AnimationSystem(),
                //                new CollisionSystem(),
                //                new BattleGUISystem()
                );
        stateManager.attach(new EditorSystem());
    }

    /**
     * Return the first founded key.
     *
     * @param map
     * @param value
     * @return the key associated to a key in a map.
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Return a list of key associated to a value.
     *
     * @param map
     * @param value
     * @return multiple key attached to a value.
     */
    public static <T, E> ArrayList<T> getKeysByValue(Map<T, E> map, E value) {
        ArrayList<T> keyList = new ArrayList<>();
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                keyList.add(entry.getKey());
            }
        }
        if (keyList.isEmpty()) {
            return null;
        } else {
            return keyList;
        }
    }
}
