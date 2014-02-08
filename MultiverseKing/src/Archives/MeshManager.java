/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Archives;

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

/**
 * V.01 of GridGenerator, Handle hex mesh generation.
 * At the current stage to get the maximum power of this mesh generation chunk have to be generated on the X axis.
 * @todo Algorithm have to be improve.
 * @todo Mesh update, modification of the mesh even after it hav been instanced. 
 * @todo Maybe make this as a custom control with some adjustement would work better than the current stage ?
 * @see http://www.redblobgames.com/grids/hexagons/#basics as hex algorithm starter guide.
 * @see http://0fps.wordpress.com/2012/06/30/meshing-in-a-minecraft-game/ as the chunk system idea came from there.
 * @author roah
 */
public class MeshManager {

    final float sqrt = FastMath.sqrt(3);        //Make life easier.
    final float hexSize = 1f;                   //hex radius.
    final float hexWidth = sqrt * hexSize;      //Make life easier.
    final float floorHeight = 0.5f;             //ho much the result should be upped
    private Hashtable generatedMesh = new Hashtable();

    public float getHexSize() {return hexSize;}
    public float getHexWidth() {return hexWidth;}
    
    public MeshManager() {
        generatedMesh.put(0, generateTile(1));
    }
    
    /**
     * Generate one hex set to the ground as Y=0.
     * @return newly generated hex.
     */
    Mesh getTile() {
        return (Mesh)generatedMesh.get(0);
    }
    
    /**
     * Return a mesh elevated from the ground (Y=0 == ground).
     * @param floor needed elevation.
     * @return Mesh
     */
    Mesh getHeightedTile(int floor){
        Mesh result = new Mesh();
        if(generatedMesh.containsKey(floor)) {
            return (Mesh)generatedMesh.get(floor);
        } else {
            generatedMesh.put(floor, generateHeightedTile(floor));
            result = (Mesh)generatedMesh.get(floor);
        }
        return result;
    }
    
    /**
     * Generate an hex mesh of the desired size.
     * @param size Number of tiles to link with the Mesh.
     * @return A newly generated mesh.
     */
    Mesh generateTile(int size) {
        Mesh result;
        if(size != 1 && size != 0){
            result = getMultipleTiles(size, 0);
        } else {
            result = getOneTile();
        }
        return result;
    }
    
    
    private Mesh getOneTile(){
        Vector3f[] triVertices = getTriVerticesPosition(1, 0);
        Vector2f[] triTexCoord = getTriTexCoord(1);
        int[] triIndex = getTriIndex(1, true);
        
        return setCollisionBound(setAllBuffer(triVertices, triTexCoord, triIndex));
    }
    
    private Mesh generateHeightedTile(int floor) {
        Vector3f[][] triVertices = new Vector3f[3][];
        triVertices[0] = getTriVerticesPosition(1, getHeight(floor));
        triVertices[1] = getTriVerticesPosition(1, getHeight(floor));
        triVertices[2] = getTriVerticesPosition(1, 0);
        
        Vector2f[] triTexCoord = getTriTexCoord(1);
        Vector2f[] sideTexCoord = getHexHeightSideCoord(floor);
        
        int[] triIndex = getTriIndex(1, true);
        int[] sideIndex = getHexHeightSideIndex();
        
        Vector3f[] combinedVertice = new Vector3f[triVertices[0].length*3+2];
        System.arraycopy(triVertices[0], 0, combinedVertice, 0, triVertices[0].length);
        System.arraycopy(triVertices[1], 0, combinedVertice, triVertices[0].length, triVertices[0].length);
        System.arraycopy(triVertices[2], 0, combinedVertice, triVertices[0].length*2, triVertices[0].length);
        combinedVertice[combinedVertice.length-2] = new Vector3f(0, getHeight(floor), hexSize);
        combinedVertice[combinedVertice.length-1] = new Vector3f(0, 0, hexSize);
                
        Vector2f[] combinedTexCoord = new Vector2f[triTexCoord.length + sideTexCoord.length];
        System.arraycopy(triTexCoord, 0, combinedTexCoord, 0, triTexCoord.length);
        System.arraycopy(sideTexCoord, 0, combinedTexCoord, triTexCoord.length, sideTexCoord.length);
        
        int[] combinedIndex = new int[triIndex.length + sideIndex.length];
        System.arraycopy(triIndex, 0, combinedIndex, 0, triIndex.length);
        System.arraycopy(sideIndex, 0, combinedIndex, triIndex.length, sideIndex.length);
        
        System.out.println(triVertices[0].length);
        System.out.println(combinedTexCoord.length);
        System.out.println(combinedVertice.length);
        
        return setCollisionBound(setAllBuffer(combinedVertice, combinedTexCoord, combinedIndex));
    }
    
    
    
    private Mesh getMultipleTiles(int size, float height){
        Vector3f[] triVertices = getTriVerticesPosition(size, height);
        Vector3f[] quadVertices = getQuadVerticesPosition(size, height);
        Vector2f[] triTexCoord = getTriTexCoord(size);
        Vector2f[] quadTexCoord = getQuadTexCoord(size);
    
        int[] triIndex = getTriIndex(size, false);
        int[] quadIndex = getQuadIndex();
    
        Mesh[] chunk = {new Mesh(), new Mesh()};
        
        chunk[0] = setAllBuffer(triVertices, triTexCoord, triIndex);
        chunk[1] = setAllBuffer(quadVertices, quadTexCoord, quadIndex);
        
        ArrayList<Geometry> geoChunk = new ArrayList<Geometry>();
        geoChunk.add(new Geometry("chunk", chunk[0]));
        geoChunk.add(new Geometry("chunk", chunk[1]));
        
        Mesh finalChunk = new Mesh();
        GeometryBatchFactory.mergeGeometries(geoChunk, finalChunk);
        
        return setCollisionBound(finalChunk);
    }
    
    private int[] getHexHeightSideIndex(){
        int[] result = new int[12*3];
        
        int i = 0;
        int j;
        for(j = 0; j < result.length-6; j+=6){
            result[j] = 6 + i;
            result[j+1] = 6 + i+1;
            result[j+2] = 6*2 + i;
            
            result[j+3] = 6*2 + i+1;
            result[j+4] = 6*2 + i;
            result[j+5] = 6 + i+1;
            
            i++;
        }
        result[j] = 6*2 + i+2;
        result[j+1] = 6 + i;
        result[j+2] = 6*2 + i+1;

        result[j+3] = 6*2 +i;
        result[j+4] = 6 + i;
        result[j+5] = 6*2 + i+2;
        
        return result;
    }
    
    private Vector2f[] getHexHeightSideCoord(int floor) {
        Vector2f[] result = new Vector2f[12+2];
        
        int revert = 1;
        float xOffset = 0;
        for(int i = 0; i < result.length-2; i+=2){
            xOffset = (i >= (result.length-2)/2) ? floor/2 : 0f;
            result[i] = new Vector2f(xOffset, 0.25f);
            result[i+1] = new Vector2f(xOffset, 0.75f);
        }
        result[result.length-2] = new Vector2f(0, 0.25f);
        result[result.length-1] = new Vector2f(floor/2, 0.25f);
        
        return result;
    }
    
    private int[] getQuadIndex(){
        int[] index = new int[]{2,3,0, 1,2,0};
        return index;
    }
    
    private int[] getTriIndex(int size, boolean mergedQuad){
        int[] index = (!mergedQuad) ? new int[(size*2)*3] : new int[(size*2+2)*3];
        
        int j = 0;
        for(int i = 0; i < (size*2)*3; i+= 6){
//            Hex in Y+
            index[i] = j+5;
            index[i+1] = j+1;
            index[i+2] = j;
            
            //Hex in Y-
            index[i+3] = j+2;
            if(((i+6) < ((size*2)*3))){
                index[i+4] = j+6;
            } else {
                index[i+4] = j+4;
            }
            index[i+5] = j+3;
            j+= 4;
        }
        
        if(mergedQuad){
            index[(size*2)*3] = 1;
            index[(size*2)*3+1] = 4;
            index[(size*2)*3+2] = 2;
            
            index[(size*2)*3+3] = 1;
            index[(size*2)*3+4] = 5;
            index[(size*2)*3+5] = 4;
        }
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
    
    private Vector3f[] getTriVerticesPosition(int size, float height) {
        Vector3f[] vertices = new Vector3f[size*4+2];
        int j = 0;
        float currentHex;
        for(int i = 0; i < size*4; i+=4){
            currentHex = j*hexWidth; 
            vertices[i] = new Vector3f(currentHex, height, hexSize);
            vertices[i+1] = new Vector3f(currentHex -(hexWidth/2), height, (hexSize/2));
            vertices[i+2] = new Vector3f(currentHex -(hexWidth/2), height, -(hexSize/2));
            vertices[i+3] = new Vector3f(currentHex, height, -hexSize);
            j++;
        }
        currentHex = j*hexWidth;
        vertices[size*4] = new Vector3f(currentHex - (hexWidth/2), height, -(hexSize/2));
        vertices[size*4+1] = new Vector3f(currentHex - (hexWidth/2), height, hexSize/2);
        
        return vertices;
    }
    
    private Vector3f[] getQuadVerticesPosition(int size, float height){
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-(hexWidth/2), height, hexSize/2);
        vertices[1] = new Vector3f((size * hexWidth)-(hexWidth/2), height, hexSize/2);
        vertices[2] = new Vector3f((size * hexWidth)-(hexWidth/2), height, -(hexSize/2));
        vertices[3] = new Vector3f(-(hexWidth/2), height, -(hexSize/2));
        
        return vertices;
    }
    
    private Vector2f[] getQuadTexCoord(int count){
        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0f, 0.25f);
        texCoord[1] = new Vector2f((float)count, 0.25f);
        texCoord[2] = new Vector2f((float)count, 0.75f);
        texCoord[3] = new Vector2f(0f, 0.75f);
        
        return texCoord;
    }
    
    private Vector2f[] getTriTexCoord(int count){
        //@help: count*4+2 == (tri number on X)*(top tri & bot tri)*(tri vertice number minus 1(shared vertice))+(2 last vertice)
        Vector2f[] texCoord = new Vector2f[count*4+2];  
        
        for(int k = 0; k < 2; k++){
            int j = 0;
            for(int i = 0; i < count*4; i+=4){
                texCoord[i] = new Vector2f(j+0.5f, 0f);
                texCoord[i+1] = new Vector2f(j, 0.25f);
                texCoord[i+2] = new Vector2f(j, 0.75f);
                texCoord[i+3] = new Vector2f(j+0.5f, 1f);
                j++;
            }
            j--;
            texCoord[count*4] = new Vector2f(j+1f, 0.75f);
            texCoord[count*4+1] = new Vector2f(j+1f, 0.25f);
        }
        return texCoord;
    }

    private float getHeight(int floor) {
        return floor*floorHeight;
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