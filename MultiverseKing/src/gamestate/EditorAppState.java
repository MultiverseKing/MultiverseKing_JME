package gamestate;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.chunksystem.ChunkControl;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate.Offset;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede, Roah
 */
public class EditorAppState extends HexMapAppState implements TileChangeListener{
    private Spatial hexCursor;                  //@todo see HexCursor script.
    private HashMap chunks = new HashMap<String, Node>();
    private ElementalAttribut mapElement;
    private final float cursorOffset = -0.15f;         //Got an offset issue with hex_void_anim.png this will solve it temporary

    public EditorAppState(MapData mapData, ElementalAttribut eAttribut, MultiverseMain main) {
        super(main, mapData);
        mapElement = eAttribut;
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
            Vector3f tilePos = mapData.getTileWorldPosition(offsetPos);
            hexCursor.setLocalTranslation(new Vector3f(tilePos.x, tilePos.y, tilePos.z + cursorOffset));
            changeTileElement(offsetPos);
        }
    }

    public void tileChange(TileChangeEvent event) {
        if (event.getNewTile().getHexElement() != event.getOldTile().getHexElement() || 
                event.getNewTile().getHeight() != event.getOldTile().getHeight()) {
            mapNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).updateChunk(event.tilePos());
        }
    }
    
    private void initCursor(MultiverseMain main) {
        /** Testing cursor */
        hexCursor = main.getAssetManager().loadModel("Models/utility/animPlane.j3o");
        Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
        animShader.setInt("Speed", 16);
        hexCursor.setMaterial(animShader);
        main.getRootNode().attachChild(hexCursor);
        hexCursor.setLocalTranslation(new Vector3f(0f, 0.01f, cursorOffset)); //Remove offset and set it to zero if hex_void_anim.png is not used
    }

    private void addEmptyChunk(Vector2Int position) {
        Node chunk = new Node(position.toString());
        chunk.setLocalTranslation(mapData.getChunkWorldPosition(position));
        chunk.addControl(new ChunkControl(mapData, meshManager, hexMat, mapElement));
        chunks.put(position.toString(), chunk);
        mapData.addEmptyChunk(position, mapElement);
        mapNode.attachChild(chunk);
    }
    
    private void changeTileElement(Offset tilePos) {
        HexTile tile = mapData.getTile(tilePos);
        if(tile != null){
            if (tile.getHexElement() == ElementalAttribut.NATURE) {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.EARTH, (byte)-2));
            } else if (tile.getHexElement() == ElementalAttribut.EARTH) {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.ICE));
            } else {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.NATURE, (byte)3));
            }
        } else {
            System.out.println("No hex selected.");
        }
    }
}
