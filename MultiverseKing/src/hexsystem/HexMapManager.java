/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import hexsystem.TilesManager;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import GUI.EditorGUI;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.List;
import kingofmultiverse.MultiverseMain;
import utility.attribut.ElementalAttribut;
import utility.attribut.GameState;
import utility.MouseRay;
import utility.Vector2Int;

/**
 * global control.
 * @author roah
 */
public class HexMapManager extends AbstractAppState{
    private final MouseRay mouseRay;
    private MultiverseMain main;
    private TilesManager tilesManager;
    private Node hexMapNode;
    private Node hexTilesNode;
//    private Vector2Int mapSize;
    private HexCursor hexCursor; //TODO
    private AppState hexGUI;
    private HexTile[][] hexTiles;
    private Geometry mark;
    

    public HexMapManager() {
        mouseRay = new MouseRay();
    }
    
    public Node getHexMapNode() {return this.hexMapNode;}
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        hexMapNode = new Node("HexMap");
        hexTilesNode = new Node("TilesGroup");
        tilesManager = new TilesManager(app.getAssetManager());
        hexMapNode.setLocalTranslation(Vector3f.ZERO);
        hexTilesNode.setLocalTranslation(Vector3f.ZERO);
        hexMapNode.attachChild(hexTilesNode);
        
        if(main.getGameState() == GameState.EDITOR){
            EditorGUI editorGUI = new EditorGUI();
            hexGUI = editorGUI;
            newEditorMap();
             
        } else {
            System.out.println("No GUI to initialize");
        }
        main.getStateManager().attach(hexGUI);
        
        initInput();
        initMarkDebug();
    }
    
    private void newEditorMap(){
        hexTilesNode.attachChild(tilesManager.generateTiles(5,0));
//        hexMapNode.attachChild(hexZoneGenerator.generateEmptyZone(mapSize, hexTiles));
        main.getRootNode().attachChild(hexMapNode);
    }

    /**
     * TODO used to test the chunk alteration..
     * @param closestCollision 
     */
    void moveUpTile(CollisionResult closestCollision) {
//        tilesManager.updateChunk(closestCollision.getGeometry(), closestCollision.getContactPoint().x);
    }
    
    /**
     * TODO
     * @param selectedTile 
     */
    void selectedHex(Vector2Int selectedTile) {
        //The operation depend on what needed.
        //First think to do is to position the cursor at the right place.
        if(hexCursor == null){
            hexCursor = new HexCursor(this);
        } 
    }
    
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
    
    /** A red ball that marks the last spot that was "hit" by the "shot". */
    protected final void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }
    
    private Vector2Int selectedTile(CollisionResult closestCollision){
//        
        int positionY = Integer.parseInt(closestCollision.getGeometry().getName());
//        System.out.println("Chunk position "+positionY);                      //TODO Debug to remove.
        
        float floatTilePosition = closestCollision.getContactPoint().x/tilesManager.getHexWidth();
        int absTilePosition = (int)FastMath.floor(FastMath.abs(floatTilePosition));
        floatTilePosition -= absTilePosition;
        
        if((positionY & 1) == 0 && FastMath.abs(floatTilePosition) > tilesManager.getHexWidth()/4) {
            absTilePosition += 1;
        }
//        System.out.println("Final tile is : "+absTilePosition);               //TODO Debug to remove.
        System.out.println("Array position = "+absTilePosition+";"+positionY);  //TODO Debug to remove.
        return null;
    }

    public void changeZoneElement(String eAttribut) {
//        Node zoneToChange = (Node) hexMapNode.getChild("TilesGroup");
        List<Spatial> chunk = hexTilesNode.getChildren();
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
}