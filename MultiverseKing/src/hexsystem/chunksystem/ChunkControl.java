package hexsystem.chunksystem;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import hexsystem.HexSettings;
import hexsystem.MapData;
import java.util.ArrayList;
import java.util.Set;
import utility.ElementalAttribut;

/**
 * control the chunk geometry, all tiles geometry.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    private final MeshManager meshManager;
    private final AssetManager assetManager;
    private ArrayList<Geometry> geo = new ArrayList<Geometry>();
    private final MapData mapData;

    /**
     *
     * @param mapData
     * @param meshManager
     * @param assetManager
     * @param mapElement
     */
    public ChunkControl(MapData mapData, MeshManager meshManager, AssetManager assetManager, ElementalAttribut mapElement) {
        this.mapData = mapData;
        this.meshManager = meshManager;
        this.assetManager = assetManager;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if (spatial != null) {
            // initialize
            update();
        } else {
            // cleanup
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * update all tile on the chunk.
     */
    public void update() {
        /**
         * remove the old tile from the chunk.
         */
        Node root = (Node) this.spatial;
        root.detachAllChildren();
        geo.clear();

        /**
         * Generate new parameter to generate the tile.
         */
        MeshParameter meshParam = new MeshParameter(mapData);
        meshParam.initialize(HexSettings.CHUNK_SIZE, false);

        /**
         * Generate the tile and attach them with the right texture. 1 object by
         * element.
         */
        Set<ElementalAttribut> paramElement = meshParam.getAllElementInList();
        for (ElementalAttribut e : paramElement) {
            Geometry tile = new Geometry(e.toString(), meshManager.getMesh(meshParam.setElement(e)));
            Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            Texture text = assetManager.loadTexture("Textures/HexField/" + e.toString() + ".png");
            text.setWrap(Texture.WrapMode.Repeat);
            mat.setTexture("ColorMap", text);
//            mat.getAdditionalRenderState().setWireframe(true);
            mat.getAdditionalRenderState().setDepthWrite(true);
            tile.setMaterial(mat);
            root.attachChild(tile);
        }
    }
}