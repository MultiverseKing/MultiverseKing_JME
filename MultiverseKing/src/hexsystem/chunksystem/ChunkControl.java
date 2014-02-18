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
import hexsystem.events.TileChangeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.HexCoordinate.Offset;
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
    public void updateChunk(HexCoordinate.Offset tilePos){
        Vector2Int subChunkLocalGridPos = getSubChunkLocalGridPos(tilePos);
//        Offset subChunkWorldGridPos = mapData.convertWorldToGridPosition(chunkSpatial.getSubChunkWorldPos(subChunkLocalGridPos));
        Offset subChunkWorldGridPos = chunkSpatial.getSubChunkWorldGridPos(subChunkLocalGridPos);
        
        HexTile[][] tile = new HexTile[subChunkSize][subChunkSize];
        ArrayList<Byte> allo = new ArrayList<Byte>();
//        ArrayList<ArrayList<Byte>> meshParameter = new ArrayList<ArrayList<Byte>()>();//[subChunkSize][subChunkSize];
        HashMap mat = new HashMap();
//        HashMap meshParam = new HashMap();
        ArrayList<Vector2Int[]> meshParam = new ArrayList<Vector2Int[]>();
        
        mat.put(mapData.getMapElement().ordinal(), getMaterial(mapData.getMapElement()));
        boolean initParam = false;
        for(int y = 0; y < subChunkSize; y++){
            for(int x = 0; x < subChunkSize; x++){
                tile[x][y] = mapData.getTile(new HexCoordinate().new Offset(subChunkWorldGridPos.q+x, subChunkWorldGridPos.r+y));
                if(tile[x][y].getHexElement() != mapData.getMapElement()){
                    mat.put(tile[x][y].getHexElement().ordinal(), getMaterial(tile[x][y].getHexElement()));
                }
                if(!initParam) {
                    meshParam.add(new Vector2Int[2]);
                    meshParam.get(0)[0] = new Vector2Int(x, y); //Start Position for the mesh
                    initParam = true;
                } else {
                    
                }
//                meshParameter[x][y].add(tile[x][y].getHexElement().ordinal());  // = mat to use for the tile
//                meshParameter[x][y].add(tile[x][y].getHeight());          // = get the height
            }
        }
        chunkSpatial.updateSubChunk(subChunkLocalGridPos, meshParameter, mat, subChunkSize);
    }
    
    /**
     * @todo
     * @param hexElement
     * @param tilePos 
     * @deprecated use updateChunk
     */
    public void changeTileElement(ElementalAttribut hexElement, HexCoordinate.Offset tilePos) {
        Vector2Int subChunkLocalGridPos = getSubChunkLocalGridPos(tilePos);
//        Offset subChunkWorldGridPos = mapData.convertWorldToGridPosition(chunkSpatial.getSubChunkWorldPos(subChunkLocalGridPos));
        Offset subChunkWorldGridPos = chunkSpatial.getSubChunkWorldGridPos(subChunkLocalGridPos);
        HexTile[][] tile = new HexTile[subChunkSize][subChunkSize];
        ArrayList<Material> mat = new ArrayList<Material>();
        
        mat.add(getMaterial(mapData.getMapElement()));
        
        for(int x = 0; x < subChunkSize; x++){
            for(int y = 0; y < subChunkSize; y++){
                tile[x][y] = mapData.getTile(new HexCoordinate().new Offset(subChunkWorldGridPos.q+x, subChunkWorldGridPos.r+y));
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
    Vector2Int getSubChunkLocalGridPos(HexCoordinate.Offset tilePos){
        return new Vector2Int((int) (FastMath.abs(tilePos.q)/mapData.getHexSettings().getCHUNK_SIZE()/subChunkSize), 
                              (int) (FastMath.abs(tilePos.r)/mapData.getHexSettings().getCHUNK_SIZE()/subChunkSize));
    }
}
