/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import hexsystem.HexSettings;
import hexsystem.MapData;
import java.lang.reflect.Array;
import java.util.ArrayList;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MeshManager {

    private final float hexSize;    //hex radius.
    private final float hexWidth;   //Make life easier.
    private final float floorHeight;             //ho much the result should be upped
    
    public MeshManager(HexSettings settings) {
        this.hexSize = settings.getHEX_RADIUS();
        this.hexWidth = FastMath.sqrt(3) * hexSize;
        this.floorHeight = settings.getFloorHeight();
    }
    /**
     * Create a flat grid at specifiate position, specifiate size and specifiate attribut.
     * @param position grid position to start the mesh.
     * @param size number of tile to put in mesh.
     * @param eAttribut texture to use.
     * @return newly created mesh.
     */
    Mesh getMesh(Vector2Int position, Vector2Int size, int eAttribut) {
        return getMesh(position, size, 0, eAttribut);
    }
    /**
     * Create a flat mesh of 1 tile at specifiate position.
     * @param position grid position where the tile need to be.
     * @return newly created tile mesh.
     */
    Mesh getMesh(Vector2Int position) {
        return getMesh(position, new Vector2Int(0, 0), 0, 0);
    }
    /**
     * Create a grid at specifiate position, size, height and texture coordinate.
     * @param position where to start the mesh.
     * @param size number of tile inside the mesh.
     * @param height height of the mesh.
     * @param element texture to use.
     * @return newly generated tile grid mesh.
     */
    Mesh getMesh(Vector2Int position, Vector2Int size, int height, int element){
        Vector3f[] vertices = getVerticesPosition(position, size, height);
        Vector3f[] texCoord = getTexCoord(size, element);
        int[] index = getIndex(size.y, 0);
        
        return setCollisionBound(setAllBuffer(vertices, texCoord, index));
    }
    
    Mesh getMergedMesh(ArrayList<Vector2Int[]> meshParameter) {
        ArrayList<Vector3f[]> vertices = new ArrayList<Vector3f[]>();
        ArrayList<Vector3f[]> texCoord = new ArrayList<Vector3f[]>();
        ArrayList<int[]> index = new ArrayList<int[]>();
                
        int mergedVerticeCount = 0;
        int mergedtextCoordCount = 0;
        int mergedIndexCount = 0;
                
        for(int i = 0; i < meshParameter.size(); i++) {
            Vector3f[] vert = getVerticesPosition(meshParameter.get(i)[0], meshParameter.get(i)[1], meshParameter.get(i)[2].y);
            Vector3f[] tex = getTexCoord(meshParameter.get(i)[1], meshParameter.get(i)[2].x);
            int[] indice = getIndex(meshParameter.get(i)[1].y, mergedVerticeCount);
            
            mergedtextCoordCount += tex.length;
            mergedVerticeCount += vert.length;
            mergedIndexCount += indice.length;
            
            if(meshParameter.get(i)[2].y != 0){
                Vector3f[] sideVert = getSideVerticesPosition(meshParameter.get(i)[0], meshParameter.get(i)[1], meshParameter.get(i)[2].y);
                Vector3f[] vertCombi = new Vector3f[sideVert.length+vert.length];
                System.arraycopy(vert, 0, vertCombi, 0, vert.length);
                System.arraycopy(sideVert, 0, vertCombi, vert.length, sideVert.length);
                mergedVerticeCount += sideVert.length;
                vertices.add(vertCombi);
                
//                Vector3f[] sideTex = getSideVerticestexCoord(meshParameter.get(i)[0], meshParameter.get(i)[1], meshParameter.get(i)[2].y);
//                Vector3f[] texCombi = new Vector3f[sideTex.length+tex.length];
//                System.arraycopy(tex, 0, texCombi, 0, tex.length);
//                System.arraycopy(sideTex, 0, texCombi, tex.length, sideTex.length);
//                mergedtextCoordCount += sideTex.length;
//                texCoord.add(texCombi);
                
                int[] sideIndice = getSideIndex(meshParameter.get(i)[1], mergedVerticeCount);
                int[] indiceCombi = new int[sideIndice.length+indice.length];
                System.arraycopy(indice, 0, indiceCombi, 0, indice.length);
                System.arraycopy(sideIndice, 0, indiceCombi, indice.length, sideIndice.length);
                mergedIndexCount += sideIndice.length;
                index.add(indiceCombi);
            } else {
                vertices.add(vert);
                texCoord.add(tex);
                index.add(indice);
            }
//            System.out.println(meshParameter.get(i)[0] +" "+ meshParameter.get(i)[1]+ " " + meshParameter.get(i)[2].x); //debug
            
        }
        Vector3f[] mergedVertices = new Vector3f[mergedVerticeCount];
        Vector3f[] mergedtextCoord = new Vector3f[mergedtextCoordCount];
        int[] mergedIndex = new int[mergedIndexCount];
        
        mergedVerticeCount = 0;
        mergedtextCoordCount = 0;
        mergedIndexCount = 0;
        for(int i = 0; i < meshParameter.size(); i++){
            System.arraycopy(vertices.get(i), 0, mergedVertices, mergedVerticeCount, vertices.get(i).length);
//            System.arraycopy(texCoord.get(i), 0, mergedtextCoord, mergedtextCoordCount, texCoord.get(i).length);
            System.arraycopy(index.get(i), 0, mergedIndex, mergedIndexCount, index.get(i).length);
            
            mergedVerticeCount += vertices.get(i).length;
//            mergedtextCoordCount += texCoord.get(i).length;
            mergedIndexCount += index.get(i).length;
        }
        return setCollisionBound(setAllBuffer(mergedVertices, mergedtextCoord, mergedIndex));
    }
    
    private int[] getIndex(int size, int offset){
        int[] index = new int[size*2*3];
        int j = 0;
        for(int i = 0; i < size*4; i+=4){
            index[j] = i+2+offset;
            index[j+1] = i+3+offset;
            index[j+2] = i+offset;
                    
            index[j+3] = i+1+offset;
            index[j+4] = i+2+offset;
            index[j+5] = i+offset;
            
            j+= 6;
        }
        return index;
    }
    
    private Vector3f[] getVerticesPosition(Vector2Int position, Vector2Int size, float height){
        Vector3f[] vertices = new Vector3f[4*size.y];
        int index = 0;
        for(int i = 0; i < size.y; i++){
            if((position.y+i&1) == 0){ //even
                vertices[index] = new Vector3f(-(hexWidth/2) + (position.x * hexWidth), height*floorHeight, hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((size.x * hexWidth)-(hexWidth/2)+(position.x * hexWidth), height*floorHeight, hexSize+((position.y+i)*(hexSize*1.5f)) );
                vertices[index+2] = new Vector3f((size.x * hexWidth)-(hexWidth/2)+(position.x * hexWidth), height*floorHeight, -hexSize+((position.y+i)*(hexSize*1.5f)) );
                vertices[index+3] = new Vector3f(-(hexWidth/2) + (position.x * hexWidth), height*floorHeight, -hexSize+((position.y+i)*(hexSize*1.5f)) );
            } else {
                vertices[index] = new Vector3f((position.x * hexWidth), height*floorHeight+0.001f, hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+1] = new Vector3f((size.x * hexWidth)+(position.x * hexWidth), height*floorHeight+0.001f, hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+2] = new Vector3f((size.x * hexWidth)+(position.x * hexWidth), height*floorHeight+0.001f, -hexSize+((position.y+i)*(hexSize*1.5f)));
                vertices[index+3] = new Vector3f((position.x * hexWidth), height*floorHeight+0.001f, -hexSize+((position.y+i)*(hexSize*1.5f)));
            }
            index+= 4;
        }
        
        return vertices;
    }
    
    private Vector3f[] getSideVerticesPosition(Vector2Int position, Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[(size.x*4+2)*2];
        int j = position.x;
        float currentHexPos;
        for(int i = 0; i < size.x*4*2; i+=8){
            currentHexPos = j*hexWidth;
            vertices[i] = new Vector3f(currentHexPos, height*floorHeight, -hexSize);
            vertices[i+1] = new Vector3f(currentHexPos, 0, -hexSize);
            vertices[i+2] = new Vector3f(currentHexPos -(hexWidth/2), height*floorHeight, -(hexSize/2));
            vertices[i+3] = new Vector3f(currentHexPos -(hexWidth/2), 0, -(hexSize/2));
            if((size.y&1) == 0){
                currentHexPos = (j*hexWidth)+(hexWidth/2);
                vertices[i+4] = new Vector3f(currentHexPos +(-hexWidth/2), height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
                vertices[i+5] = new Vector3f(currentHexPos +(-hexWidth/2), 0, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
                vertices[i+6] = new Vector3f(currentHexPos, height*floorHeight, (hexSize*size.y) +((size.y-1)*(hexSize/2)));
                vertices[i+7] = new Vector3f(currentHexPos, 0, (hexSize*size.y) +((size.y-1)*(hexSize/2)));
            } else {
                vertices[i+4] = new Vector3f(currentHexPos -(hexWidth/2), height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
                vertices[i+5] = new Vector3f(currentHexPos -(hexWidth/2), 0, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
                vertices[i+6] = new Vector3f(currentHexPos, height*floorHeight, (hexSize*size.y) +((size.y-1)*(hexSize/2)));
                vertices[i+7] = new Vector3f(currentHexPos, 0, (hexSize*size.y) +((size.y-1)*(hexSize/2)));
            }
            j++;
        }
        currentHexPos = (j*hexWidth);
        vertices[size.x*4*2] = new Vector3f(currentHexPos +(-hexWidth/2), height*floorHeight, -hexSize/2);
        vertices[size.x*4*2+1] = new Vector3f(currentHexPos +(-hexWidth/2), 0, -hexSize/2);
        if((size.y&1) == 0){
            vertices[size.x*4*2+2] = new Vector3f(currentHexPos, height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
            vertices[size.x*4*2+3] = new Vector3f(currentHexPos, 0, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
        } else {
            vertices[size.x*4*2+2] = new Vector3f(currentHexPos+(-hexWidth/2), height*floorHeight, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
            vertices[size.x*4*2+3] = new Vector3f(currentHexPos+(-hexWidth/2), 0, ((hexSize*size.y)-hexSize/2) +((size.y-1)*(hexSize/2)));
        }
        return vertices;
    }
    
    private int[] getSideIndex(Vector2Int size, int offset){
        int[] index = new int[(size.x*4+2)*3];
        
        index[0] = offset+0;
        index[1] = offset+2;
        index[2] = offset+1;

        index[3] = offset+1;
        index[4] = offset+3;
        index[5] = offset+2;
        
        int j = offset+2;
        for(int i = 0; i < size.x; i++){
            index[5+i] = j;
            index[5+i+1] = j+2;
            index[5+i+2] = j+1;
            
            index[5+i+3] = j+1;
            index[5+i+4] = j+3;
            index[5+i+5] = j+2;
            
            index[5+i+6] = j-2;
            index[5+i+7] = j+4;
            index[5+i+8] = j-1;
            
            index[5+i+9] = j-1;
            index[5+i+10] = j+5;
            index[5+i+11] = j+4;
            
            index[5+i] = j+4;
            index[5+i+1] = j+6;
            index[5+i+2] = j+5;
            
            index[5+i+3] = j+5;
            index[5+i+4] = j+7;
            index[5+i+5] = j+6;
            
            index[5+i+6] = j+2;
            index[5+i+7] = j+6;
            index[5+i+8] = j+3;
            
            index[5+i+9] = j+3;
            index[5+i+10] = j+7;
            index[5+i+11] = j+6;
            j+= 8;
        }
        
        index[index.length-6] = offset+size.x*4*2;
        index[index.length-5] = offset+size.x*4*2+2;
        index[index.length-4] = offset+size.x*4*2+1;

        index[index.length-3] = offset+size.x*4*2+1;
        index[index.length-2] = offset+size.x*4*2+3;
        index[index.length-1] = offset+size.x*4*2+2;
        
        return index;
    }
    
    private Vector3f[] getTexCoord(Vector2Int size, int element){
        Vector3f[] texCoord = new Vector3f[4*size.y];
        int index = 0;
        for(int i = 0; i< size.y; i++){
            texCoord[index] = new Vector3f(0f, 0f, element);
            texCoord[index+1] = new Vector3f(size.x, 0f, element);
            texCoord[index+2] = new Vector3f(size.x, 1f, element);
            texCoord[index+3] = new Vector3f(0f, 1f, element);
            index+=4;
        }        
        return texCoord;
    }
    
    private Mesh setCollisionBound(Mesh meshToUpdate) {
        meshToUpdate.createCollisionData();
        meshToUpdate.updateBound();
        return meshToUpdate;
    }
    
    private Mesh setAllBuffer(Vector3f[] vertices, Vector3f[] texCoord, int[] index){
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 3, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        return result;
    }

    
}