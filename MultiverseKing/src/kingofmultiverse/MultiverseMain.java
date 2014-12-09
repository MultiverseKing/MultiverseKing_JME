package kingofmultiverse;

import test.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import entitysystem.EntityDataAppState;
import editor.EditorSystem;
import entitysystem.loader.JSONLoader;
import hexsystem.area.MapDataAppState;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.hexgridapi.base.HexSetting;
import org.hexgridapi.base.MapData;
import org.hexgridapi.utility.ElementalAttribut;
import tonegod.gui.core.Screen;
import utility.ArrowShape;

/**
 * test
 *
 * @author normenhansen, Roah
 */
public class MultiverseMain extends SimpleApplication {

    /**
     *
     * @param args
     */
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
        setPauseOnLostFocus(false);
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

        // Disable the default flyby cam
        cameraInit();

        //Init all light
        lightSettup();
        //init All used System
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

    private void lightSettup() {
        /**
         * A white, directional light source.
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
//        sun.setColor(new ColorRGBA(200/255, 200/255, 200/255, 1));
//        sun.setColor(ColorRGBA.Blue);
        rootNode.addLight(sun);

        /* this shadow needs a directional light */
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 2);
        dlsf.setLight(sun);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);

        /* Drop shadows */
//        final int SHADOWMAP_SIZE = 1024;
//        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
//        dlsr.setLight(sun);
//        viewPort.addProcessor(dlsr);

        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
//        ambient.setColor(ColorRGBA.White);
        ambient.setColor(new ColorRGBA(230 / 255, 230 / 255, 230 / 255, 1));
        rootNode.addLight(ambient);
    }

    private void cameraInit() {
        flyCam.setEnabled(false);
        rtsCam = new RTSCamera(RTSCamera.UpVector.Y_UP);
        rtsCam.setCenter(new Vector3f(8, 15f, 8));
        rtsCam.setRot(120);
        stateManager.attach(rtsCam);
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

    private void initDebug() {
        ArrowShape arrowShape = new ArrowShape(assetManager, rootNode, new Vector3f(0f, 0f, 0f));
    }

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
        ArrayList<T> keyList = new ArrayList<T>();
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
