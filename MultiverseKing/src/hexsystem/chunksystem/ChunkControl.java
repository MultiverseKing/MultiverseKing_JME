/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import hexsystem.HexTile;
import hexsystem.MapData;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 * control the chunk geometry, all tiles geometry.
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

    public ChunkControl(MapData mapData, MeshManager meshManager, Material hexMat, ElementalAttribut mapElement) {
        this.mapData = mapData;
        chunkSpatial = new ChunkSpatial(meshManager, hexMat);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if (spatial != null) {
            // initialize
            chunkSpatial.initialize((Node) spatial, mapData.getHexSettings(), subChunkSize, this);
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
                updateTile(getSubChunkWorldGridPos(new Vector2Int(x, y)));
            }
        }
    }

    /**
     * update the speciate tile geometry.
     * @param tilePos tile geometry to update.
     */
    public void updateTile(HexCoordinate tilePos) {
        Vector2Int subChunkLocalGridPos = getSubChunkLocalGridPos(tilePos);
        HexCoordinate subChunkWorldGridPos = getSubChunkWorldGridPos(subChunkLocalGridPos);
        Vector2Int subChunkWorldGridPosOffset = subChunkWorldGridPos.getAsOffset();
        MeshParameter meshParam = new MeshParameter(mapData, subChunkWorldGridPos);

        int i = 0;
        boolean initParam = false;
        for (int y = 0; y < subChunkSize; y++) {
            if (initParam) {
                initParam = false;
                i++;
            }
            for (int x = 0; x < subChunkSize - 1 ; x++) {
                HexTile tile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, subChunkWorldGridPosOffset.x + x, subChunkWorldGridPosOffset.y + y));
                HexTile nearTile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, subChunkWorldGridPosOffset.x + x +1, subChunkWorldGridPosOffset.y + y));
                if (!initParam) {
                    meshParam.add(new Vector2Int(x, y), new Vector2Int(1, 1), (byte) tile.getElement().ordinal(), (byte) tile.getHeight());
                    initParam = true;
                }
                if (nearTile.getElement() == tile.getElement()
                        && nearTile.getHeight() == tile.getHeight()) {
                    meshParam.extendsSizeX(i);
                } else {
                    i++;
                    meshParam.add(new Vector2Int(x + 1, y), new Vector2Int(1, 1), (byte) nearTile.getElement().ordinal(), (byte) nearTile.getHeight());
                }
            }
        }
        chunkSpatial.updateSubChunk(subChunkLocalGridPos, meshParam);
    }

    /**
     * @return SubChunk local chunk position.
     */
    Vector2Int getSubChunkLocalGridPos(HexCoordinate tile) {
        Vector2Int tilePos = tile.getAsOffset();
        Vector2Int result = new Vector2Int((int) ((FastMath.abs(tilePos.x) % mapData.getHexSettings().getCHUNK_SIZE()) / subChunkSize),
                (int) ((FastMath.abs(tilePos.y) % mapData.getHexSettings().getCHUNK_SIZE()) / subChunkSize));
        return result;
    }

    /**
     * @return Subchunk position in hexMap.
     */
    HexCoordinate getSubChunkWorldGridPos(Vector2Int subChunkLocalGridPos) {
        return new HexCoordinate(HexCoordinate.OFFSET, subChunkLocalGridPos.x * subChunkSize, subChunkLocalGridPos.y * subChunkSize);
    }
    
    
}