/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import hexsystem.MapData;
import hexsystem.MeshManager;
import kingofmultiverse.MultiverseMain;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {
    private final MultiverseMain main;
    private final MeshManager meshManager;
    private final MapData mapData;
    private ChunkSpatial chunkSpatial;      //Contain the spatial for the chunk to work with
    
    public ChunkControl(MultiverseMain main, MeshManager meshManager, MapData mapData) {
        this.main = main;
        this.meshManager = meshManager;
        this.mapData = mapData;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if (spatial != null){
            // initialize
            chunkSpatial = new ChunkSpatial(mapData.getHexSettings(), meshManager, main.getAssetManager(), mapData.getMapElement(), (Node)spatial);
        } else {
            // cleanup
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates..
        chunkSpatial.setEnabled(enabled);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
}
