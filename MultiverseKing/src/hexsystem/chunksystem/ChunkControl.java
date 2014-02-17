/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.MeshManager;
import java.util.ArrayList;
import kingofmultiverse.MultiverseMain;
import utility.Coordinate;
import utility.Coordinate.Offset;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class ChunkControl extends AbstractControl {
    private final MultiverseMain main;
    private final MapData mapData;
    private ChunkSpatial chunkSpatial;      //Contain the spatial for the chunk to work with
    private final int subChunkSize = 8;     //a subchunk contain 8*8 tiles
    
    public ChunkControl(MultiverseMain main, MapData mapData, MeshManager meshManager) {
        this.main = main; //Have to be change since all method in main isn't needed
        this.mapData = mapData;
        chunkSpatial = new ChunkSpatial(meshManager);
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
        if (spatial != null){
            // initialize
            chunkSpatial.initialize((Node)spatial, mapData.getHexSettings(), subChunkSize, getMaterial(mapData.getMapElement()));
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
     * @param tilePos 
     */
    public void updateChunk(Coordinate.Offset tilePos){
        Vector2Int subChunkLocalGridPos = getSubChunkLocalGridPos(tilePos);
//        Offset subChunkWorldGridPos = mapData.convertWorldToGridPosition(chunkSpatial.getSubChunkWorldPos(subChunkLocalGridPos));
        Offset subChunkWorldGridPos = chunkSpatial.getSubChunkWorldGridPos(subChunkLocalGridPos);
        
        HexTile[][] tile = new HexTile[subChunkSize][subChunkSize];
        ArrayList<Material> mat = new ArrayList<Material>();
        
        mat.add(getMaterial(mapData.getMapElement()));
        
        for(int x = 0; x < subChunkSize; x++){
            for(int y = 0; y < subChunkSize; y++){
                tile[x][y] = mapData.getTile(new Coordinate().new Offset(subChunkWorldGridPos.q+x, subChunkWorldGridPos.r+y));
                if(tile[x][y].getHexElement() != mapData.getMapElement()){
                    mat.add(getMaterial(tile[x][y].getHexElement()));
                }
            }
        }
        chunkSpatial.updateSubChunk(subChunkLocalGridPos, mat);
    }

    /**
     * @todo
     * @param hexElement
     * @param tilePos 
     * @deprecated use updateChunk
     */
    public void changeTileElement(ElementalAttribut hexElement, Coordinate.Offset tilePos) {
        Vector2Int subChunkLocalGridPos = getSubChunkLocalGridPos(tilePos);
//        Offset subChunkWorldGridPos = mapData.convertWorldToGridPosition(chunkSpatial.getSubChunkWorldPos(subChunkLocalGridPos));
        Offset subChunkWorldGridPos = chunkSpatial.getSubChunkWorldGridPos(subChunkLocalGridPos);
        HexTile[][] tile = new HexTile[subChunkSize][subChunkSize];
        ArrayList<Material> mat = new ArrayList<Material>();
        
        mat.add(getMaterial(mapData.getMapElement()));
        
        for(int x = 0; x < subChunkSize; x++){
            for(int y = 0; y < subChunkSize; y++){
                tile[x][y] = mapData.getTile(new Coordinate().new Offset(subChunkWorldGridPos.q+x, subChunkWorldGridPos.r+y));
                if(tile[x][y].getHexElement() != mapData.getMapElement()){
                    mat.add(getMaterial(tile[x][y].getHexElement()));
                }
            }
        }
        mapData.getTile(tilePos).getHexElement();
    }
    
    private Material getMaterial(ElementalAttribut eAttribut) {
        Material result = main.getAssetManager().loadMaterial("Materials/hexMat.j3m");
        Texture2D text = (Texture2D) main.getAssetManager().loadTexture("Textures/Test/"+ eAttribut.name() +"Center.png");
        text.setWrap(Texture.WrapMode.Repeat);
        result.setTexture("ColorMap", text);
//        mat.getAdditionalRenderState().setWireframe(true); //needed for debug on MeshManager
        return result;
    }
    
    /**
     * @return SubChunk local grid position, relative to Chunk grid position.
     */
    Vector2Int getSubChunkLocalGridPos(Coordinate.Offset tilePos){
        return new Vector2Int((int) (FastMath.abs(tilePos.q)/mapData.getHexSettings().getCHUNK_SIZE()/subChunkSize), 
                              (int) (FastMath.abs(tilePos.r)/mapData.getHexSettings().getCHUNK_SIZE()/subChunkSize));
    }
}
