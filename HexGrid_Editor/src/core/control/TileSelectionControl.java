package core.control;

import com.jme3.asset.AssetManager;
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
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.mesh.MeshManager;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class TileSelectionControl extends AbstractControl implements TileChangeListener {
    private static HashMap<Byte, Mesh> singleTile = new HashMap<>();
    private static Material mat;
    private ArrayList<HexCoordinate> coords = new ArrayList<>();

    public TileSelectionControl(AssetManager am, MapData md) {
        if(mat == null){
            mat = am.loadMaterial("Materials/hexMat.j3m");
            mat.setTexture("ColorMap", am.loadTexture("Textures/EMPTY_TEXTURE_KEY.png"));
            mat.setColor("Color", new ColorRGBA(1, 0, 0, 0.3f));
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        }
        md.registerTileChangeListener(this);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if(spatial instanceof Node){
            //initialise
        } else {
            throw new UnsupportedOperationException("spatial must be an instance of node.");
        }
    }
    
    public void addTile(HexCoordinate pos, Byte height){
        if(!coords.contains(pos)){
            Geometry geo = new Geometry(pos.getAsOffset().toString(), getMesh(height));
            geo.setMaterial(mat);
            geo.setLocalTranslation(pos.convertToWorldPosition());
            ((Node)spatial).attachChild(geo);
            coords.add(pos);
        } else {
            ((Node)spatial).getChild(pos.getAsOffset().toString()).removeFromParent();
            coords.remove(pos);
        }
    }

    public int getTileCount() {
        return coords.size();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void tileChange(TileChangeEvent... events) {
        for(int i = 0; i < events.length; i++){
            if(coords.contains(events[i].getTilePos())){
                Spatial tile = ((Node)spatial).getChild(events[i].getTilePos().getAsOffset().toString());
                if(events[i].getNewTile() != null){
                    ((Geometry)tile).setMesh(getMesh(events[i].getNewTile().getHeight()));
//                    tile.setLocalTranslation(event.getTilePos().convertToWorldPosition());
//                    coord.put(event.getTilePos(), event.getNewTile().getHeight());
                } else {
//                    coord.put(event.getTilePos(), (byte)0);
                    ((Geometry)tile).setMesh(getMesh((byte)0));
//                    tile.setLocalTranslation(event.getTilePos().convertToWorldPosition());
                }
            }
        }
    }

    private Mesh getMesh(Byte height) {
        if(!singleTile.containsKey(height)){
            singleTile.put(height, MeshManager.getInstance().getSingleMesh(height));
        }
        return singleTile.get(height);
    }
    
    public void clear(){
        coords.clear();
        for(Spatial s : ((Node)spatial).getChildren()){
            s.removeFromParent();
        }
    }

    public ArrayList<HexCoordinate> getList() {
        return coords;
    }
}
