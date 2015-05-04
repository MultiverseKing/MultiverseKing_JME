package org.hexgridapi.core.control;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import java.util.HashMap;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.mesh.GreddyMeshingParameter;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 * @deprecated use {@link TileSelectionControl} instead.
 * @deprecated too much computation for any noticeable improvement.
 * @deprecated does not handle the MeshGenerator. (from 3.0.0)
 */
public class AreaRangeControl extends AbstractControl {

    private final AssetManager assetManager;
    private final GreddyMeshingParameter meshParam;
    private final MapData.GhostMode mode;
    private int radius = 0;
    private HexCoordinate centerPosition;
    private ColorRGBA color;

    public AreaRangeControl(GreddyMeshingParameter meshParam, AssetManager assetManager, MapData.GhostMode mode, HexCoordinate centerPosition, int radius, ColorRGBA color) {
        this.meshParam = meshParam;
        this.assetManager = assetManager;
        this.mode = mode;
        this.centerPosition = centerPosition;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            // initialize
            super.setSpatial(spatial);
            spatial.setShadowMode(RenderQueue.ShadowMode.Off);
//            spatial.setQueueBucket(RenderQueue.Bucket.Transparent);
            update(true);
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
    public void update(boolean updateColor) {
        if (spatial == null || !enabled) {
            return;
        }

        /**
         * Get the old material if any, else load it.
         */
        Material mat;
        if (((Node) spatial).getChildren().isEmpty()) {
            mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            Texture text = assetManager.loadTexture("/Textures/EMPTY_TEXTURE_KEY.png");
            text.setWrap(Texture.WrapMode.Repeat);
            mat.setTexture("ColorMap", text);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            mat.getAdditionalRenderState().setAlphaTest(false);
            mat.getAdditionalRenderState().setAlphaFallOff(0.9f);
        } else {
            mat = ((Geometry) ((Node) spatial).getChild(0)).getMaterial();
        }

        if (updateColor) {
            mat.setColor("Color", color);
//            mat.getAdditionalRenderState().setWireframe(false);
        }
        /**
         * generate the new tile.
         */
        HashMap<String, Mesh> mesh = meshParam.getMesh(centerPosition, radius, true, null);

        /**
         * remove the old tile from the chunk.
         */
        ((Node) spatial).detachAllChildren();

        /**
         * Attach the new tile.
         */
        for (String value : mesh.keySet()) {
            Geometry tile = null;
            if (!value.equals("NO_TILES")) {
                tile = new Geometry(value, mesh.get(value));
                tile.setMaterial(mat);
                ((Node) spatial).attachChild(tile);
            } 
//            else if (debug) {
//                Material tmp = assetManager.loadMaterial("Materials/hexMat.j3m");
//                Texture text = assetManager.loadTexture("/Textures/EMPTY_TEXTURE_KEY.png");
//                text.setWrap(Texture.WrapMode.Repeat);
//                tmp.setTexture("ColorMap", text);
//                tmp.setColor("Color", color);
//                tile = new Geometry(value, mesh.get(value));
//                tmp.getAdditionalRenderState().setWireframe(true);
//                tile.setMaterial(tmp);
//                ((Node) spatial).attachChild(tile);
//            }
            if (tile != null) {
//                tile.setQueueBucket(RenderQueue.Bucket.Inherit);
//                tile.setShadowMode(RenderQueue.ShadowMode.Off);
            }
        }
    }

    public void update(HexCoordinate centerPosition, int radius, ColorRGBA color) {
        this.centerPosition = centerPosition;
        update(checkColor(color));
    }

    private boolean checkColor(ColorRGBA color) {
        if (this.color == null || !color.equals(this.color)) {
            this.color = color;
            return true;
        } else {
            return false;
        }
    }
}
