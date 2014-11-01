package org.hexgridapi.base;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import java.util.HashMap;

/**
 * Directly control the chunk geometry, all tiles geometry.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    private final AssetManager assetManager;
    private final MeshParameter meshParam;
    private final String texturePath;
    
    public ChunkControl(MeshParameter meshParam, AssetManager assetManager, String texturePath) {
        this.meshParam = meshParam;
        this.assetManager = assetManager;
        this.texturePath = texturePath;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            // initialize
            super.setSpatial(spatial);
            update();
        } else if (spatial == null) {
            // cleanup
        } else {
            throw new UnsupportedOperationException("Provided spatial must be a Node.");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * update all tile on the chunk, following the data contained in mapData.
     */
    public void update() {
        if (spatial == null) {
            return;
        }
        /**
         * remove the old tile from the chunk.
         */
        ((Node) spatial).detachAllChildren();
        /**
         * Generate the tile and attach them with the right texture. 
         * 1 geometry by texture.
         */
        HashMap<String, Mesh> mesh = meshParam.getMesh(false, MeshParameter.Shape.SQUARE);

        for (String value : mesh.keySet()) {
            Geometry tile = new Geometry(value, mesh.get(value));
            Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            Texture text;
            if(value.equals("EMPTY_TEXTURE_KEY")) {
                text = assetManager.loadTexture("/Textures/" + value + ".png");
            } else {
                text = assetManager.loadTexture(texturePath + value + ".png");
            }
            text.setWrap(Texture.WrapMode.Repeat);
            mat.setTexture("ColorMap", text);
//            mat.getAdditionalRenderState().setWireframe(true);
            mat.getAdditionalRenderState().setDepthWrite(true);
            tile.setMaterial(mat);
            ((Node) spatial).attachChild(tile);
        }
    }
}