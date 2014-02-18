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
import java.util.ArrayList;
import java.util.HashMap;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
class ChunkSpatial {
    private final MeshManager meshManager;
    private Geometry[][] geo;
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
        int subChunkCount = hexSettings.getCHUNK_SIZE()/subChunkSize;
        geo = new Geometry[subChunkCount][subChunkCount];
        subChunkTileElementCount = new byte[subChunkCount][subChunkCount];
        Mesh tileMesh = meshManager.generateMergedTile(Vector2Int.ZERO, new Vector2Int(subChunkSize, subChunkSize));
        
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
    void updateSubChunk(Vector2Int subChunkLocalGridPos, ArrayList[][] meshParameter, HashMap mat, int subChunkSize) {
        ArrayList<Geometry> geo = new ArrayList<Geometry>();
        Vector2Int startPos = Vector2Int.ZERO;
        Vector2Int endPos = Vector2Int.ZERO;
        byte heigth = 0;
        boolean initStartPos = false;
        int x = 0;
        int y = 0;
        do{
            for(y = 0; y < subChunkSize; y++) {
                for(x = 0; x < subChunkSize; x++) {
                    if(!initStartPos && meshParameter[x][y].get(0) == meshParameter[x+1][y].get(0) && 
                                        meshParameter[x][y].get(1) == meshParameter[x+1][y].get(1) ) {
                        startPos = new Vector2Int(x, y);
                        initStartPos = true;
                    } else {
                        geo.add(new Geometry((x+"|"y), meshManager.getMesh(startPos, endPos, meshParameter[x][y].get(1)));
                    }
                }
            }
            y++;
        } while(true);
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
