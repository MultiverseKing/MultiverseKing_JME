/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package archives;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import java.util.ArrayList;
import jme3tools.optimize.GeometryBatchFactory;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MeshManagerV2 {
    
    final float sqrt = FastMath.sqrt(3);        //Make life easier.
    final float hexSize = 1f;                   //hex radius.
    final float hexWidth = sqrt * hexSize;      //Make life easier.
    final float floorHeight = 0.5f;             //ho much the result should be upped
    private Hashtable generatedMesh = new Hashtable();

    public float getHexSize() {return hexSize;}
    public float getHexWidth() {return hexWidth;}
    
    /**
     * Generate chunk mesh of the size of the chunk with no height.
     * @param size Number of tiles to link to the chunk.
     * @return A newly generated chunk.
     */
    Mesh generateChunk(Vector2Int size) {
        Mesh result = getMesh(size, 0);
        return result;
    }
    
    private Mesh getMesh(Vector2Int size, int height){
        Vector3f[] triVertices = getTriVerticesPosition(size, height);
        Vector3f[] quadVertices = getQuadVerticesPosition(size, height);
        
        Vector2f[] triTexCoord = getTriTexCoord(size);
        Vector2f[] quadTexCoord = getQuadTexCoord(size);
        
        int[] triIndex = getTriIndex(size);
        int[] quadIndex = getQuadIndex(size);
        
        Mesh[] chunk = {new Mesh(), new Mesh()};
        
        chunk[0] = setAllBuffer(triVertices, triTexCoord, triIndex);
        chunk[1] = setAllBuffer(quadVertices, quadTexCoord, quadIndex);
        
        ArrayList<Geometry> geoChunk = new ArrayList<Geometry>();
        geoChunk.add(new Geometry("chunk", chunk[0]));
        geoChunk.add(new Geometry("chunk", chunk[1]));
        
        Mesh finalChunk = new Mesh();
        GeometryBatchFactory.mergeGeometries(geoChunk, finalChunk);
        
        return setCollisionBound(finalChunk);
//        return setCollisionBound(chunk[0]);
    }
    
    private Vector3f[] getTriVerticesPosition(Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[size.x*4+2];
        int j = 0;
        float currentHexPos;
        for(int i = 0; i < size.x*4; i+=4){
            currentHexPos = j*hexWidth;
            vertices[i] = new Vector3f(currentHexPos, height*floorHeight, -hexSize);
            vertices[i+1] = new Vector3f(currentHexPos -(hexWidth/2), height*floorHeight, -(hexSize/2));
            if((size.y&1) == 0){
                currentHexPos = (j*hexWidth)+(hexWidth/2);
                vertices[i+2] = new Vector3f(currentHexPos +(-hexWidth/2), height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
                vertices[i+3] = new Vector3f(currentHexPos, height*floorHeight, (hexSize*size.y) +((size.y-1)*(hexSize/2)));
            } else {
                vertices[i+2] = new Vector3f(currentHexPos -(hexWidth/2), height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
                vertices[i+3] = new Vector3f(currentHexPos, height*floorHeight, (hexSize*size.y) +((size.y-1)*(hexSize/2)));
            }
            j++;
        }
        currentHexPos = (j*hexWidth);
        vertices[size.x*4] = new Vector3f(currentHexPos +(-hexWidth/2), height*floorHeight, -hexSize/2);
        if((size.y&1) == 0){
            vertices[size.x*4+1] = new Vector3f(currentHexPos, height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
        } else {
            vertices[size.x*4+1] = new Vector3f(currentHexPos+(-hexWidth/2), height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
        }
        
        return vertices;
    }
    
    private Vector3f[] getQuadVerticesPosition(Vector2Int size, int height){
        Vector3f[] vertices = new Vector3f[(4*size.y)+((size.y-1)*2)];
        int index = 0;
        vertices[index] = new Vector3f(-(hexWidth/2), height*floorHeight, -(hexSize/2));
        vertices[index+1] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height*floorHeight, -(hexSize/2));
        vertices[index+2] = new Vector3f(-(hexWidth/2), height*floorHeight, (hexSize/2));
        vertices[index+3] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height*floorHeight, (hexSize/2));
        index += 4;
        for(int i = 1; i < size.y; i++){
            float Ypos = (hexSize*i)+((hexSize/2)*i);
            if ((i&1) == 0){ //even
                vertices[index] = new Vector3f(0, height*floorHeight, (hexSize/2)+(hexSize*(i-1))+((hexSize/2)*(i-1)));
                vertices[index+1] = new Vector3f((size.x * hexWidth), height*floorHeight, (hexSize/2)+(hexSize*(i-1))+((hexSize/2)*(i-1)));
                vertices[index+2] = new Vector3f(-(hexWidth/2), height*floorHeight, -(hexSize/2)+Ypos);
                vertices[index+3] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height*floorHeight, -(hexSize/2)+Ypos);
                vertices[index+4] = new Vector3f(-(hexWidth/2), height*floorHeight, (hexSize/2)+Ypos);
                vertices[index+5] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height*floorHeight, (hexSize/2)+Ypos);
//                System.err.println("even" + (index+2));
            } else {
                vertices[index] = new Vector3f(-(hexWidth/2), height*floorHeight, (hexSize/2)+(hexSize*(i-1))+((hexSize/2)*(i-1)));
                vertices[index+1] = new Vector3f((size.x * hexWidth)-(hexWidth/2), height*floorHeight, (hexSize/2)+(hexSize*(i-1))+((hexSize/2)*(i-1)));
                vertices[index+2] = new Vector3f(0, height*floorHeight, -(hexSize/2)+Ypos);
                vertices[index+3] = new Vector3f((size.x * hexWidth), height*floorHeight, -(hexSize/2)+Ypos);
                vertices[index+4] = new Vector3f(0, height*floorHeight, (hexSize/2)+Ypos);
                vertices[index+5] = new Vector3f((size.x * hexWidth), height*floorHeight, (hexSize/2)+Ypos);
            }
            index+=6;
        }
        return vertices;
    }
    
    private int[] getQuadIndex(Vector2Int size){
        int[] index = new int[(size.y*2*3)+((size.y-1)*2*3)];

        index[0] = 2;
        index[1] = 1;
        index[2] = 0;

        index[3] = 2;
        index[4] = 3;
        index[5] = 1;
            
        int i = 6;
        for(int j = 0; j < (size.y)*6-6; j+=6){
            
            index[i] = j+4;
            index[i+1] = j+6;
            index[i+2] = j+5;
            
            index[i+3] = j+5;
            index[i+4] = j+6;
            index[i+5] = j+7;
            
            index[i+6] = j+6;
            index[i+7] = j+8;
            index[i+8] = j+7;
            
            index[i+9] = j+7;
            index[i+10] = j+8;
            index[i+11] = j+9;

            i+=12;
        }
//        System.err.println((size.y)*4);
        return index;
    }
        
    private Vector2f[] getQuadTexCoord(Vector2Int size){
        Vector2f[] texCoord = new Vector2f[(4*size.y)+((size.y-1)*2)];
        
        texCoord[0] = new Vector2f(0f, 0.25f);
        texCoord[1] = new Vector2f(size.x, 0.25f);
        texCoord[2] = new Vector2f(0f, 0.75f);
        texCoord[3] = new Vector2f(size.x, 0.75f);
        
        int index = 4;    
        for(int i = 1; i < size.y; i++){
        
        if((i&1)==0){
            texCoord[index] = new Vector2f(0.5f, 0.001f);
            texCoord[index+1] = new Vector2f(size.x+0.5f, 0.001f);
            texCoord[index+2] = new Vector2f(0f, 0.25f);
            texCoord[index+3] = new Vector2f(size.x, 0.25f);
            texCoord[index+4] = new Vector2f(0f, 0.75f);
            texCoord[index+5] = new Vector2f(size.x, 0.75f);
        } else {
            texCoord[index] = new Vector2f(-0.5f, 0.001f);
            texCoord[index+1] = new Vector2f(-(size.x+0.5f), 0.001f);
            texCoord[index+2] = new Vector2f(-1f, 0.25f);
            texCoord[index+3] = new Vector2f(-(size.x+1f), 0.25f);
            texCoord[index+4] = new Vector2f(-1f, 0.75f);
            texCoord[index+5] = new Vector2f(-(size.x+1f), 0.75f);
        }
        index+= 6;
        }
        
        return texCoord;
    }
    
    private Vector2f[] getTriTexCoord(Vector2Int size){
        Vector2f[] texCoord = new Vector2f[size.x*4+2];  
        int j = 0;
        for(int k = 0; k < 2; k++){
            j = 0;
            for(int i = 0; i < size.x*4; i+=4){
                texCoord[i] = new Vector2f(j+0.5f, 0f);
                texCoord[i+1] = new Vector2f(j, 0.25f);
                texCoord[i+2] = new Vector2f(j, 0.75f);//Y+
                texCoord[i+3] = new Vector2f(j+0.5f, 1f);
                j++;
            }
        }
        texCoord[size.x*4] = new Vector2f(j, 0.25f);
        texCoord[size.x*4+1] = new Vector2f(j, 0.75f);
        return texCoord;
    }
    
    private int[] getTriIndex(Vector2Int size){
        int[] index = new int[(size.x*2)*3];
        
        int j = 0;
        int i = 0;
        for(i = 0; i < (size.x*2)*3-6; i+= 6){
            index[i] = j;
            index[i+1] = j+1;
            index[i+2] = j+5;
            
            index[i+3] = j+2;
            index[i+4] = j+3;
            index[i+5] = j+6;
            j+= 4;
        }
        
        index[i] = j;
        index[i+1] = j+1;
        index[i+2] = size.x*4;

        index[i+3] = j+2;
        index[i+4] = j+3;
        index[i+5] = size.x*4+1;
        
//      [XY] == World position
//      ++++++++++++++++++++++++++++++++++++++ = Y-
//      +++++[03]++++[07]++++[11]++++[15]+++++
//      +[02]++++[06]++++[10]++++[14]++++[16]+
//      +++++[XY]++++[XY]++++[XY]++++[XY]+++++
//      +[01]++++[05]++++[09]++++[13]++++[17]+
//      +++++[00]++++[04]++++[08]++++[12]+++++
//      ++++++++++++++++++++++++++++++++++++++ = Y+
         
        return index;
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
