package gamestate;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.MeshManager;
import hexsystem.chunksystem.ChunkControl;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import utility.Coordinate.Offset;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede, Roah
 */
public class EditorAppState extends BaseInput implements TileChangeListener {
    private Spatial hexCursor;                  //@todo see HexCursor script.
    private Node mapNode;
    private HashMap chunks = new HashMap();
    private MeshManager meshManager;
    private final float offset = 0.15f;         //Got an offset issue with hex_void_anim.png this will solve it temporary
    

    public EditorAppState(MapData mapData, ElementalAttribut eAttribut, MultiverseMain main) {
        super(main, mapData);
        mapNode = new Node("mapNode");
        main.getRootNode().attachChild(mapNode);
        mapData.setMapElement(eAttribut);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mapData.registerTileChangeListener(this);
        meshManager = new MeshManager(mapData.getHexSettings());
        addEmptyChunk(new Vector2Int(0,0));
        initCursor((MultiverseMain)app);
    }

    @Override
    protected void getleftMouseActionResult(CollisionResult closestCollision) {
        Offset offsetPos = convertWorldToGrid(closestCollision.getContactPoint());
        if(main.isDebugMode()){
            System.out.println(offsetPos.q + "|" +offsetPos.r);
        }
    }

    public void tileChange(TileChangeEvent event) {
        HexTile oldTile = event.getOldTile();
        HexTile newTile = event.getNewTile();
        if (newTile.getHexElement() != oldTile.getHexElement()) {
//            tiles[event.getX()][event.getY()].setMaterial(getMaterialForTile(event.getNewTile()));
//            tiles[event.getX()][event.getY()].setMesh(meshManager.getHeightedTile(event.getNewTile().getHeight()));
        } else if (newTile.getHeight() != oldTile.getHeight()) {
            //TODO: Regenerate mesh
            System.out.println("Work");
//            tiles[event.getX()][event.getY()].setMesh(meshManager.getHeightedTile(event.getNewTile().getHeight()));
        }
    }
    
    private void initCursor(MultiverseMain main) {
        /** Testing cursor */
        hexCursor = main.getAssetManager().loadModel("Models/utility/SimpleSprite.j3o");
        Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
        animShader.setInt("Speed", 16);
        hexCursor.setMaterial(animShader);
        main.getRootNode().attachChild(hexCursor);
        hexCursor.rotate(-FastMath.HALF_PI, FastMath.PI, 0f);
        hexCursor.setLocalTranslation(new Vector3f(0f, 0.01f,offset)); //Remove offset and set it to zero if hex_void_anim.png is not used
        hexCursor.scale(2f);
        hexCursor.setCullHint(Spatial.CullHint.Always);
    }

    private void addEmptyChunk(Vector2Int position) {
        Node chunk = new Node(position.x+"|"+position.y);
        chunk.setLocalTranslation(getChunkPosition(position));
        chunk.addControl(new ChunkControl(main, meshManager, mapData));
        chunks.put(position.x+"|"+position.y, chunk);
        mapNode.attachChild((Spatial) chunks.get(position.x+"|"+position.y));
    }

    private Vector3f getChunkPosition(Vector2Int position) {
        float posX = (position.x*mapData.getHexSettings().getCHUNK_SIZE()) * mapData.getHexSettings().getHEX_WIDTH() - (mapData.getHexSettings().getHEX_WIDTH()/2);
        float posY = (position.y*mapData.getHexSettings().getCHUNK_SIZE()) * (float)(mapData.getHexSettings().getHEX_RADIUS()*1.5);
        float posZ = 0;
        
        return new Vector3f(posX, posY, posZ);
    }
}
