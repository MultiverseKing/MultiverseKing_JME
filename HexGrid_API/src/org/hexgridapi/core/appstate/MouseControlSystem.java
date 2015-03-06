package org.hexgridapi.core.appstate;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.control.GridRayCastControl;
import org.hexgridapi.core.control.TileSelectionControl;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.MouseRayListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;

/**
 * take care of all input hapening on the room grid.
 *
 * @author Eike Foede, roah
 */
public class MouseControlSystem extends AbstractAppState {

//    private final MouseRay mouseRay = new MouseRay();    //@see utility.MouseRay.
    private final Node rootNode;
//    private final float cursorOffset = -0.15f;           //Got an offset issue with hex_void_anim.png this will solve it temporary
    private GridRayCastControl rayCastControl;
    private Application app;
    private ArrayList<TileInputListener> inputListeners = new ArrayList<TileInputListener>();
    private ArrayList<MouseRayListener> rayListeners = new ArrayList<MouseRayListener>(3);
    private TileSelectionControl tileSelectionControl = new TileSelectionControl(this);
//    private Spatial cursor;
    private int listenerPulseIndex = -1;
    private Vector2f lastScreenMousePos = new Vector2f(0, 0);
    private MapData mapData;
    private boolean isLock = false;
    /**
     * Inner Class.
     */
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public void onTileChange(TileChangeEvent... events) {
//            if (cursor == null) {
//                initCursor();
//            }
//            for (int i = 0; i < events.length; i++) {
//                if (new HexCoordinate(cursor.getLocalTranslation()).equals(events[i].getTilePos())) {
//                    cursor.setLocalTranslation(cursor.getLocalTranslation().x,
//                            (events[i].getNewTile() != null ? events[i].getNewTile().getHeight() : 0)
//                            * HexSetting.FLOOR_OFFSET + 0.1f, cursor.getLocalTranslation().z);
//                }
//            }
        }
    };

    public MouseControlSystem(Node rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        mapData = stateManager.getState(MapDataAppState.class).getMapData();
        mapData.registerTileChangeListener(tileChangeListener);
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addListener(tileActionListener, new String[]{"Confirm", "Cancel"});
        /**
         * Activate the RaycastDebug.
         */
        rayCastControl = new GridRayCastControl(app, rootNode, ColorRGBA.Red);
        tileSelectionControl.initialise(app);
    }

    /**
     * Register a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void registerTileInputListener(TileInputListener listener) {
        inputListeners.add(listener);
    }

    /**
     * Remove a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void removeTileInputListener(TileInputListener listener) {
        inputListeners.remove(listener);
    }

    /**
     * Add a listener for the mouse Raycasting.
     *
     * @param listener
     */
    public void registerRayInputListener(MouseRayListener listener) {
        rayListeners.add(listener);
        inputListeners.add(listener);
    }

    /**
     * Remove a listener from the mouse Raycasting.
     *
     * @param listener
     */
    public void removeRayInputListener(MouseRayListener listener) {
        rayListeners.remove(listener);
        inputListeners.remove(listener);
    }

    /**
     * Diseable the input used to interact with the grid.
     */
    public void removeInput() {
        app.getInputManager().removeListener(tileActionListener);
//        clearCursor();
        clearDebug();
    }
    private final ActionListener tileActionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (listenerPulseIndex == -1) {
                if (name.equals("Confirm") && !isPressed) {
                    castRay(MouseInputEventType.LMB);
                } else if (name.equals("Cancel") && !isPressed) {
                    castRay(MouseInputEventType.RMB);
                }
            } else {
                inputListeners.get(listenerPulseIndex).onMouseAction(
                        new MouseInputEvent(MouseInputEventType.PULSE, tileSelectionControl.getSelectedPos(), 
                        mapData.getTile(tileSelectionControl.getSelectedPos()).getHeight(),//new HexCoordinate(cursor.getLocalTranslation()),
                        rayCastControl.get3DRay(GridRayCastControl.CastFrom.MOUSE), null));
            }
        }
    };

    public void clearDebug() {
        rayCastControl.clear();
    }

//    private void initCursor() {
//        if (cursor == null) {
//            cursor = app.getAssetManager().loadModel("Models/animPlane.j3o");
//            Material animShader = app.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
//            animShader.setInt("Speed", 16);
//            cursor.setMaterial(animShader);
//        }
//        if (!rootNode.hasChild(cursor)) {
//            rootNode.attachChild(cursor);
//        }
//        //Remove offset and set it to zero if hex_void_anim.png is not used
////        float z = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, 0, 0)).getHeight() * HexSettings.FLOOR_OFFSET + 0.01f;
////        cursor.setLocalTranslation(new Vector3f(0f, z + 0.01f, cursorOffset));
//    }
    @Override
    public void update(float tpf) {
        if (listenerPulseIndex != -1) {
            Vector2f newMousePos = app.getInputManager().getCursorPosition().normalize();
            if (!newMousePos.equals(lastScreenMousePos)) {
                castRay(MouseInputEventType.PULSE);
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
    public boolean setCursorPulseMode(TileInputListener listener) {
        if (listenerPulseIndex == -1) {
            //We keep track of the listener locking the input.
            if (!inputListeners.contains(listener)) {
                inputListeners.add(listener);
            }
            listenerPulseIndex = inputListeners.indexOf(listener);
            if (initialized) {
                lastScreenMousePos = app.getInputManager().getCursorPosition();
                return true;
            } else {
                return true;
            }
        } else {
            /**
             * We check if the listener calling the pulseMode is the same than
             * the one who activated it. if it is the same we desable the pulse
             * mode.
             */
            if (inputListeners.contains(listener) && inputListeners.indexOf(listener) == listenerPulseIndex) {
                listenerPulseIndex = -1;
                return true;
            } else if (inputListeners.contains(listener) && inputListeners.indexOf(listener) != listenerPulseIndex) {
                Logger.getGlobal().log(Level.WARNING, "{0} : Pulse already locked by : {1} , Lock requested by : {2}",
                        new Object[]{getClass().getName(), inputListeners.get(listenerPulseIndex).getClass().toString(), listener.toString()});
                return false;
            } else {
                Logger.getGlobal().log(Level.WARNING, "{0} : Listener not registered :  {1}.", new Object[]{getClass().getName(), listener.toString()});
                return false;
            }
        }
    }

    private void castRay(MouseInputEventType mouseInput) {
        Ray ray = rayCastControl.get3DRay(GridRayCastControl.CastFrom.MOUSE);
        MouseInputEvent event = null;
        if (!mouseInput.equals(MouseInputEventType.PULSE)) {
                event = callRayActionListeners(mouseInput, ray);
            if (event == null) {
                event = rayCastControl.castRay(ray);
                if (event != null && event.getPosition() != null) { // && !event.getEventPosition().equals(lastHexPos)) {
                    HexTile tile = mapData.getTile(event.getPosition());
                    callMouseInputActionListeners(new MouseInputEvent(event, mouseInput,
                            tile != null ? tile.getHeight() : 0));
                }
            } else {// if (!event.getEventPosition().equals(lastHexPos)) {
                callMouseInputActionListeners(event);
                rayCastControl.setDebugPosition(event.getCollisionResult().getContactPoint());
            }
        } else {
            tileSelectionControl.onMouseAction(rayCastControl.castRay(ray));
        }
    }

    /**
     * @param mouseInput L or R listener to call
     * @param event event to pass
     */
    private void callMouseInputActionListeners(MouseInputEvent event) {
        for (TileInputListener l : inputListeners) {
            l.onMouseAction(event);
        }
    }

    /**
     * @todo When multiple ray listeners run on same time, the closest got the
     * event.
     * @param mouseInputType
     * @param ray
     */
    private MouseInputEvent callRayActionListeners(MouseInputEventType mouseInputType, Ray ray) {
        MouseInputEvent event = null;
        for (MouseRayListener l : rayListeners) {
            event = l.MouseRayInputAction(mouseInputType, ray);
            if (event != null) {
                return event;
            }
        }
        return event;
    }

//    public void setCursor(HexCoordinate tilePos) {
//        moveCursor(tilePos);
//    }
    public TileSelectionControl getSelectionControl() {
        return tileSelectionControl;
    }

//    public int getTileHeight() {
//        HexTile tile = mapData.getTile(tileSelectionControl.getSelectedPos());
//        if (tile != null) {
//            return tile.getHeight();
//        } else {
//            return 0;
//        }
//    }
//
//    public int getTileTextureValue() {
//        HexTile tile = mapData.getTile(tileSelectionControl.getSelectedPos());
//        if (tile != null) {
//            return tile.getTextureKey();
//        } else {
//            return 0;
//        }
//    }
//
//    public String getTileTextureKey() {
//        HexTile tile = mapData.getTile(tileSelectionControl.getSelectedPos());
//        if (tile != null) {
//            return mapData.getTextureValue(tile.getTextureKey());
//        } else {
//            return mapData.getTextureValue(0);
//        }
//    }
//    private void moveCursor(HexCoordinate tilePos) {
////        if(enable <= 0){
//        if (!isLock) {
//            initCursor();
//            Vector3f pos = tilePos.convertToWorldPosition();
//            HexTile tile = mapData.getTile(tilePos);
//            //        cursor.setLocalTranslation(pos.x, (tile != null ? tile.getHeight() * HexSetting.FLOOR_OFFSET : HexSetting.GROUND_HEIGHT * HexSetting.FLOOR_OFFSET)
//            //                + ((tilePos.getAsOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
//            cursor.setLocalTranslation(pos.x, (tile != null ? tile.getHeight() * HexSetting.FLOOR_OFFSET : 0)
//                    + ((tilePos.getAsOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
//        }
//        /**
//         * The cursor real position is not updated on pulseMode.
//         */
////        if (listenerPulseIndex == -1) {
////            lastHexPos = tilePos;
////        }
////        } else {
////            enable--;
////        }
//    }
//    public void clearCursor() {
//        if (cursor != null && rootNode.hasChild(cursor)) {
//            cursor.removeFromParent();
//        }
//    }
//
//    public void lockCursor() {
//        this.isLock = true;
//    }
//
//    public void unlockCursor() {
//        this.isLock = false;
//        moveCursor(lastHexPos);
//    }
    @Override
    public void cleanup() {
        super.cleanup();
//        if (cursor != null) {
//            cursor.removeFromParent();
//        }
        rayCastControl.clear();
        listenerPulseIndex = -1;
        app.getInputManager().removeListener(tileActionListener);
        mapData.removeTileChangeListener(tileChangeListener);
    }
}
