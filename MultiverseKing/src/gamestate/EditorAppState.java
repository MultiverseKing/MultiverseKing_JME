package gamestate;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
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
    private HashMap chunks = new HashMap<String, Node>();
    private final float cursorOffset = -0.15f;         //Got an offset issue with hex_void_anim.png this will solve it temporary

    public EditorAppState(MapData mapData, ElementalAttribut eAttribut, MultiverseMain main) {
        super(main, mapData);
        mapData.setMapElement(eAttribut);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mapData.registerTileChangeListener(this);
        addEmptyChunk(new Vector2Int(0,0));
        initCursor((MultiverseMain)app);
    }

    @Override
    protected void mouseLeftActionResult() {
        Offset offsetPos = getLastLeftMouseCollisionGridPos();
        if(offsetPos != null){
            if(main.isDebugMode()){
                System.out.println(offsetPos.q + "|" +offsetPos.r);
            }
            Vector3f tilePos = mapData.getTileWorldPosition(offsetPos);
            hexCursor.setLocalTranslation(new Vector3f(tilePos.x, tilePos.y, tilePos.z + cursorOffset));
//            changeTileElement(offsetPos);
        }
    }

    public void tileChange(TileChangeEvent event) {
        HexTile oldTile = event.getOldTile();
        HexTile newTile = event.getNewTile();
        if (newTile.getHexElement() != oldTile.getHexElement() || newTile.getHeight() != oldTile.getHeight()) {
//            mapNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).changeTileElement(newTile.getHexElement(), event.tilePos());
            mapNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).updateChunk(event.tilePos());
//            tiles[event.getX()][event.getY()].setMaterial(getMaterialForTile(event.getNewTile()));
//            tiles[event.getX()][event.getY()].setMesh(meshManager.getHeightedTile(event.getNewTile().getHeight()));
        } 
        /*else if (newTile.getHeight() != oldTile.getHeight()) {
            //TODO: Regenerate mesh
            System.out.println("Work");
//            tiles[event.getX()][event.getY()].setMesh(meshManager.getHeightedTile(event.getNewTile().getHeight()));
        }*/
    }
    
    private void initCursor(MultiverseMain main) {
        /** Testing cursor */
//        hexCursor = main.getAssetManager().loadModel("Models/utility/SimpleSprite.j3o");
        hexCursor = main.getAssetManager().loadModel("Models/utility/animPlane.j3o");
        Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
        animShader.setInt("Speed", 16);
        hexCursor.setMaterial(animShader);
        main.getRootNode().attachChild(hexCursor);
//        hexCursor.rotate(-FastMath.HALF_PI, FastMath.PI, 0f);
        hexCursor.setLocalTranslation(new Vector3f(0f, 0.01f, cursorOffset)); //Remove offset and set it to zero if hex_void_anim.png is not used
//        hexCursor.scale(2f);
//        hexCursor.setCullHint(Spatial.CullHint.Always);
    }

    private void addEmptyChunk(Vector2Int position) {
        Node chunk = new Node(position.toString());
        chunk.setLocalTranslation(mapData.getChunkWorldPosition(position));
        chunk.addControl(new ChunkControl(main, mapData, meshManager));
        chunks.put(position.toString(), chunk);
        mapData.addEmptyChunk(position);
        mapNode.attachChild(chunk);
    }

    /**
     * @todo height support.
     */
    private void changeTileElement(Offset tilePos) {
        HexTile tile = mapData.getTile(tilePos);
        if(tile != null){
            if (tile.getHexElement() == ElementalAttribut.NATURE) {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.EARTH));
            } else if (tile.getHexElement() == ElementalAttribut.EARTH) {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.ICE));
            } else {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.NATURE));
            }
        } else {
            System.out.println("No hex selected.");
        }
    }
}
