package gamestate.Editor;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import gamestate.TmpCleanupState;
import hexsystem.MapData;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 * @todo change the way the camera work, make it more rts style to fit the new design.
 * @author Eike Foede, Roah
 */
public class EditorAppState extends TmpCleanupState implements TileChangeListener {

    private final float cursorOffset = -0.15f;         //Got an offset issue with hex_void_anim.png this will solve it temporary
    private final EditorGUI editorGUI;
    private Spatial cursor;
    private Node camTarget = new Node("camFocus");      //for chase cam
    private boolean activeCursor;
    private HexCoordinate offsetPos;

    public HexCoordinate getOffsetPos() {
        return offsetPos;
    }

    public EditorAppState(MapData mapData, MultiverseMain main) {
        super(main, mapData);
        this.editorGUI = new EditorGUI(mapData);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main.getStateManager().attach(editorGUI);
        mapData.registerTileChangeListener(this);
        mapData.addChunk(new Vector2Int(0, 0), null);
        initCursor();
//        initChaseCamera();
//        initInput();
    }

    private void initInput() {
        main.getInputManager().addMapping("ChangeCamFocus", new KeyTrigger(KeyInput.KEY_F));
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
        /**
         * Testing cursor
         */
        cursor = main.getAssetManager().loadModel("Models/utility/animPlane.j3o");
        Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
        animShader.setInt("Speed", 16);
        cursor.setMaterial(animShader);
        main.getRootNode().attachChild(cursor);
        cursor.setLocalTranslation(new Vector3f(0f, mapData.getHexSettings().getGROUND_HEIGHT() + 0.01f, cursorOffset)); //Remove offset and set it to zero if hex_void_anim.png is not used
    }

    private void initChaseCamera() {
        camTarget.setLocalTranslation(cursor.getLocalTranslation());
        main.getRootNode().attachChild(camTarget);
        ChaseCamera chaseCam = new ChaseCamera(main.getCamera(), camTarget, main.getInputManager());
        chaseCam.setMaxDistance(30);
        chaseCam.setMinDistance(2);
        chaseCam.setLookAtOffset(new Vector3f(0f, 1.5f, 0f));
        chaseCam.setSmoothMotion(true);
    }
//    HexCoordinate last = new HexCoordinate(HexCoordinate.AXIAL, 0, 0);
    
    /**
     * Method called each time a left mouse action is done.
     */
    @Override
    protected void mouseLeftActionResult() {
        HexCoordinate offsetPos = super.getLastLeftMouseCollisionGridPos();
        if (offsetPos != null) {
//            changeTile(offsetPos);
            this.offsetPos = offsetPos;
            moveCursor(offsetPos);
            editorGUI.openHexPropertiesWin(offsetPos);
//            Dijkstra da = new Dijkstra();
//            da.setMapData(mapData);
//            List<HexCoordinate> way = da.getPath(last, offsetPos);
//            if (way != null) {
//                for (int i = 0; i < way.size(); i++) {
//                    HexTile hf = mapData.getTile(way.get(i));
//                    HexTile newTile = hf.cloneChangedElement(ElementalAttribut.EARTH);
//                    mapData.setTile(way.get(i), newTile);
//                }
//            }
//            last = offsetPos;

        }
    }

    public void tileChange(TileChangeEvent event) {
        if (mapData.convertWorldToGridPosition(cursor.getLocalTranslation()).equals(event.getTilePos())) {
            cursor.setLocalTranslation(cursor.getLocalTranslation().x, event.getNewTile().getHeight() * mapData.getHexSettings().getFloorHeight(), cursor.getLocalTranslation().z);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        if(activeCursor){
            castRay();
        }
    }

    /**
     * Change the camera focus to the selected position.
     *
     * @param position where the focus should be.
     */
    public void moveCameraFocus(Vector3f position) {
        camTarget.setLocalTranslation(position);
    }

    private void moveCursor(HexCoordinate tilePos) {
        Vector3f pos = mapData.getTileWorldPosition(tilePos);
        Vector2Int offsetPos = tilePos.getAsOffset();
        cursor.setLocalTranslation(pos.x, mapData.getTile(tilePos).getHeight() * mapData.getHexSettings().getFloorHeight() + ((offsetPos.y & 1) == 0 ? 0.001f : 0.002f), pos.z + cursorOffset);
    }

    public void setActivecursor(boolean isActive) {
        super.pauseInput(isActive);
        activeCursor = isActive;
    }
}
