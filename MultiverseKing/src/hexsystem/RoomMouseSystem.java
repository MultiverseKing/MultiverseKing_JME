package hexsystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import hexsystem.events.HexMapRayListener;
import hexsystem.events.TileChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.MouseRay;

/**
 * take care of all input hapening on the room grid.
 *
 * @author Eike Foede, roah
 */
public final class RoomMouseSystem extends AbstractAppState {

    private final MouseRay mouseRay = new MouseRay();    //@see utility/MouseRay.
    private final float cursorOffset = -0.15f;         //Got an offset issue with hex_void_anim.png this will solve it temporary
    private MultiverseMain main;
    private ArrayList<HexMapInputListener> hexMapListeners = new ArrayList<HexMapInputListener>();
    private ArrayList<HexMapRayListener> rayListeners = new ArrayList<HexMapRayListener>(3);
    private Spatial cursor;
    private Spatial rayDebug;
    private int listenerPulseIndex = -1;
    private HexCoordinate lastHexPos;
    private Vector2f lastScreenMousePos = new Vector2f(0, 0);
    private MapData mapData;

    public RoomMouseSystem() {
    }
    public RoomMouseSystem(HexMapInputListener inputListener, HexMapRayListener rayListener) {
        registerRayInputListener(rayListener);
        registerTileInputListener(inputListener);
    }
    public RoomMouseSystem(HexMapRayListener rayListener) {
        registerRayInputListener(rayListener);
    }
    public RoomMouseSystem(HexMapInputListener inputListener) {
        registerTileInputListener(inputListener);
    }
    
    public Spatial getRayDebug() {
        return rayDebug;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.main = (MultiverseMain) app;
        mapData = stateManager.getState(HexSystemAppState.class).getMapData();
        initInput();
        initMarkDebug();
    }

    /**
     * Register a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void registerTileInputListener(HexMapInputListener listener) {
        hexMapListeners.add(listener);
    }

    /**
     * Remove a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void removeTileInputListener(HexMapInputListener listener) {
        hexMapListeners.remove(listener);
    }

    /**
     * Add a listener for the mouse Raycasting.
     *
     * @param listener
     */
    public void registerRayInputListener(HexMapRayListener listener) {
        rayListeners.add(listener);
        hexMapListeners.add(listener);
    }

    /**
     * Remove a listener from the mouse Raycasting.
     *
     * @param listener
     */
    public void removeRayInputListener(HexMapRayListener listener) {
        rayListeners.remove(listener);
        hexMapListeners.remove(listener);
    }

    /**
     * Activate the input to interact with the grid.
     */
    public void initInput() {
        main.getInputManager().addListener(tileActionListener, new String[]{"Confirm", "Cancel"});
    }

    /**
     * Diseable the input used to interact with the grid.
     */
    public void removeInput() {
        main.getInputManager().removeListener(tileActionListener);
        clearCursor();
        clearDebug();
    }
    private final ActionListener tileActionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Confirm") && !isPressed) {
                if (listenerPulseIndex == -1) {
                    castRay("L");
                } else {
                    hexMapListeners.get(listenerPulseIndex).leftMouseActionResult(
                            new HexMapInputEvent(mapData.convertWorldToGridPosition(cursor.getLocalTranslation()), mouseRay.get3DRay(main), null));
                }
            } else if (name.equals("Cancel") && !isPressed) {
                if (listenerPulseIndex == -1) {
                    castRay("R");
                }
            }
        }
    };

    private void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        rayDebug = new Geometry("DebugRayCast", sphere);
        Material mark_mat = new Material(main.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        rayDebug.setMaterial(mark_mat);
    }

    public void clearDebug() {
        rayDebug.removeFromParent();
    }

    private void initCursor() {
        if (cursor == null) {
            cursor = main.getAssetManager().loadModel("Models/utility/animPlane.j3o");
            Material animShader = main.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
            animShader.setInt("Speed", 16);
            cursor.setMaterial(animShader);
        }
        if (!main.getRootNode().hasChild(cursor)) {
            main.getRootNode().attachChild(cursor);
        }
        //Remove offset and set it to zero if hex_void_anim.png is not used
//        float z = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, 0, 0)).getHeight() * HexSettings.FLOOR_OFFSET + 0.01f;
//        cursor.setLocalTranslation(new Vector3f(0f, z + 0.01f, cursorOffset));
    }

    @Override
    public void update(float tpf) {
        if (listenerPulseIndex != -1) {
            Vector2f newMousePos = main.getInputManager().getCursorPosition().normalize();
            if (!newMousePos.equals(lastScreenMousePos)) {
                castRay("0");
                lastScreenMousePos = newMousePos;
            }
        }
    }

    /**
     * Activate the cursor on pulse mode, Raycast will follow the mouse, Have to
     * be called by the the same listener to disable. The pulse mode lock other
     * update.
     *
     * @todo Ray listener support
     * @param listener calling for it.
     * @return false if an error happen or if already on pulseMode.
     */
    public boolean setCursorPulseMode(HexMapInputListener listener) {
        if (listenerPulseIndex == -1) {
            //We keep track of the listener locking the input.
            if (!hexMapListeners.contains(listener)) {
                hexMapListeners.add(listener);
            }
            listenerPulseIndex = hexMapListeners.indexOf(listener);
            lastScreenMousePos = main.getInputManager().getCursorPosition();
            return true;
        } else {
            /**
             * We check if the listener calling the pulseMode is the same than
             * the one who activated it. if it is the same we desable the pulse
             * mode.
             */
            if (hexMapListeners.contains(listener) && hexMapListeners.indexOf(listener) == listenerPulseIndex) {
                listenerPulseIndex = -1;
                return true;
            } else if (hexMapListeners.contains(listener) && hexMapListeners.indexOf(listener) != listenerPulseIndex) {
                System.err.println("Pulse already locked by : " + hexMapListeners.get(listenerPulseIndex).getClass().toString()
                        + ". Lock request by : " + listener.toString());
                return false;
            } else {
                System.err.println("listener not registered : " + listener.toString());
                return false;
            }
        }
    }

    private void castRay(String mouseInput) {
        CollisionResults results = new CollisionResults();
        Ray ray = mouseRay.get3DRay(main);
        HexMapInputEvent event = callRayActionListeners(mouseInput, ray);

        if (event == null) {
            main.getRootNode().getChild("hexMapNode").collideWith(ray, results);
            if (results.size() != 0) {
                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();
                    setDebugPosition(closest.getContactPoint());

                    HexCoordinate newPos = convertMouseCollision(results);
//                    if (newPos != null && !newPos.equals(lastHexPos)) {
                    event = new HexMapInputEvent(newPos, ray, closest);
                    moveCursor(newPos);
                    callMouseActionListeners(mouseInput, event);
//                    }
                }
            } else {
                //Error catching.
                System.out.println("null raycast");
                main.getRootNode().detachChild(rayDebug);
            }
        } else {
//            if (!event.getEventPosition().equals(lastHexPos)) {
            moveCursor(event.getEventPosition());
            callMouseActionListeners(mouseInput, event);
//            }
        }
    }

    /**
     * @param mouseInput L or R listener to call
     * @param event event to pass
     */
    private void callMouseActionListeners(String mouseInput, HexMapInputEvent event) {
        for (HexMapInputListener l : hexMapListeners) {
            if (mouseInput.contains("L")) {
                l.leftMouseActionResult(event);
            } else if ((mouseInput.contains("R"))) {
                l.rightMouseActionResult(event);
            } else {
                return;
            }
        }
    }

    /**
     * @todo When multiple ray listeners run on same time, the closest got the
     * event.
     * @param mouseInput
     * @param ray
     */
    private HexMapInputEvent callRayActionListeners(String mouseInput, Ray ray) {
        HexMapInputEvent event = null;
        for (HexMapRayListener l : rayListeners) {
            if (mouseInput.contains("L")) {
                event = l.leftRayInputAction(ray);
            } else if ((mouseInput.contains("R"))) {
                event = l.rightRayInputAction(ray);
            } else {
                return null;
            }
        }
        return event;
    }

    public void setDebugPosition(Vector3f pos) {
        if (rayDebug != null) {
            if (main.getRootNode().hasChild(rayDebug)) {
                rayDebug.setLocalTranslation(pos);
            } else {
                main.getRootNode().attachChild(rayDebug);
                rayDebug.setLocalTranslation(pos);
            }
        }
    }

    public void setCursor(HexCoordinate tilePos) {
        moveCursor(tilePos);
        enable = 1;
    }
    Byte enable = 0;

    private void moveCursor(HexCoordinate tilePos) {
//        if(enable <= 0){
        initCursor();
        Vector3f pos = tilePos.convertToWorldPosition();
        cursor.setLocalTranslation(pos.x, mapData.getTile(tilePos).getHeight() * HexSettings.FLOOR_OFFSET
                + ((tilePos.getAsOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
        /**
         * The cursor real position is not updated on pulseMode.
         */
        if (listenerPulseIndex == -1) {
            lastHexPos = tilePos;
        }
//        } else {
//            enable--;
//        }
    }

    public void clearCursor() {
        if (cursor != null && main.getRootNode().hasChild(cursor)) {
            cursor.removeFromParent();
        }
    }

    private HexCoordinate convertMouseCollision(CollisionResults rayResults) {
        HexCoordinate tilePos;
        Vector3f pos;
        Iterator<CollisionResult> i = rayResults.iterator();

        do {
            pos = i.next().getContactPoint();
            tilePos = mapData.convertWorldToGridPosition(pos);
            if (mapData.getTile(tilePos) == null) {
                break;
            } else {
                return tilePos;
            }/*else if (mapData.getTile(tilePos).getHeight() 
             * == (byte)FastMath.floor(pos.y/mapData.getHexSettings().getFloorHeight())){
             return tilePos;
             }*/
        } while (i.hasNext());

        return null;
    }

    /**
     *
     * @param event
     */
    public void tileChange(TileChangeEvent event) {
        if (cursor == null) {
            initCursor();
        }
        if (mapData.convertWorldToGridPosition(cursor.getLocalTranslation()).equals(event.getTilePos())) {
            cursor.setLocalTranslation(cursor.getLocalTranslation().x, event.getNewTile().getHeight()
                    * HexSettings.FLOOR_OFFSET + 0.1f, cursor.getLocalTranslation().z);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }
}
