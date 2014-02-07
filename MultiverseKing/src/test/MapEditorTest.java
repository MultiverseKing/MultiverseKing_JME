package test;

import GUI.EditorGUI;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.MapSpatialAppState;
import kingofmultiverse.MultiverseMain;
import utility.MouseRay;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;
import utility.attribut.GameState;

/**
 *
 * @author Eike Foede
 */
public class MapEditorTest extends AbstractAppState {

    private MouseRay mouseRay;    //@see utility/MouseRay.
    private Spatial hexCursor;          //@todo see HexCursor script.
    private Spatial mark;
    private MultiverseMain main;        //used to get some global variable.
    private AppState hexGUI;            //Current GUI used. Maybe pointless ?
    private MapData mapData;
    private MapSpatialAppState spats;
    private final float offset = 0.15f;               //Got an offset issue with hex_void_anim.png this will solve it temporary

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mouseRay = new MouseRay();
        main = (MultiverseMain) app;
        mapData = stateManager.getState(MapData.class);
        spats = stateManager.getState(MapSpatialAppState.class); //variable name should be changed, hard to read as say before
        
//        /** Testing cursor */
//        hexCursor = main.getAssetManager().loadModel("Models/utility/SimpleSprite.j3o");
//        Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
//        animShader.setInt("Speed", 16);
////        Material animShader = main.getAssetManager().loadMaterial("Materials/debugMat.j3m");
//        hexCursor.setMaterial(animShader);
//        main.getRootNode().attachChild(hexCursor);
//        hexCursor.rotate(-FastMath.HALF_PI, FastMath.PI, 0f);
//        hexCursor.setLocalTranslation(new Vector3f(0f, 0.01f,offset)); //Remove offset and set it to zero if hex_void_anim.png is not used
//        hexCursor.scale(2f);
////        hexCursor.setCullHint(Spatial.CullHint.Always);
        
        if (main.getGameState() == GameState.EDITOR) {
            EditorGUI editorGUI = new EditorGUI();
            hexGUI = editorGUI;

            main.getStateManager().attach(hexGUI);

        } else {
            System.out.println("No GUI to initialize"); //Debug
        }
        initInput();
        initMarkDebug();
    }

    /**
     * HexMap base input, it not depend on the gameMode or other thing if hexMap
     * is instanced that mean Tiles is or will be instanced so this input too.
     */
    private void initInput() {
        main.getInputManager().addMapping("LeftMouse", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addListener(actionListener, new String[]{"LeftMouse"});
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("LeftMouse") && isPressed) {
                CollisionResults results = new CollisionResults();
                main.getRootNode().getChild("MapNode").collideWith(mouseRay.get3DRay(main), results);
                if (results.size() != 0) {
                    if (results.size() > 0) {
                        CollisionResult closest = results.getClosestCollision();
                        mark.setLocalTranslation(closest.getContactPoint());
                        main.getRootNode().attachChild(mark);    //TODO Debug to remove.
                        selectedHex(results.getClosestCollision());
                    } else if (main.getRootNode().hasChild(mark)) {
                        // No hits? Then remove the red mark.
                        main.getRootNode().detachChild(mark);    //TODO Debug to remove.
                    } else {
                        System.out.println("no  mark");
                    }
                } else {
                    //Error catching.
                    System.out.println("null raycast");
                }
            }
        }
    };

    protected final void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    private void selectedHex(CollisionResult cr) {

        Vector2Int tilePos = spats.getTilePositionForCoordinate(cr.getContactPoint());

        if(mapData.exist(tilePos)){
            Vector3f cursorPos = spats.getSpatialPositionForTile(tilePos.x, tilePos.y);
            hexCursor.setLocalTranslation(cursorPos.x, hexCursor.getLocalTranslation().y, cursorPos.z + offset); //Remove offset and set it to zero if hex_void_anim.png is not used
            
            if (mapData.getTile(tilePos).getHexElement() == ElementalAttribut.NATURE) {
                mapData.setTile(tilePos.x, tilePos.y, new HexTile(ElementalAttribut.EARTH, 2));
            } else if (mapData.getTile(tilePos).getHexElement() == ElementalAttribut.EARTH) {
                mapData.setTile(tilePos.x, tilePos.y, new HexTile(ElementalAttribut.ICE, 3));
            } else {
                mapData.setTile(tilePos.x, tilePos.y, new HexTile(ElementalAttribut.NATURE, -2));
            }
        } else {
            System.out.println("No hex selected.");
        }

    }
}