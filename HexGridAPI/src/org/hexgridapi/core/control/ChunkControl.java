package org.hexgridapi.core.control;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
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
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.MapData.GhostMode;
import org.hexgridapi.core.mesh.GreddyMeshingParameter;
import org.hexgridapi.utility.Vector2Int;

/**
 * Directly control the chunk geometry, all tiles geometry.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    protected final GhostMode mode;
    protected final AssetManager assetManager;
    protected final GreddyMeshingParameter meshParam;
    protected final Material hexMaterial;
    protected boolean onlyGround;
    protected Vector2Int chunkPosition;

    /**
     * Crete a new chunk.
     *
     * @param meshParam instance of mesh generator to use.
     * @param assetManager used to load texture and materials.
     * @param onlyGround used to know if depth have to be added to the chunk.
     * @param debugMode used to know if null tile need to be generated.
     * @param chunkPosition initial position.
     */
    public ChunkControl(GreddyMeshingParameter meshParam, AssetManager assetManager,
            Material hexMaterial, GhostMode mode, Vector2Int chunkPosition, boolean onlyGround) {
        this.hexMaterial = hexMaterial;
//        if (hexMaterial.getKey().getExtension().equals("j3md")) {
//        } else {
//            System.err.println("matCLone");
//            this.hexMaterial = hexMaterial.clone();
//        }
        this.meshParam = meshParam;
        this.assetManager = assetManager;
        this.mode = mode;
        this.chunkPosition = chunkPosition;
        this.onlyGround = onlyGround;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        if (spatial != null && spatial instanceof Node) {
            // initialize
            super.setSpatial(spatial);
            ((Node) spatial).attachChild(new Node("TILES.0|0"));
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
        ((Node) ((Node) spatial).getChild("TILES.0|0")).detachAllChildren();
        /**
         * Generate the tile and attach them with the right texture.
         * 1 geometry by texture.
         */
        setMesh((Node) ((Node) spatial).getChild("TILES.0|0"),
                meshParam.getMesh(onlyGround, chunkPosition));
    }

    protected void setMesh(Node parent, HashMap<String, Mesh> mesh) {
        if (!hexMaterial.getName().equals("arrayTextureMaterial")) {
            for (String value : mesh.keySet()) {
                Material mat = hexMaterial.clone();
                Geometry tile = new Geometry(value, mesh.get(value));
                Texture text;
                if (value.equals("EMPTY_TEXTURE_KEY")) {
                    text = assetManager.loadTexture(new TextureKey(HexSetting.TEXTURE_PATH + value + ".png", false));
                } else if (value.equals("NO_TILE") && (mode.equals(MapData.GhostMode.GHOST)
                        || mode.equals(MapData.GhostMode.GHOST_PROCEDURAL))) {
                    text = assetManager.loadTexture(new TextureKey(HexSetting.TEXTURE_PATH + "EMPTY_TEXTURE_KEY.png", false));
                    mat.setColor("Color", ColorRGBA.Blue);
                } else {
                    text = assetManager.loadTexture(new TextureKey(HexSetting.TEXTURE_PATH + value + ".png", false));
                }
                text.setWrap(Texture.WrapMode.Repeat);

                mat.setTexture("ColorMap", text);
//            mat.setTexture("DiffuseMap", text);
//            mat.getAdditionalRenderState().setWireframe(true);
//            tile.getMesh().setMode(Mesh.Mode.Points);
                tile.setMaterial(mat);
                tile.setShadowMode(RenderQueue.ShadowMode.Inherit);
                parent.attachChild(tile);
            }
        } else {
            Geometry tile = new Geometry("Geometry.ArrayTexture.TILES.0|0", mesh.get("mesh"));
            tile.setMaterial(hexMaterial);
            tile.setShadowMode(RenderQueue.ShadowMode.Inherit);
            parent.attachChild(tile);
        }
    }

    public Vector2Int getChunkPosition() {
        return chunkPosition;
    }

    /**
     * @return false if the chunk contain no tile (excluding ghost tile).
     * <p>If GhostMode.GHOST || GhostMode.GHOST_PROCEDURAL && only ghost
     * tile<br>
     * return true. </p>
     * <p>If GhostMode.NONE || GhostMode.PROCEDURAL && contain no tile<br>
     * return true. </p>
     * @deprecated See {
     * @see MapData#contain(Vector2Int)}
     */
    public boolean isEmpty() {
        if ((mode.equals(MapData.GhostMode.GHOST)
                || mode.equals(MapData.GhostMode.GHOST_PROCEDURAL))
                && ((Node) ((Node) spatial).getChild("TILES.0|0"))
                .getChildren().size() < 2
                && ((Node) ((Node) spatial).getChild("TILES.0|0"))
                .getChildren().get(0).getName().equals("NO_TILE")
                || (mode.equals(MapData.GhostMode.NONE)
                || mode.equals(MapData.GhostMode.PROCEDURAL))
                && ((Node) ((Node) spatial).getChild("TILES.0|0"))
                .getChildren().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}