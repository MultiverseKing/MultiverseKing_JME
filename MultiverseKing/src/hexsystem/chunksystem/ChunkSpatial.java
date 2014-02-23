/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.VertexBuffer;
import com.jme3.shader.VarType;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.TextureArray;
import hexsystem.HexSettings;
import java.util.ArrayList;
import java.util.List;
import jme3tools.optimize.GeometryBatchFactory;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
class ChunkSpatial {
    private final MeshManager meshManager;
    private final Material hexMat;
    private Node rootChunk;
    private Geometry[][] geo;

    
    ChunkSpatial(MeshManager meshManager, Material hexMat) {
        this.meshManager = meshManager;
        this.hexMat = hexMat;
    }
    
    void initialize(Node rootChunk, HexSettings hexSettings, int subChunkSize, ElementalAttribut eAttribut){
        this.rootChunk = rootChunk;
        int subChunkCount = hexSettings.getCHUNK_SIZE()/subChunkSize;
        geo = new Geometry[subChunkCount][subChunkCount];
//        Mesh tileMesh = meshManager.getFlatMesh(Vector2Int.ZERO, new Vector2Int(subChunkSize, subChunkSize));
        
        for (int x = 0; x < subChunkCount; x++) {
            for (int y = 0; y < subChunkCount; y++) {
                geo[x][y] = new Geometry(Integer.toString(x/**subChunkSize*/)+"|"+Integer.toString(y/**subChunkSize*/), meshManager.getFlatMesh(Vector2Int.ZERO, new Vector2Int(subChunkSize, subChunkSize)));
                geo[x][y].setLocalTranslation(getSubChunkLocalWorldPosition(x, y, hexSettings, subChunkSize));
                geo[x][y].setMaterial(hexMat);
                rootChunk.attachChild(geo[x][y]);
            }
        }
    }

    /**
     * @todo custom cull
     * @param enabled 
     */
    void setEnabled(boolean enabled) {
        CullHint culling = CullHint.Inherit;
        if(!enabled){
            culling = Spatial.CullHint.Always;
        }
        for (int x = 0; x < geo.length; x++) {
            for (int y = 0; y < geo[x].length; y++) {
                geo[x][y].setCullHint(culling);
            }
        }
    }
    
    //@todo update the spatial
    void updateSubChunk(Vector2Int subChunkLocalGridPos, ArrayList<Vector2Int[]> meshParameter, int subChunkSize) {
        ArrayList<Geometry> geo = new ArrayList<Geometry>();
        this.rootChunk.detachChild(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y]);
        for(int i = 0; i < meshParameter.size(); i++) {
            Geometry subChunk = new Geometry(Integer.toString(i), meshManager.getMesh(meshParameter.get(i)[0], meshParameter.get(i)[1], meshParameter.get(i)[2].y, meshParameter.get(i)[2].x));
//            subChunk.setMaterial(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getMaterial().clone());
//            subChunk.getMaterial().setTexture("ColorMap", mat[meshParameter.get(i)[2].x]);
            subChunk.setMaterial(hexMat);
//            subChunk.setLocalTranslation(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getLocalTranslation().x, this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getLocalTranslation().y+1, this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getLocalTranslation().z);
            geo.add(subChunk);
//            subChunk.setBatchHint(Spatial.BatchHint.Never);
        }
        
        
        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y] = GeometryBatchFactory.makeBatches(geo).get(0);
        
//        GeometryBatchFactory.optimize(container);
//        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y] = ;//.makeBatches(geo).get(0);//)mergeGeometries(geo, gateau);
//        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].setName(Integer.toString(subChunkLocalGridPos.x)+"|"+Integer.toString(subChunkLocalGridPos.y));// = new Geometry(Integer.toString(subChunkLocalGridPos.x)+"|"+Integer.toString(subChunkLocalGridPos.y), gateau);
//        this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].setLocalTranslation(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getLocalTranslation().x, this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getLocalTranslation().y+1, this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getLocalTranslation().z);
        
//        System.out.println("chunk : "+Integer.toString(subChunkLocalGridPos.x)+"|"+Integer.toString(subChunkLocalGridPos.y));
        System.out.println(this.geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getName());
        System.err.println("work.");
    }

    /**
     * Convert subChunk local grid position to world position.
     * @param subChunklocalGridPos 
     * @return world position of this subChunk.
     * @deprecated no use of it
     */
    Vector3f getSubChunkWorldPos(Vector2Int subChunkLocalGridPos){
        return geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getWorldTranslation();
    }

    /**
     * Convert SubChunk local grid position to local world position, relative to chunkNode.
     * @param subChunkLocaGridPosX
     * @param subChunkLocalGridPosY
     * @return 
     */
    private Vector3f getSubChunkLocalWorldPosition(int subChunkLocaGridPosX, int subChunkLocalGridPosY, HexSettings hexSettings, int subChunkSize) {
        float resultX = (subChunkLocaGridPosX*subChunkSize) * hexSettings.getHEX_WIDTH() + (hexSettings.getHEX_WIDTH()/2);
        float resultY = 0;
        float resultZ = (subChunkLocalGridPosY*subChunkSize) * (float)(hexSettings.getHEX_RADIUS()*1.5);
        
        return new Vector3f(resultX, resultY, resultZ);
    }

    HexCoordinate.Offset getSubChunkWorldGridPos(Vector2Int subChunkLocalGridPos) {
        return new HexCoordinate().new Offset((geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getName()));
    }
}
