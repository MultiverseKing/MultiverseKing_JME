/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import hexsystem.HexSettings;
import hexsystem.MeshManager;
import java.util.ArrayList;
import utility.Coordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
class ChunkSpatial {
    private final MeshManager meshManager;
    private Geometry[][] geo;
    private Node rootNode;
    private byte[][] subChunkTileElementCount; //keep track on how many tile got different element texture on specifiate subChunk

    /**
     * @deprecated no longuer needed ?
     */
    byte getSubChunkTileElementCount(Vector2Int subChunkPos) {
        return subChunkTileElementCount[subChunkPos.x][subChunkPos.y];
    }
    
    ChunkSpatial(MeshManager meshManager) {
        this.meshManager = meshManager;
    }
    
    void initialize(Node rootNode, HexSettings hexSettings, int subChunkSize, Material mat){
        this.rootNode = rootNode;
        int subChunkCount = hexSettings.getCHUNK_SIZE()/subChunkSize;
        geo = new Geometry[subChunkCount][subChunkCount];
        subChunkTileElementCount = new byte[subChunkCount][subChunkCount];
        Mesh tileMesh = this.meshManager.generateMergedTile(new Vector2Int(subChunkSize, subChunkSize));
        
        for (int x = 0; x < subChunkCount; x++) {
            for (int y = 0; y < subChunkCount; y++) {
                geo[x][y] = new Geometry(x*subChunkSize+"|"+y*subChunkSize, tileMesh);
                geo[x][y].setLocalTranslation(getSubChunkLocalWorldPosition(x, y, hexSettings, subChunkSize));
                geo[x][y].setMaterial(mat);
                rootNode.attachChild(geo[x][y]);
                subChunkTileElementCount[x][y] = 1;
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
    void updateSubChunk(Vector2Int subChunkLocalGridPos, ArrayList<Material> mat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    Coordinate.Offset getSubChunkWorldGridPos(Vector2Int subChunkLocalGridPos) {
        return new Coordinate().new Offset((geo[subChunkLocalGridPos.x][subChunkLocalGridPos.y].getName()));
    }
    
}
