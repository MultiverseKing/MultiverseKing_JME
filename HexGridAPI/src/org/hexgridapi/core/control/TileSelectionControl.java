package org.hexgridapi.core.control;

import com.jme3.app.Application;
import com.jme3.asset.TextureKey;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
public class TileSelectionControl implements TileInputListener {

    private final Node node = new Node("tileSelectionNode");
    private static HashMap<Integer, Mesh> singleTile = new HashMap<Integer, Mesh>();
    private static Material mat;
    private ArrayList<HexCoordinate> coords = new ArrayList<HexCoordinate>();
    private ArrayList<TileSelectionListener> listeners = new ArrayList<TileSelectionListener>();
    private boolean isSelectionGroup = false;
    private HexCoordinate selectedTile;
    private CursorControl cursorControl;

    public void initialise(Application app) {
        if (mat == null) {
            mat = app.getAssetManager().loadMaterial("Materials/hexMat.j3m");
//            mat.setTexture("ColorMap", app.getAssetManager().loadTexture(new TextureKey("Textures/EMPTY_TEXTURE_KEY.png", false)));
//            mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.3f));
            mat.setTexture("ColorMap", app.getAssetManager().loadTexture(new TextureKey("Textures/EMPTY_TEXTURE_KEY.png", false)));
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
        app.getStateManager().getState(MouseControlSystem.class).registerTileInputListener(this);
        app.getStateManager().getState(MapDataAppState.class).getMapData().registerTileChangeListener(tileChangeListener);
        cursorControl = new CursorControl(app);
        /**
         * @todo attach to hexGridNode and not to rootNode
         */
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
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        @Override
        public void onTileChange(TileChangeEvent... events) {
            for (int i = 0; i < events.length; i++) {
                if (coords.contains(events[i].getTilePos())) {
                    Spatial tile = node.getChild(events[i].getTilePos().toOffset().toString());
                    if (events[i].getNewTile() != null) {
                        ((Geometry) tile).setMesh(getMesh(events[i].getNewTile().getHeight()));
                        cursorControl.setHeight(events[i].getNewTile().getHeight());
                        //                    tile.setLocalTranslation(event.getTilePos().toWorldPosition());
                        //                    coord.put(event.getTilePos(), event.getNewTile().getHeight());
                    } else {
                        //                    coord.put(event.getTilePos(), 0);
                        ((Geometry) tile).setMesh(getMesh(0));
                        cursorControl.setHeight(0);
                        //                    tile.setLocalTranslation(event.getTilePos().toWorldPosition());
                    }
                } else if (selectedTile.equals(events[i].getTilePos()) && events[i].getNewTile() != null) {
                    cursorControl.setHeight(events[i].getNewTile().getHeight());
                }
            }
        }
    };

    public void registerTileListener(TileSelectionListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeTileListener(TileSelectionListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onMouseAction(MouseInputEvent event) {
        if (event.getType().equals(MouseInputEvent.MouseInputEventType.LMB) && event.getPosition() != null) {
//                HexTile tile = mapData.getTile(event.getEventPosition());
            addTile(event.getPosition(), event.getHeight());
//                editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
        } else if (event.getPosition() != null) {
            setSelected(event.getPosition(), event.getHeight());
//                cursorControl.setPosition(event.getEventPosition(), system.getTileHeight(event.getEventPosition()));
        }
    }

    private void addTile(HexCoordinate pos, int height) {
        if (isSelectionGroup) {
//            if (coords.isEmpty() && selectedTile != null) {
//                addGeo(selectedTile, selectedTileHeight);
//                coords.add(selectedTile);
//            }
            if (!coords.contains(pos)) {
                addGeo(pos, height);
                coords.add(pos);
            } else {
                node.getChild(pos.toOffset().toString()).removeFromParent();
                coords.remove(pos);
            }
        }
        setSelected(pos, height);
    }

    private void setSelected(HexCoordinate pos, int height) {
        selectedTile = pos;
        cursorControl.setPosition(pos, height);
        updateListeners();
    }

    private void addGeo(HexCoordinate pos, int height) {
        Geometry geo = new Geometry(pos.toOffset().toString(), getMesh(height));
        geo.setMaterial(mat);
        geo.setLocalTranslation(pos.toWorldPosition());
        node.attachChild(geo);

    }

    private void updateListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTileSelectionUpdate(selectedTile, coords);
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
        for (Spatial s : node.getChildren()) {
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
