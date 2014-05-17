package test;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import entitysystem.render.EntityRenderSystem;
import gamestate.GameDataAppState;
import entitysystem.movement.MoveToComponent;
import entitysystem.movement.MovementSystem;
import entitysystem.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import gamestate.HexMapAppState;
import hexsystem.MapData;
import hexsystem.events.ChunkChangeEvent;
import hexsystem.loader.ChunkDataLoader;
import hexsystem.loader.MapDataLoader;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.ElementalAttribut;
import utility.Rotation;

/**
 *
 * @author Eike Foede
 */
public class ExampleStartup extends SimpleApplication {

    /**
     *
     */
    @Override
    public void simpleInitApp() {

        HexCoordinate point = new HexCoordinate(HexCoordinate.OFFSET, -1, 0);
        this.getCamera().lookAt(new Vector3f(0f, 1.5f, 0f), Vector3f.UNIT_Y);
        this.getCamera().setLocation(new Vector3f(0, 21.51f, 17.17051f));
        EntityData entityData = new DefaultEntityData();
        MapData mapData = new MapData(ElementalAttribut.EARTH, assetManager);
        //Initialise data management
        stateManager.attach(new GameDataAppState(entityData));
//        stateManager.attach(new MapDataAppState(mapData));
        stateManager.attach(new HexMapAppState(this, mapData));
        mapData.addChunk(new Vector2Int(0, 0), null);
        mapData.addChunk(new Vector2Int(-1, 0), null);

        String userHome = System.getProperty("user.dir") + "/assets/MapData/";
//        System.out.println(userHome);
        assetManager.registerLocator(userHome, ChunkDataLoader.class);
        assetManager.registerLoader(ChunkDataLoader.class, "chk");
        assetManager.registerLocator(userHome, MapDataLoader.class);
        assetManager.registerLoader(MapDataLoader.class, "map");
        mapData.loadMap("IceLand"); //be carefull...
//        mapData.setTile(point, mapData.getTile(point).cloneChangedHeight(0));
        //Initialise render Systems
//        stateManager.attach(new RenderSystem(new ExampleSpatialInitialiser()));
        stateManager.attach(new MovementSystem());
        stateManager.attach(new EntityRenderSystem());
        stateManager.getState(HexMapAppState.class).chunkUpdate(new ChunkChangeEvent(Vector2Int.INFINITY));
        //TODO: Initialise visual representation of Map

        //TODO: Initialise functional systems
        //examples:
        //GaugeSystem
        //MovementSystem
        //AnimationSystem (maybe rather handled by RenderSystem)


        //Example: Initialise new character entity.
        EntityId characterId = entityData.createEntity();
//        entityData.setComponent(characterId, new SpatialPositionComponent(0, 0, 0));
//        entityData.setComponent(characterId, new RotationComponent(Quaternion.DIRECTION_Z));
        entityData.setComponent(characterId, new RenderComponent("character.j3m"));
        entityData.setComponent(characterId, new HexPositionComponent(new HexCoordinate(HexCoordinate.AXIAL, 0, 0), Rotation.A));
        entityData.setComponent(characterId, new MoveToComponent(new HexCoordinate(HexCoordinate.OFFSET, 20, 20)));
        lightSettup();
        setupCamera();
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        //Nothing to do here that far, everything is handled by external AppStates, mainly EntitySystems
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        ExampleStartup app = new ExampleStartup();
        app.start();
    }

    private void setupCamera() {
        FlyCamAppState fcs = stateManager.getState(FlyCamAppState.class);
        fcs.getCamera().setMoveSpeed(20f);
    }

    private void lightSettup() {
        /**
         * A white, directional light source
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

//        /* this shadow needs a directional light */
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 2);
//        dlsf.setLight(sun);
//        fpp.addFilter(dlsf);
//        viewPort.addProcessor(fpp); 

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
}
