/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
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
import hexsystem.MapData;
import kingofmultiverse.MultiverseMain;
import utility.Coordinate;
import utility.MouseRay;

/**
 *
 * @author roah
 */
abstract class BaseInput extends AbstractAppState {
    private final MouseRay mouseRay;    //@see utility/MouseRay.
    private Spatial mark;
    protected final MultiverseMain main;
    protected final MapData mapData;
    protected final Coordinate coordinate = new Coordinate();
    

    public BaseInput(MultiverseMain main, MapData mapData) {
        this.main = main;
        this.mouseRay = new MouseRay();
        this.mapData = mapData;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        initInput();
        if(this.main.isDebugMode()){
            initMarkDebug();
        }
    }
    
    /**
     * HexMap base input, it not depend on the gameMode or other thing if hexMap
     * is instanced that mean Tiles is or will be instanced so this input too.
     */
    private void initInput() {
        main.getInputManager().addMapping("LeftMouse", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addListener(actionListener, new String[]{"LeftMouse"});
    }
    private final ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("LeftMouse") && isPressed) {
                CollisionResults results = new CollisionResults();
                main.getRootNode().getChild("mapNode").collideWith(mouseRay.get3DRay(main), results);
                if (results.size() != 0) {
                    if (results.size() > 0) {
                        CollisionResult closest = results.getClosestCollision();
                        if(main.isDebugMode()){
                            mark.setLocalTranslation(closest.getContactPoint());
                            main.getRootNode().attachChild(mark);    //TODO Debug to remove.
                        }
                        main.getStateManager().getState(BaseInput.class).getleftMouseActionResult(results.getClosestCollision());
                    } else if (main.getRootNode().hasChild(mark)) {
                        // No hits? Then remove the red mark.
                        if(main.isDebugMode()){
                            main.getRootNode().detachChild(mark);    //TODO Debug to remove.
                        }
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
    
    private void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }
    
    protected final Coordinate.Offset convertWorldToGrid(Vector3f pos) {
        float x = pos.x;
        float z = pos.z+mapData.getHexSettings().getHEX_RADIUS();
        x = x / mapData.getHexSettings().getHEX_WIDTH();

        float t1 = z / mapData.getHexSettings().getHEX_RADIUS(), t2 = FastMath.floor(x + t1);
        float r = FastMath.floor((FastMath.floor(t1 - x) + t2) / 3);
        float q = FastMath.floor((FastMath.floor(2 * x + 1) + t2) / 3) - r;

        return coordinate.new Axial((int) q, (int) r).toOffset();
    }

    abstract void getleftMouseActionResult(CollisionResult closestCollision);
}
