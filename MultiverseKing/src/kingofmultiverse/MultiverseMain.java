package kingofmultiverse;

import hexsystem.MapData;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.lessvoid.nifty.controls.Menu;
import de.lessvoid.nifty.tools.SizeValue;
import gamestate.GameDataAppState;
import entitysystem.ExtendedEntityData;
import entitysystem.render.AnimationSystem;
import entitysystem.card.CardRenderSystem;
import entitysystem.movement.MoveToComponent;
import entitysystem.movement.MovementStatsComponent;
import entitysystem.movement.MovementSystem;
import entitysystem.position.HexPositionComponent;
import entitysystem.render.EntityRenderSystem;
import entitysystem.render.RenderComponent;
import entitysytem.units.FieldSystem;
import gamestate.HexMapAppState;
import gamestate.HexMapMouseInput;
import hexsystem.HexSettings;
import hexsystem.loader.ChunkDataLoader;
import hexsystem.loader.MapDataLoader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.ArrowShape;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector2Int;
import utility.ElementalAttribut;

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

    /**
     *
     * @return
     */
    public Screen getScreen() {
        return screen;
    }

    /**
     *
     */
    @Override
    public void simpleInitApp() {
        String userHome = System.getProperty("user.dir") + "/assets/MapData/";
        assetManager.registerLocator(userHome, ChunkDataLoader.class);
        assetManager.registerLoader(ChunkDataLoader.class, "chk");
        assetManager.registerLocator(userHome, MapDataLoader.class);
        assetManager.registerLoader(MapDataLoader.class, "map");

        // Disable the default flyby cam
        cameraInit();
        
        //Init general input 
        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        

        //Create a new screen for tonegodGUI to work with.
        initScreen();
        lightSettup();
        initSystem();
        
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    /**
     *
     * @param rm
     */
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
    RTSCamera rtsCam;

    private void cameraInit() {
        flyCam.setEnabled(false);
        rtsCam = new RTSCamera(RTSCamera.UpVector.Y_UP);
        rtsCam.setCenter(new Vector3f(8, 15f, 8));
        rtsCam.setRot(120);
        stateManager.attach(rtsCam);
//        flyCam.setMoveSpeed(10);
//        cam.setLocation(new Vector3f(10f, 18f, -5f));
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
        player.setName("Player");

        player.setLocalTranslation(new Vector3f(0, HexSettings.GROUND_HEIGHT * HexSettings.FLOOR_HEIGHT, 0));
        rootNode.attachChild(player);
        return player;
    }

    private void initScreen() {
        screen = new Screen(this);
        guiNode.addControl(screen);
        for (Element e : screen.getElementsAsMap().values()) {
            screen.removeElement(e);
        }
        screen.getElementsAsMap().clear();
    }

    private void initDebug() {
        ArrowShape arrowShape = new ArrowShape(assetManager, rootNode, new Vector3f(0f, 0f, 0f));
    }

    /**
     * @todo Init system only when needed.
     */
    public void initSystem() {
        MapData mapData = new MapData(ElementalAttribut.ICE, assetManager);
        EntityData entityData = new ExtendedEntityData(mapData);
        
        stateManager.attachAll(
                new GameDataAppState(entityData),
                new HexMapAppState(this, mapData),
                new HexMapMouseInput(),
                new EntityRenderSystem(),
                new MainGUI(this),
//                new MapEditorAppState(),
                new MovementSystem(),
                new CardRenderSystem(),
                new AnimationSystem(),
                new FieldSystem());
    }
    private boolean exemple = false;

    @Override
    public void update() {
        super.update();
        if (exemple) {
            MapData md = stateManager.getState(GameDataAppState.class).getMapData();
            md.addChunk(Vector2Int.ZERO, null);
            EntityData ed = stateManager.getState(GameDataAppState.class).getEntityData();
            //Example: Initialise new character entity.
            EntityId characterId = ed.createEntity();
            ed.setComponents(characterId, new RenderComponent("Berserk"),
                    new HexPositionComponent(new HexCoordinate(HexCoordinate.AXIAL, 0, 0), Rotation.A),
                    new MovementStatsComponent(1f, (byte)3),
                    new MoveToComponent(new HexCoordinate(HexCoordinate.OFFSET, 5, 5)));

            exemple = false;
        }
    }

    /**
     *
     * @param <T>
     * @param <E>
     * @param map
     * @param value
     * @return
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
