package kingofmultiverse;

import gamestate.gui.MainGUI;
import hexsystem.MapData;
import gamestate.EditorAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import hexsystem.HexSettings;
import java.util.logging.Level;
import utility.attribut.GameMode;
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
    private GameMode currentMode;
    private boolean debugMode;
    
    public boolean isDebugMode() {
        return debugMode;
    }
    public Screen getScreen() {
        return screen;
    }
    public GameMode getCurrentMode() {
        return currentMode;
    }
    public void setCurrentMode(GameMode newMode) {
        this.currentMode = newMode;
    }

    @Override
    public void simpleInitApp() {
        // Disable the default flyby cam
        flyCam.setEnabled(false);
        debugMode = true;
        initGUI();
        lightSettup();
        generateHexMap();
        if(debugMode){
            initDebug();
        }
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
        sun.setDirection((new Vector3f(-0.5f, -0.5f, 0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        /* Drop shadows */
        final int SHADOWMAP_SIZE = 1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);

        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.Yellow);
        rootNode.addLight(ambient);
    }

    private void cameraSettup(Node target) {
        // Enable a chase cam for this target (typically the player).
        ChaseCamera chaseCam = new ChaseCamera(cam, target, this.inputManager);
        chaseCam.setMaxDistance(30);
        chaseCam.setMinDistance(5);
        chaseCam.setLookAtOffset(new Vector3f(0f, 1.5f, 0f));
        chaseCam.setSmoothMotion(true);
    }

    /**
     * Use to generate a character who can move on the field, this will be used
     * for Exploration mode configuration, jme terrain is called with it.
     * @deprecated No need for the physic system.
     */
    private void initPlayer() {
        Player player = new Player();
        stateManager.attach(player);
    }

    private Spatial instanciatePlayer() {
        Spatial player = assetManager.loadModel("Models/Characters/Model21.j3o");
        player.setName("Player");
        player.setShadowMode(RenderQueue.ShadowMode.Cast);

//        int x = FastMath.nextRandomInt(2,9);
//        int y = FastMath.nextRandomInt(2, 9);

        player.setLocalTranslation(Vector3f.ZERO);
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
        MapData mapData = new MapData(new HexSettings());
        stateManager.attach(mapData);
        EditorAppState editorAppState = new EditorAppState(mapData, ElementalAttribut.ICE, this);
        stateManager.attach(editorAppState);
        
        cameraSettup((Node) instanciatePlayer());
        stateManager.detach(stateManager.getState(MainGUI.class));
    }
}
