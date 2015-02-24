package core.control;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
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
import core.event.TileSelectionListener;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.MouseControlAppState;
import org.hexgridapi.core.mesh.MeshManager;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class TileSelectionControl extends AbstractControl {

    private static HashMap<Integer, Mesh> singleTile = new HashMap<>();
    private static Material mat;
    private final MapData mapData;
    private ArrayList<HexCoordinate> coords = new ArrayList<>();
    private ArrayList<TileSelectionListener> listeners = new ArrayList<>();
    private boolean isSelectionGroup;

    /**
     * Inner Class.
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
        public void leftMouseActionResult(MouseInputEvent event) {
            if(event.getEventPosition() != null){
                if (isSelectionGroup) {
                    clearSelectionGroup();
                }
                HexTile tile = mapData.getTile(event.getEventPosition());
                addTile(event.getEventPosition(), tile != null ? tile.getHeight() : 0);
//                editorMainGUI.showCurrentSelectionCount(tileSelectionControl.getTileCount());
            }
        }

        @Override
        public void rightMouseActionResult(MouseInputEvent event) {
        }
    };
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        @Override
        public void tileChange(TileChangeEvent... events) {
            for (int i = 0; i < events.length; i++) {
                if (coords.contains(events[i].getTilePos())) {
                    Spatial tile = ((Node) spatial).getChild(events[i].getTilePos().getAsOffset().toString());
                    if (events[i].getNewTile() != null) {
                        ((Geometry) tile).setMesh(getMesh(events[i].getNewTile().getHeight()));
                        //                    tile.setLocalTranslation(event.getTilePos().convertToWorldPosition());
                        //                    coord.put(event.getTilePos(), event.getNewTile().getHeight());
                    } else {
                        //                    coord.put(event.getTilePos(), 0);
                        ((Geometry) tile).setMesh(getMesh(0));
                        //                    tile.setLocalTranslation(event.getTilePos().convertToWorldPosition());
                    }
                }
            }
        }
    };

    public TileSelectionControl(Application app, MouseControlAppState mc, MapData mapData) {
        if (mat == null) {
            mat = app.getAssetManager().loadMaterial("Materials/hexMat.j3m");
            mat.setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/EMPTY_TEXTURE_KEY.png"));
            mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.3f));
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        }
        this.mapData = mapData;
        /**
         * Activate the input to interact with the grid.
         */
        app.getInputManager().addMapping("selectionGrp", new KeyTrigger(KeyInput.KEY_LSHIFT), new KeyTrigger(KeyInput.KEY_RSHIFT));
        app.getInputManager().addListener(keyListener, new String[]{"selectionGrp"});
        app.getInputManager().addListener(mouseListener, new String[]{"Cancel"});
        /**
         * Register listener.
         */
        mapData.registerTileChangeListener(tileChangeListener);
        mc.registerTileInputListener(tileInputListener);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial instanceof Node) {
            //initialise
        } else {
            throw new UnsupportedOperationException("spatial must be an instance of node.");
        }
    }

    public void addTile(HexCoordinate pos, int height) {
        if (!coords.contains(pos)) {
            Geometry geo = new Geometry(pos.getAsOffset().toString(), getMesh(height));
            geo.setMaterial(mat);
            geo.setLocalTranslation(pos.convertToWorldPosition());
            ((Node) spatial).attachChild(geo);
            coords.add(pos);
        } else {
            ((Node) spatial).getChild(pos.getAsOffset().toString()).removeFromParent();
            coords.remove(pos);
        }
        updateListeners();
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    private void updateListeners(){
        for(int i = 0; i < listeners.size(); i++){
            listeners.get(i).tileSelectUpdate(null);
        }
    }

    private Mesh getMesh(int height) {
        if (!singleTile.containsKey(height)) {
            singleTile.put(height, MeshManager.getInstance().getSingleMesh(height));
        }
        return singleTile.get(height);
    }

    private void clearSelectionGroup() {
        coords.clear();
        for (Spatial s : ((Node) spatial).getChildren()) {
            s.removeFromParent();
        }
    }

    public int getTileCount() {
        return coords.size();
    }

    public ArrayList<HexCoordinate> getTileList() {
        return coords;
    }
}
