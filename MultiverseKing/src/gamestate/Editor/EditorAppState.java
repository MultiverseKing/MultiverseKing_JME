package gamestate.Editor;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import gamestate.HexMapAppState;
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
public class EditorAppState extends HexMapAppState implements TileChangeListener {
//    private EditorCursor cursor;                  //@todo see HexCursor script.
    private HashMap chunks = new HashMap<String, Node>();
    private ElementalAttribut mapElement;
    private Spatial cursor;
    private Node camTarget = new Node("camFocus");
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
        initCursor();
        initCamera();
        initInput();
    }
    
    private void initInput() {
        main.getInputManager().addMapping("ChangeCamFocus", new KeyTrigger(KeyInput.KEY_SPACE));
        main.getInputManager().addListener(editorActionListener, new String[]{"ChangeCamFocus"});
    }
    
    private final ActionListener editorActionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("ChangeCamFocus") && isPressed) {
                moveCameraFocus(cursor.getLocalTranslation());
            }
        }
    };

    private void initCursor() {
        /** Testing cursor */
        cursor = main.getAssetManager().loadModel("Models/utility/animPlane.j3o");
        Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
        animShader.setInt("Speed", 16);
        cursor.setMaterial(animShader);
        main.getRootNode().attachChild(cursor);
        cursor.setLocalTranslation(new Vector3f(0f, 0.01f, cursorOffset)); //Remove offset and set it to zero if hex_void_anim.png is not used
    }
    
    private void initCamera() {
        main.getRootNode().attachChild(camTarget);
        ChaseCamera chaseCam = new ChaseCamera(main.getCamera(), camTarget, main.getInputManager());
        chaseCam.setMaxDistance(30);
        chaseCam.setMinDistance(5);
        chaseCam.setLookAtOffset(new Vector3f(0f, 1.5f, 0f));
        chaseCam.setSmoothMotion(true);
    }
    
    @Override
    protected void mouseLeftActionResult() {
        Offset offsetPos = super.getLastLeftMouseCollisionGridPos();
        if(offsetPos != null){
            changeTile(offsetPos);
            moveCursor(offsetPos);
        }
    }

    public void tileChange(TileChangeEvent event) {
        if (event.getNewTile().getHexElement() != event.getOldTile().getHexElement() || 
                event.getNewTile().getHeight() != event.getOldTile().getHeight()) {
            mapNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).updateChunk(event.getTilePos());
        }
        if(mapData.convertWorldToGridPosition(cursor.getLocalTranslation()).equalsCoord(event.getTilePos())){
            cursor.setLocalTranslation(cursor.getLocalTranslation().x, event.getNewTile().getHeight()*mapData.getHexSettings().getFloorHeight(), cursor.getLocalTranslation().z);
        }
    }
    
    private void changeTile(Offset tilePos) {
        HexTile tile = mapData.getTile(tilePos);
        if(tile != null){
            if (tile.getHexElement() == ElementalAttribut.NATURE) {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.EARTH, (byte)-2));
            } else if (tile.getHexElement() == ElementalAttribut.EARTH) {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.ICE));
            } else {
                mapData.setTile(tilePos, new HexTile(ElementalAttribut.NATURE, (byte)5));
            }
        } else {
            System.out.println("No hex selected.");
        }
    }
    
    private void addEmptyChunk(Vector2Int position) {
        Node chunk = new Node(position.toString());
        chunk.setLocalTranslation(mapData.getChunkWorldPosition(position));
        chunk.addControl(new ChunkControl(mapData, meshManager, hexMat, mapElement));
        chunks.put(position.toString(), chunk);
        mapData.addEmptyChunk(position, mapElement);
        mapNode.attachChild(chunk);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        
    }
    
    public void moveCameraFocus(Vector3f position){
        camTarget.setLocalTranslation(position.x, 0, position.z);
    }

    private void moveCursor(Offset offsetPos) {
        Vector3f pos = mapData.getTileWorldPosition(offsetPos);
        cursor.setLocalTranslation(pos.x, mapData.getTile(offsetPos).getHeight()*mapData.getHexSettings().getFloorHeight()+0.001f, pos.z+cursorOffset);
    }
}
