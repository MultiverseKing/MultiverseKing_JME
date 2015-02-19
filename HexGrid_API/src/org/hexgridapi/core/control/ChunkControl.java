package org.hexgridapi.core.control;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import java.util.HashMap;
import org.hexgridapi.core.mesh.MeshParameter;
import org.hexgridapi.utility.Vector2Int;

/**
 * Directly control the chunk geometry, all tiles geometry.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    private boolean debugMode;
    protected final AssetManager assetManager;
    protected final MeshParameter meshParam;
    protected final String texturePath = "Textures/HexField/";
    protected boolean onlyGround;
    protected Vector2Int chunkPosition;

    public ChunkControl(MeshParameter meshParam, AssetManager assetManager, boolean onlyGround, boolean debugMode, Vector2Int chunkPosition) {
        this.meshParam = meshParam;
        this.assetManager = assetManager;
        this.debugMode = debugMode;
        this.chunkPosition = chunkPosition;
        this.onlyGround = onlyGround;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            // initialize
            super.setSpatial(spatial);
//            spatial.setShadowMode(RenderQueue.ShadowMode.Inherit);
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
//        HashMap<String, Mesh> mesh = meshParam.getMesh(onlyGround);
        setMesh(meshParam.getMesh(onlyGround, debugMode, chunkPosition));
    }

    public void setMesh(HashMap<String, Mesh> mesh) {
        for (String value : mesh.keySet()) {
            Geometry tile = new Geometry(value, mesh.get(value));
            Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            Texture text;
            if (value.equals("EMPTY_TEXTURE_KEY")) {
                text = assetManager.loadTexture("/Textures/" + value + ".png");
            } else if (value.equals("NO_TILE") && debugMode) {
                text = assetManager.loadTexture("/Textures/" + "EMPTY_TEXTURE_KEY" + ".png");
                mat.setColor("Color", ColorRGBA.Blue);
            } else {
                text = assetManager.loadTexture(texturePath + value + ".png");
            }
            text.setWrap(Texture.WrapMode.Repeat);

            mat.setTexture("ColorMap", text);
//            mat.getAdditionalRenderState().setWireframe(true);
//            tile.getMesh().setMode(Mesh.Mode.Points);
            tile.setMaterial(mat);
//            tile.setShadowMode(RenderQueue.ShadowMode.Inherit);
            ((Node) spatial).attachChild(tile);
        }
    }

    public Vector2Int getChunkPosition() {
        return chunkPosition;
    }

    public boolean isEmpty() {
        if (debugMode && ((Node) spatial).getChildren().size() < 2
                || !debugMode && ((Node) spatial).getChildren().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}