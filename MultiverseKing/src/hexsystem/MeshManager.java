/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
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
    
    public Mesh generateMergedTile(Vector2Int size) {
        Mesh result = getMesh(size, 0);
        return result;
    }
    
    public Mesh generateOneTile() {
        Mesh result = getMesh(new Vector2Int(0, 0), 0);
        return result;
    }
    
    private Mesh getMesh(Vector2Int size, int height){
        Vector3f[] vertices = getVerticesPosition(size, height);
        Vector2f[] texCoord = getTexCoord(size);
        int[] index = getIndex(size);
        
        Mesh chunk = new Mesh();
        
        chunk = setAllBuffer(vertices, texCoord, index);
        
        return setCollisionBound(chunk);
    }
    
    private int[] getIndex(Vector2Int size){
        int[] index = new int[size.y*2*3];
        int j = 0;
        for(int i = 0; i < size.y*4; i+=4){
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
    
    private Vector3f[] getVerticesPosition(Vector2Int size, float height){
        Vector3f[] vertices = new Vector3f[4*size.y];
        int index = 0;
        for(int i = 0; i < size.y; i++){
            if((i&1) == 0){ //even
                vertices[index] = new Vector3f(-(hexWidth/2), height, hexSize+(i*(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height, hexSize+(i*(hexSize*1.5f)));
                vertices[index+2] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height, -hexSize+(i*(hexSize*1.5f)));
                vertices[index+3] = new Vector3f(-(hexWidth/2), height, -hexSize+(i*(hexSize*1.5f)));
            } else {
                vertices[index] = new Vector3f(0f, height+0.001f, hexSize+(i*(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((size.x * hexWidth), height+0.001f, hexSize+(i*(hexSize*1.5f)));
                vertices[index+2] = new Vector3f((size.x * hexWidth), height+0.001f, -hexSize+(i*(hexSize*1.5f)));
                vertices[index+3] = new Vector3f(0f, height+0.001f, -hexSize+(i*(hexSize*1.5f)));
            }
            index+= 4;
        }
        
        return vertices;
    }
    
    private Vector2f[] getTexCoord(Vector2Int size){
        Vector2f[] texCoord = new Vector2f[4*size.y];
        int index = 0;
        for(int i = 0; i< size.y; i++){
            texCoord[index] = new Vector2f(0f, 0f);
            texCoord[index+1] = new Vector2f(size.x, 0f);
            texCoord[index+2] = new Vector2f(size.x, 1f);
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
