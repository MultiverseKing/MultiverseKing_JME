/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import hexsystem.HexSettings;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MeshManager {

    private float hexSize;    //hex radius.
    private float hexWidth;   //Make life easier.
    
    public MeshManager(HexSettings settings) {
        this.hexSize = settings.getHEX_RADIUS();
        this.hexWidth = FastMath.sqrt(3) * hexSize;
    }
    
    Mesh generateMergedTile(Vector2Int startPos, Vector2Int endPos) {
        Mesh result = getMesh(startPos, endPos, 0);
        return result;
    }
    
    Mesh generateOneTile(Vector2Int startPos) {
        Mesh result = getMesh(startPos, new Vector2Int(0, 0), 0);
        return result;
    }
    
    Mesh getMesh(Vector2Int startPos, Vector2Int endPos, int height){
        Vector3f[] vertices = getVerticesPosition(startPos, endPos, height);
        Vector2f[] texCoord = getTexCoord(endPos);
        int[] index = getIndex(endPos.y-startPos.y);
        
        Mesh chunk = new Mesh();
        
        chunk = setAllBuffer(vertices, texCoord, index);
        
        return setCollisionBound(chunk);
    }
    
    private int[] getIndex(int size){
        int[] index = new int[size*2*3];
        int j = 0;
        for(int i = 0; i < size*4; i+=4){
            index[j] = i+2;
            index[j+1] = i+3;
            index[j+2] = i;
                    
            index[j+3] = i+1;
            index[j+4] = i+2;
            index[j+5] = i;
            
            j+= 6;
        }
        return index;
    }
    
    private Vector3f[] getVerticesPosition(Vector2Int startPos, Vector2Int endPos, float height){
        Vector3f[] vertices = new Vector3f[4*endPos.y];
        int index = 0;
        for(int i = startPos.y; i < endPos.y; i++){
            if((i&1) == 0){ //even
                vertices[index] = new Vector3f(-(hexWidth/2) + (startPos.x * hexWidth), height, hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((endPos.x * hexWidth)-(hexWidth/2) + (startPos.x * hexWidth), height, hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
                vertices[index+2] = new Vector3f((endPos.x * hexWidth)-(hexWidth/2) + (startPos.x * hexWidth), height, -hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
                vertices[index+3] = new Vector3f(-(hexWidth/2) + (startPos.x * hexWidth), height, -hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
            } else {
                vertices[index] = new Vector3f((startPos.x * hexWidth), height+0.001f, hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((endPos.x * hexWidth) + (startPos.x * hexWidth), height+0.001f, hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
                vertices[index+2] = new Vector3f((endPos.x * hexWidth) + (startPos.x * hexWidth), height+0.001f, -hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
                vertices[index+3] = new Vector3f((startPos.x * hexWidth), height+0.001f, -hexSize+(i*(hexSize*1.5f)) + (startPos.y *(hexSize*1.5f)));
            }
            index+= 4;
        }
        
        return vertices;
    }
    
    private Vector2f[] getTexCoord(Vector2Int endPos){
        Vector2f[] texCoord = new Vector2f[4*endPos.y];
        int index = 0;
        for(int i = 0; i< endPos.y; i++){
            texCoord[index] = new Vector2f(0f, 0f);
            texCoord[index+1] = new Vector2f(endPos.x, 0f);
            texCoord[index+2] = new Vector2f(endPos.x, 1f);
            texCoord[index+3] = new Vector2f(0f, 1f);
            index+=4;
        }        
        return texCoord;
    }
    
    private Mesh setCollisionBound(Mesh meshToUpdate) {
        meshToUpdate.createCollisionData();
        meshToUpdate.updateBound();
        return meshToUpdate;
    }
    
    private Mesh setAllBuffer(Vector3f[] vertices, Vector2f[] texCoord, int[] index){
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 2 , BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        return result;
    }
    
}
