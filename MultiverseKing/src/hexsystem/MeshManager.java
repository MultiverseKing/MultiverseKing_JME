/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
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
class MeshManager {
    final float sqrt = FastMath.sqrt(3);        //Make life easier.
    final float hexSize = 1f;                   //hex radius.
    final float hexWidth = sqrt * hexSize;      //Make life easier.

    public float getHexSize() {return hexSize;}
    public float getHexWidth() {return hexWidth;}
    
    /**
     * Generate one hex mesh.
     * @return newly generated hex.
     */
    Mesh generateTiles() {
        return generateTiles(1);
    }
    
    /**
     * Generate an hex mesh of the desired size.
     * @param size Number of tiles to link with the Mesh.
     * @return A newly generated mesh.
     */
    Mesh generateTiles(int size) {
        Mesh result = new Mesh();
        if(size != 0){
            result = getTiles(size);
        } else {
            result = getTiles(new Integer(1));
        }
        return result;
    }
    
    /**
     * @todo Texture splatting to handle texturing. jme terrain mat' maybe could do the trick.
     */
    private Mesh getTiles(int size){
        
        Vector3f[] triVertices = getTriVerticesPosition(size);
        Vector3f[] quadVertices = getQuadVerticesPosition(size);
        Vector2f[] triTexCoord = getTriTexCoord(size);
        Vector2f[] quadTexCoord = getQuadTexCoord(size);
        
        int[] triIndex = getTriIndex(size);
        int[] quadIndex = getQuadIndex();
        
        Mesh[] chunk = {new Mesh(), new Mesh()};
        
        chunk[0].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(triVertices));
        chunk[0].setBuffer(VertexBuffer.Type.TexCoord, 2 , BufferUtils.createFloatBuffer(triTexCoord));
        chunk[0].setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(triIndex));
        
        chunk[1].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(quadVertices));
        chunk[1].setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(quadTexCoord));
        chunk[1].setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(quadIndex));
    
        ArrayList<Geometry> geoChunk = new ArrayList<Geometry>();
        geoChunk.add(new Geometry("chunk", chunk[0]));
        geoChunk.add(new Geometry("chunk", chunk[1]));
        
        Mesh finalChunk = new Mesh();
        GeometryBatchFactory.mergeGeometries(geoChunk, finalChunk);
        
        finalChunk.createCollisionData();
        finalChunk.updateBound();
        
        return finalChunk;
    }
    
    private int[] getQuadIndex(){
        int[] index = new int[]{2,3,0, 1,2,0};
        return index;
    }
    
    private int[] getTriIndex(int count){
        int[] index = new int[(count*2)*3];
        
        int j = 0;
        for(int i = 0; i < (count*2)*3; i+= 6){
//            Hex in Y+
            index[i] = j+5;
            index[i+1] = j+1;
            index[i+2] = j;
            
            //Hex in Y-
            index[i+3] = j+2;
            if(((i+6) < ((count*2)*3))){
                index[i+4] = j+6;
            } else {
                index[i+4] = j+4;
            }
            index[i+5] = j+3;
            j+= 4;
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
    
    private Vector3f[] getTriVerticesPosition(int size) {
        Vector3f[] vertices = new Vector3f[size*4+2];
        int j = 0;
        float currentHex;
        for(int i = 0; i < size*4; i+=4){
            currentHex = j*hexWidth;
            vertices[i] = new Vector3f(currentHex, 0, hexSize);
            vertices[i+1] = new Vector3f(currentHex -(hexWidth/2), 0, (hexSize/2));
            vertices[i+2] = new Vector3f(currentHex -(hexWidth/2), 0, (hexSize/2));
            vertices[i+3] = new Vector3f(currentHex, 0, hexSize);
            j++;
        }
        currentHex = j*hexWidth;
        vertices[size*4] = new Vector3f(currentHex - (hexWidth/2), 0, hexSize/2);
        vertices[size*4+1] = new Vector3f(currentHex - (hexWidth/2), 0, hexSize/2);
        
        return vertices;
    }
    
    private Vector3f[] getQuadVerticesPosition(int size){
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(hexWidth/2, 0, hexSize/2);
        vertices[1] = new Vector3f((size * hexWidth)-(hexWidth/2), 0, hexSize/2);
        vertices[2] = new Vector3f((size * hexWidth)-(hexWidth/2), 0, hexSize/2);
        vertices[3] = new Vector3f(hexWidth/2, 0, hexSize/2);
        
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
}