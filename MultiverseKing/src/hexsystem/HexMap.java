/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import GUI.EditorGUI;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.List;
import kingofmultiverse.MultiverseMain;
import utility.Coordinate;
import utility.attribut.GameState;
import utility.MouseRay;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * global hexfield controller, work at all time mainly.
 * @todo Adding AppState corresponding to -> BattleMode - ExplorationMode.
 * @todo Finish EditorMode then do BattleMode -> ExplorationMode.
 * @author roah
 */
public class HexMap extends AbstractAppState{
    private final MouseRay mouseRay;    //@see utility/MouseRay.
    private final Coordinate coordinate;
    private MultiverseMain main;        //used to get some global variable.
    private MeshManager meshManager;    //@see MeshManager.
    private Node hexMapNode;            //Could be remove ?
    private Node tilesNode;             //Keep track of all Tiles currently instanced.
    private Spatial hexCursor;          //@todo see HexCursor script.
    private AppState hexGUI;            //Current GUI used. Maybe pointless ?
    private List<HexTile> hexTiles;       //Will be used later for faster calculation, mainly neighbourg, pathfinding etc...
    private Geometry mark;              //Debug purpose, show where the mouse ray collide, sould be mouved to utility/MouseRay ?
    

    public HexMap() {
        mouseRay = new MouseRay();
        coordinate = new Coordinate();
    }
    
    public Node getHexMapNode() {return this.hexMapNode;}   //To avoid search tought rootnode.
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        hexMapNode = new Node("HexMap");
        tilesNode = new Node("Tiles");              //HexMap could have other thing attach to it so All tile will be grouped under this node.
        meshManager = new MeshManager();
//      As a bit paranoiaque we put the position Node to Zero, maybe redundant ?
        hexMapNode.setLocalTranslation(Vector3f.ZERO);
        tilesNode.setLocalTranslation(Vector3f.ZERO);
        hexMapNode.attachChild(tilesNode);
       
        /**
         * As we don't know the current gameState before runtime and each gameState got his own GUI
         * we look what is the current gameState then we instantiate the needed GUI, we store it so we can access later to.
         * This have to be change to use a switch, at final stage the game could only start on three type of mode :
         * BATTLE       -> If the player are playing in arena mode.
         * EXPLORATION  -> If the player are playing normaly.
         * EDITOR       -> Should be only used by develloper since it's pointless for player, have to be hidden.
         */
        if(main.getGameState() == GameState.EDITOR){
            EditorGUI editorGUI = new EditorGUI();
            hexGUI = editorGUI;
            newEditorMap();
             
        } else {
            System.out.println("No GUI to initialize"); //Debug
        }
        main.getStateManager().attach(hexGUI);
        
        initInput();
        initMarkDebug();
    }
    
    private void newEditorMap(){
        hexTiles.add(new HexTile(coordinate.new Offset(0, 0), ElementalAttribut.EARTH, 0));
    }
    
    /**
     * @todo lot of thing...
     * @param selectedTile 
     */
    void selectedHex(Vector2Int selectedTile) {
        //The operation depend on what needed.
        //First think to do is to set the cursor position where the player left clicked.
        setCursor(selectedTile);
    }
    
    /**
     * HexMap base input, it not depend on the gameMode or other thing if hexMap 
     * is instanced that mean Tiles is or will be instanced so this input too.
     */
    private void initInput() {
        main.getInputManager().addMapping("LeftMouse", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addListener(actionListener, new String[] {"LeftMouse"});
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals("LeftMouse") && isPressed){
                CollisionResults results = new CollisionResults();
                main.getRootNode().getChild("HexMap").collideWith(mouseRay.get3DRay(main), results);
                if(results.size() != 0){
                    if (results.size() > 0){
                        CollisionResult closest = results.getClosestCollision();
                        mark.setLocalTranslation(closest.getContactPoint());
                        main.getRootNode().attachChild(mark);    //TODO Debug to remove.
                        selectedHex(selectedTile(results.getClosestCollision()));
                    } else if (main.getRootNode().hasChild(mark)){
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
    
    /** 
     * A red ball that marks the last spot that was "hit" by the "shot".
     * Should be moved in utility/MouseRay ?
     */
    protected final void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }
    
    /**
     * Convert the Ray world collision to hexGrid position so we can use Tiles Array to get the needed hex/Tiles.
     * @param closestCollision Last collision with the mouse.
     * @return HexGrid Position.
     */
    private Vector2Int selectedTile(CollisionResult closestCollision){
//        
        int positionY = Integer.parseInt(closestCollision.getGeometry().getName());
//        System.out.println("Chunk position "+positionY);                      //TODO Debug to remove.
        
        float floatTilePosition = closestCollision.getContactPoint().x/meshManager.getHexWidth();
        int absTilePosition = (int)FastMath.floor(FastMath.abs(floatTilePosition));
        floatTilePosition -= absTilePosition;
        
        if((positionY & 1) == 0 && FastMath.abs(floatTilePosition) > meshManager.getHexWidth()/4) {
            absTilePosition += 1;
        }
//        System.out.println("Final tile is : "+absTilePosition);               //TODO Debug to remove.
        System.out.println("Array position = "+absTilePosition+";"+positionY);  //TODO Debug to remove.
        return null;
    }

    /**
     * Change all Tiles textures/materials, visual effect of a selected Zone.
     * @todo Have to change a zone not all the Map.
     * @param eAttribut Element choose.
     */
    public void changeZoneElement(String eAttribut) {
//        Node zoneToChange = (Node) hexMapNode.getChild("TilesGroup");
        List<Spatial> chunk = tilesNode.getChildren();
        Geometry g;
        int o =0;
        Texture2D tex = (Texture2D) main.getAssetManager().loadTexture("Textures/Test/"+eAttribut+"Center.png");
        tex.setWrap(Texture.WrapMode.Repeat);
        for(Spatial i : chunk){
            g  = (Geometry)i;
//            g.getMaterial().getParam("Diffuse").setValue(tex);
            g.getMaterial().setTexture("ColorMap", tex);
//            g.getMaterial().setBoolean("repeat", true);
            System.out.println(o);
            o++;
        }
    }

    private void setCursor(Vector2Int selectedTile) {
//        if(hexCursor == null) {
            hexCursor = (Spatial)main.getAssetManager().loadModel("Models/utility/AnimPlane.j3o");
            main.getRootNode().attachChild(hexCursor);
//        }
        hexCursor.setLocalTranslation(new Vector3f(0, -0.5f, 0));
    }
}