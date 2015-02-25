package org.hexgridapi.core.control;

import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.mesh.MeshGenerator;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.events.TileSelectionListener;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class TileSelectionControl extends AbstractControl {

    private final Node node = new Node("tileSelectionNode");
    private static HashMap<Integer, Mesh> singleTile = new HashMap<Integer, Mesh>();
    private static Material mat;
    private final MouseControlSystem system;
    private ArrayList<HexCoordinate> coords = new ArrayList<HexCoordinate>();
    private ArrayList<TileSelectionListener> listeners = new ArrayList<TileSelectionListener>();
    private boolean isSelectionGroup = false;
    private HexCoordinate selectedTile;
    private CursorControl cursorControl;

    public TileSelectionControl(MouseControlSystem system) {
        this.system = system;
        system.registerTileInputListener(tileInputListener);
    }
    
    public void initialise(Application app){
        if (mat == null) {
            mat = app.getAssetManager().loadMaterial("Materials/hexMat.j3m");
            mat.setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/EMPTY_TEXTURE_KEY.png"));
            mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.3f));
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        }
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addMapping("selectionGrp", new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_RSHIFT));
        app.getInputManager().addListener(keyListener, new String[]{"selectionGrp"});
        app.getInputManager().addListener(mouseListener, new String[]{"Cancel"});
        /**
         * Register listener.
         */
        app.getStateManager().getState(MapDataAppState.class).getMapData().registerTileChangeListener(tileChangeListener);
        cursorControl = new CursorControl(app);
        node.addControl(this);
        ((Node) app.getViewPort().getScenes().get(0)).attachChild(node);
    }
    
    /**
     * Listeners.
     */
    private final ActionListener keyListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("selectionGrp") && isPressed) {
                isSelectionGroup = true;
            } else if (name.equals("selectionGrp") && !isPressed) {
                isSelectionGroup = false;
            }
        }
    };
    private final ActionListener mouseListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Cancel") && !isPressed) {
                clearSelectionGroup();
            }
        }
    };
    private final TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            if (event.getEventType().equals(MouseInputEvent.MouseInputEventType.LMB) && event.getEventPosition() != null) {
//                HexTile tile = mapData.getTile(event.getEventPosition());
                addTile(event.getEventPosition());
//                editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
            } else {
                setSelected(event.getEventPosition());
//                cursorControl.setPosition(event.getEventPosition(), system.getTileHeight(event.getEventPosition()));
            }
        }
    };
    private final TileChangeListener tileChangeListener= new TileChangeListener() {
        @Override
        public void onTileChange(TileChangeEvent... events) {
            for (int i = 0; i < events.length; i++) {
                if (coords.contains(events[i].getTilePos())) {
                    Spatial tile = ((Node) spatial).getChild(events[i].getTilePos().getAsOffset().toString());
                    if (events[i].getNewTile() != null) {
                        ((Geometry) tile).setMesh(getMesh(events[i].getNewTile().getHeight()));
                        cursorControl.setHeight(events[i].getNewTile().getHeight());
                        //                    tile.setLocalTranslation(event.getTilePos().convertToWorldPosition());
                        //                    coord.put(event.getTilePos(), event.getNewTile().getHeight());
                    } else {
                        //                    coord.put(event.getTilePos(), 0);
                        ((Geometry) tile).setMesh(getMesh(0));
                        cursorControl.setHeight(0);
                        //                    tile.setLocalTranslation(event.getTilePos().convertToWorldPosition());
                    }
                }
            }
        }
    };

    public void registerTileListener(TileSelectionListener listener) {
        this.listeners.add(listener);
    }

//    @Override
//    public void setSpatial(Spatial spatial) {
//        super.setSpatial(spatial);
//        if (spatial instanceof Node) {
//            //initialise
//        } else {
//            throw new UnsupportedOperationException("spatial must be an instance of node.");
//        }
//    }

    public void setSpatial(Application app, Spatial spatial){
        super.setSpatial(spatial);
    }
    
    private void addTile(HexCoordinate pos) {
        setSelected(pos);
        if (isSelectionGroup) {
            if (!coords.contains(pos)) {
                Geometry geo = new Geometry(pos.getAsOffset().toString(), getMesh(system.getTileHeight()));
                geo.setMaterial(mat);
                geo.setLocalTranslation(pos.convertToWorldPosition());
                ((Node) spatial).attachChild(geo);
                coords.add(pos);
            } else {
                ((Node) spatial).getChild(pos.getAsOffset().toString()).removeFromParent();
                coords.remove(pos);
            }
        }
        updateListeners();
    }

    private void setSelected(HexCoordinate pos) {
        selectedTile = pos;
        cursorControl.setPosition(pos, system.getTileHeight());
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void updateListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTileSelectionUpdate(selectedTile);
        }
    }

    private Mesh getMesh(int height) {
        if (!singleTile.containsKey(height)) {
            singleTile.put(height, MeshGenerator.getInstance().getSingleMesh(height));
        }
        return singleTile.get(height);
    }

    private void clearSelectionGroup() {
        coords.clear();
        for (Spatial s : ((Node) spatial).getChildren()) {
            s.removeFromParent();
        }
    }

    public HexCoordinate getSelectedPos() {
        return selectedTile;
    }

    public ArrayList<HexCoordinate> getSelectedList() {
        return coords;
    }
}
