package gamestate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import hexsystem.MapData;
import java.util.Iterator;
import utility.HexCoordinate;
import utility.MouseRay;

/**
 * TODO: JUST temporarily, has to be better designed!!
 *
 * @Idea : Switch it to hexMapMouseInput, but the dependency to mapData is an
 * issue.
 * @author Eike Foede
 */
public abstract class TmpCleanupState extends AbstractAppState {

    /**
     * Mouse raycast.
     */
    private final MouseRay mouseRay;    //@see utility/MouseRay.
    /**
     * Tiles data manager.
     */
    protected final MapData mapData;
    /**
     * Main application.
     */
    protected final SimpleApplication main;
    /**
     *
     */
    protected CollisionResults lastRayResults;
    private Spatial mark;

    /**
     *
     * @param main
     * @param mapData
     */
    public TmpCleanupState(SimpleApplication main, MapData mapData) {
        this.main = main;
        this.mouseRay = new MouseRay();
        this.mapData = mapData;
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        initMarkDebug();
        initInput();
    }

    /**
     * Base input, it not depend on the gameMode or other thing if hexMap is
     * instanced that mean Tiles is or will be instanced so this input too.
     */
    private void initInput() {
        main.getInputManager().addMapping("LeftMouse", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        main.getInputManager().addListener(tileActionListener, new String[]{"LeftMouse"});
    }

    /**
     *
     * @param pause
     */
    public void pauseInput(boolean pause) {
        if (pause) {
            main.getInputManager().removeListener(tileActionListener);
        } else {
            main.getInputManager().addListener(tileActionListener, new String[]{"LeftMouse"});
        }
    }
    private final ActionListener tileActionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("LeftMouse") && isPressed) {
                castRay();
            }
        }
    };

    /**
     *
     */
    protected void castRay() {
        CollisionResults results = new CollisionResults();
        main.getRootNode().getChild("mapNode").collideWith(mouseRay.get3DRay(main), results);
        if (results.size() != 0) {
            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();

                mark.setLocalTranslation(closest.getContactPoint());
                main.getRootNode().attachChild(mark);    //TODO Debug to remove.

                main.getStateManager().getState(TmpCleanupState.class).setleftMouseActionResult(results);
                main.getStateManager().getState(TmpCleanupState.class).mouseLeftActionResult();
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

    private void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    private void setleftMouseActionResult(CollisionResults results) {
        this.lastRayResults = results;
    }

    /**
     *
     * @return
     */
    protected final HexCoordinate getLastLeftMouseCollisionGridPos() {
        HexCoordinate tilePos;
        Vector3f pos;
        Iterator<CollisionResult> i = lastRayResults.iterator();

        do {
            pos = i.next().getContactPoint();
            tilePos = mapData.convertWorldToGridPosition(pos);
            if (mapData.getTile(tilePos) == null) {
                break;
            } else {
//                System.out.println(pos);
                return tilePos;
            }/*else if (mapData.getTile(tilePos).getHeight() == (byte)FastMath.floor(pos.y/mapData.getHexSettings().getFloorHeight())){
             return tilePos;
             }*/
        } while (i.hasNext());

        return null;
    }

    /**
     *
     */
    abstract protected void mouseLeftActionResult();
}
