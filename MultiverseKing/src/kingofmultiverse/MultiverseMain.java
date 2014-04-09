package kingofmultiverse;

import hexsystem.MapData;
import gamestate.Editor.EditorAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Caps;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import gamestate.HexMapAppState;
import hexsystem.HexSettings;
import hexsystem.loader.ChunkDataLoader;
import hexsystem.loader.MapDataLoader;
import java.util.logging.Level;
import tonegod.gui.core.Screen;
import utility.ArrowShape;
import utility.attribut.ElementalAttribut;

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

    public Screen getScreen() {
        return screen;
    }

    @Override
    public void simpleInitApp() {
        if (!renderManager.getRenderer().getCaps().contains(Caps.TextureArray)) {
            throw new UnsupportedOperationException("Your hardware does not support TextureArray");
        }

        String userHome = System.getProperty("user.dir") + "/assets/MapData/";
//        System.out.println(userHome);
        assetManager.registerLocator(userHome, ChunkDataLoader.class);
        assetManager.registerLoader(ChunkDataLoader.class, "chk");
        assetManager.registerLocator(userHome, MapDataLoader.class);
        assetManager.registerLoader(MapDataLoader.class, "map");

        // Disable the default flyby cam
        flyCam.setEnabled(false);

        initGUI();
        lightSettup();
        generateHexMap();
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
         * A white, directional light source
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
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
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
    }

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

    private Spatial instanciatePlayer(HexSettings hexSettings) {
        Spatial player = assetManager.loadModel("Models/Characters/Berserk/export.j3o");
//        Material mat = assetManager.loadMaterial("Materials/Characters/Berserk/Model_LP3.j3m");
//        player.setMaterial(mat);
        player.setName("Player");
//        player.setShadowMode(RenderQueue.ShadowMode.Cast);

//        int x = FastMath.nextRandomInt(2,9);
//        int y = FastMath.nextRandomInt(2, 9);

        player.setLocalTranslation(new Vector3f(0, hexSettings.getGROUND_HEIGHT() * hexSettings.getFloorHeight(), 0));
        rootNode.attachChild(player);
        return player;
    }

    private void initGUI() {
        screen = new Screen(this);
        this.getGuiNode().addControl(screen);
        MainGUI mainGUI = new MainGUI(this);
        stateManager.attach(mainGUI);
    }

    private void initDebug() {
        ArrowShape arrowShape = new ArrowShape(assetManager, rootNode, new Vector3f(0f, 0f, 0f));
    }

    public void generateHexMap() {
        MapData mapData = new MapData(ElementalAttribut.ICE, assetManager);
        EditorAppState editorAppState = new EditorAppState(mapData, this);
        stateManager.attach(new HexMapAppState(this,mapData));
        stateManager.attach(editorAppState);
        instanciatePlayer(mapData.getHexSettings());

//        chaseCameraSettup((Node) instanciatePlayer());
//        stateManager.detach(stateManager.getState(MainGUI.class));
    }
}
