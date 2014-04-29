/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import archives.MeshManagerV3;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import hexsystem.MapData;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * control the chunk geometry, all tiles geometry.
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {

    private static final int subChunkSize = 16;    //how much tile in a subchunk, must be power of two, /!\ chunk contain 32 tiles /!\
    private final MapData mapData;
    private final ChunkSpatial chunkSpatial;      //Contain the spatial for the chunk to work with

    public ChunkControl() {
        mapData = null;
        chunkSpatial = null;
    }

    public ChunkControl(MapData mapData, MeshManagerV3 meshManager, AssetManager assetManager, ElementalAttribut mapElement) {
        this.mapData = mapData;
        chunkSpatial = new ChunkSpatial(meshManager, assetManager);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if (spatial != null) {
            // initialize
            chunkSpatial.initialize((Node) spatial, mapData.getHexSettings(), subChunkSize, new MeshParameter(mapData));
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

    /**
     * Update all tile inside this chunk.
     */
    public void updateChunk() {
        for (byte x = 0; x < mapData.getHexSettings().getCHUNK_SIZE() / subChunkSize; x++) {
            for (byte y = 0; y < mapData.getHexSettings().getCHUNK_SIZE() / subChunkSize; y++) {
                updateTile(chunkSpatial.getSubChunkHexWorldPos(new Vector2Int(x, y), subChunkSize));
            }
        }
    }

    /**
     * update the speciate tile geometry.
     *
     * @param tilePos tile geometry to update.
     */
    public void updateTile(HexCoordinate tilePos) {
        Vector2Int subChunkLocalChunkPos = getSubChunkLocalChunkPos(tilePos);
        HexCoordinate subChunkHexWorldPos = chunkSpatial.getSubChunkHexWorldPos(subChunkLocalChunkPos, subChunkSize);
        MeshParameter meshParam = new MeshParameter(mapData);
        meshParam.initialize(subChunkSize, subChunkHexWorldPos, false);

        chunkSpatial.updateSubChunk(subChunkLocalChunkPos, meshParam);
    }

    /**
     * @return SubChunk local chunk position.
     */
    Vector2Int getSubChunkLocalChunkPos(HexCoordinate tile) {
        Vector2Int tilePos = tile.getAsOffset();
        Vector2Int result = new Vector2Int((int) ((FastMath.abs(tilePos.x) % mapData.getHexSettings().getCHUNK_SIZE()) / subChunkSize),
                (int) ((FastMath.abs(tilePos.y) % mapData.getHexSettings().getCHUNK_SIZE()) / subChunkSize));
        return result;
    }
}