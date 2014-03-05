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
import utility.HexCoordinate.Offset;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {
    private final MapData mapData;
    private ChunkSpatial chunkSpatial;      //Contain the spatial for the chunk to work with
    private final int subChunkSize = 16;    //how much tile in a subchunk, must be power of two, /!\ chunk contain 32 tiles /!\
    
    public ChunkControl(MapData mapData, MeshManager meshManager, Material hexMat, ElementalAttribut mapElement) {
        this.mapData = mapData;
        chunkSpatial = new ChunkSpatial(meshManager, hexMat, mapElement);
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if (spatial != null){
            // initialize
            chunkSpatial.initialize((Node)spatial, mapData.getHexSettings(), subChunkSize);
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
     * @todo updata height
     * @todo wrapper for meshparameter
     * @param tilePos 
     */
    public void updateChunk(HexCoordinate.Offset tilePos){
        Vector2Int subChunkLocalGridPos = getSubChunkLocalGridPos(tilePos);
        Offset subChunkWorldGridPos = getSubChunkWorldGridPos(subChunkLocalGridPos);
//        ArrayList<MeshParameter> meshParam = new ArrayList<MeshParameter>();
        MeshParameter meshParam = new MeshParameter(mapData, subChunkWorldGridPos);
        
        int i = 0;
        boolean initParam = false;
        for(int y = 0; y < subChunkSize; y++){
            if(initParam){
                initParam = false;
                i++;
            }
            for(int x = 0; x < subChunkSize-1; x++){
                HexTile tile = mapData.getTile(new HexCoordinate().new Offset(subChunkWorldGridPos.q+x, subChunkWorldGridPos.r+y));
                HexTile nearTile = mapData.getTile(new HexCoordinate().new Offset(subChunkWorldGridPos.q+x+1, subChunkWorldGridPos.r+y));
                if(!initParam) {
                    meshParam.add(new Vector2Int(x, y), new Vector2Int(1,1), (byte)tile.getHexElement().ordinal(), (byte)tile.getHeight());
                    initParam = true;
                } 
                if (nearTile.getHexElement() == tile.getHexElement() && 
                           nearTile.getHeight() == tile.getHeight() ) {
                    meshParam.extendsSizeX(i);
                } else {
                    i++;
                    meshParam.add(new Vector2Int(x+1, y), new Vector2Int(1,1), (byte)nearTile.getHexElement().ordinal(), (byte)nearTile.getHeight());
                }
            }
        }
        chunkSpatial.updateSubChunk(subChunkLocalGridPos, meshParam);
    }
    
    /**
     * @return SubChunk local grid position, relative to Chunk grid position.
     */
    Vector2Int getSubChunkLocalGridPos(HexCoordinate.Offset tilePos){
        Vector2Int result = new Vector2Int((int) ((FastMath.abs(tilePos.q)%mapData.getHexSettings().getCHUNK_SIZE())/subChunkSize), 
                              (int) ((FastMath.abs(tilePos.r)%mapData.getHexSettings().getCHUNK_SIZE())/subChunkSize));
        return result;
    }
    
    Offset getSubChunkWorldGridPos(Vector2Int subChunkLocalGridPos) {
        return new HexCoordinate().new Offset(subChunkLocalGridPos.x*subChunkSize, subChunkLocalGridPos.y*subChunkSize);
    }
}