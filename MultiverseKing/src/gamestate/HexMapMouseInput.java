package gamestate;

import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import hexsystem.HexSettings;
import hexsystem.MapData;
import hexsystem.events.TileChangeEvent;
import java.util.ArrayList;
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
public class HexMapMouseInput extends EntitySystemAppState {

    private final MouseRay mouseRay = new MouseRay();    //@see utility/MouseRay.
    private final float cursorOffset = -0.15f;         //Got an offset issue with hex_void_anim.png this will solve it temporary
    private ArrayList<HexMapInputListener> hexMapListener = new ArrayList<HexMapInputListener>();
    private Spatial cursor;
    private Spatial mark;
    private int currentFocusIndex = -1;
    private HexCoordinate lastHexPos;
    private Vector2f lastMousePos = new Vector2f(0, 0);
    private MapData mapData;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        initMarkDebug();
        initInput();
//        initCursor();
        return entityData.getEntities(HexPositionComponent.class, RenderComponent.class);
    }

    /**
     * Register a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void registerTileInputListener(HexMapInputListener listener) {
        hexMapListener.add(listener);
    }
    /**
     * Remove a listener to respond to Tile Input.
     *
     * @param listener to register.
     */
    public void removeTileInputListener(HexMapInputListener listener) {
        hexMapListener.remove(listener);
    }
    

    /**
     * Base input, it not depend on the gameMode or other thing if hexMap is
     * instanced that mean Tiles is or will be instanced so this input too.
     */
    private void initInput() {
        app.getInputManager().addListener(tileActionListener, new String[]{"Confirm", "Cancel"});
    }

    private void initMarkDebug() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    private void initCursor() {
            cursor = app.getAssetManager().loadModel("Models/utility/animPlane.j3o");
            Material animShader = app.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
            animShader.setInt("Speed", 16);
            cursor.setMaterial(animShader);
            app.getRootNode().attachChild(cursor);
            //Remove offset and set it to zero if hex_void_anim.png is not used
            float z = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, 0, 0)).getHeight() * HexSettings.FLOOR_HEIGHT + 0.01f;
            cursor.setLocalTranslation(new Vector3f(0f,  z +  0.01f , cursorOffset));
            System.out.println(HexSettings.GROUND_HEIGHT * HexSettings.FLOOR_HEIGHT+" + "+z+0.01f);
    }

    @Override
    protected void updateSystem(float tpf) {
        if (currentFocusIndex != -1) {
            Vector2f newMousePos = app.getInputManager().getCursorPosition().normalize();
            if (!newMousePos.equals(lastMousePos)) {
                castRay("0");
                lastMousePos = newMousePos;
            }
        }
    }

    @Override
    protected void addEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateEntity(Entity e) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Activate the cursor on pulse mode, Raycast will follow the mouse.
     *
     * @param lockInput is this the listener locking the input.
     * @param listenerFocus listener to add.
     * @return false
     */
    public boolean setActiveCursor(boolean lockInput, HexMapInputListener listenerFocus) {
        if (lockInput && currentFocusIndex == -1) {
            //We keep track of the listener locking the input.
            if (!hexMapListener.contains(listenerFocus)) {
                hexMapListener.add(listenerFocus);
            }
            currentFocusIndex = hexMapListener.indexOf(listenerFocus);
            lastMousePos = app.getInputManager().getCursorPosition();
            return true;
        } else if (!lockInput && currentFocusIndex != -1) {
            //We check if the listener unlocking the input is the same then the one who locked it.
            if (hexMapListener.contains(listenerFocus) && hexMapListener.indexOf(listenerFocus) == currentFocusIndex) {
                currentFocusIndex = -1;
                return true;
            } else if (hexMapListener.contains(listenerFocus) && hexMapListener.indexOf(listenerFocus) != currentFocusIndex) {
                System.err.println("listener not allowed to unlock the input : " + listenerFocus.toString());
                return false;
            } else {
                System.err.println("listener not registered : " + listenerFocus.toString());
                return false;
            }
        } else if (lockInput && currentFocusIndex != -1) {
            System.err.println("input already locked by : " + hexMapListener.get(currentFocusIndex).toString() 
                    + ". Lock request by : " + listenerFocus.toString());
            return false;
        }
        return true;
    }
    private final ActionListener tileActionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Confirm") && isPressed) {
                castRay("L");
//                System.out.println("Left");
            } else if (name.equals("Cancel") && isPressed) {
                castRay("R");
//                System.out.println("Right");
            }
        }
    };

    private void castRay(String mouseInput) {
        CollisionResults results = new CollisionResults();
        app.getRootNode().getChild("mapNode").collideWith(mouseRay.get3DRay(app), results);
        if (results.size() != 0) {
            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();

                mark.setLocalTranslation(closest.getContactPoint());
                app.getRootNode().attachChild(mark);    //TODO Debug to remove.

                setMouseRayResult(results, mouseInput);
            } else if (app.getRootNode().hasChild(mark)) {
                // No hits? Then remove the red mark.
                app.getRootNode().detachChild(mark);    //TODO Debug to remove.
            } else {
                System.out.println("no  mark");
            }
        } else {
            //Error catching.
            System.out.println("null raycast");
        }
    }

    private void setMouseRayResult(CollisionResults rayResults, String mouseInput) {
        HexCoordinate newPos = getLastLeftMouseCollisionGridPos(rayResults);
        if (newPos != null) {
            if (!newPos.equals(lastHexPos) || mouseInput.contains("L") || mouseInput.contains("R")) {
                HexMapInputEvent event = new HexMapInputEvent(newPos, rayResults);
                moveCursor(event.getEventPosition());
                if (mouseInput.contains("L")) {
                    for (HexMapInputListener l : hexMapListener) {
                        l.leftMouseActionResult(event);
                    }
                } else if (mouseInput.contains("R")) {
                    for (HexMapInputListener l : hexMapListener) {
                        l.rightMouseActionResult(event);
                    }
                }
                lastHexPos = newPos;
            }
        }
    }

    private void moveCursor(HexCoordinate tilePos) {
        if(cursor == null){
            initCursor();
        }
        Vector3f pos = mapData.getTileWorldPosition(tilePos);
        cursor.setLocalTranslation(pos.x, mapData.getTile(tilePos).getHeight() * HexSettings.FLOOR_HEIGHT 
                + ((tilePos.getAsOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
    }

    /**
     *
     * @return
     */
    private HexCoordinate getLastLeftMouseCollisionGridPos(CollisionResults rayResults) {
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
                    * HexSettings.FLOOR_HEIGHT + 0.1f, cursor.getLocalTranslation().z);
        }
    }

    @Override
    protected void cleanupSystem() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
