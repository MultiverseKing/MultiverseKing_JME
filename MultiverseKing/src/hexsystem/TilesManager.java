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
class TilesManager {
    final float sqrt = FastMath.sqrt(3);        //Make life easier.
    final float hexSize = 1f;                   //hex radius.
    final float hexWidth = sqrt * hexSize;      //Make life easier.
    private final AssetManager assetManager;    //Used to set Material and texture, should be moved in another place?

    public float getHexSize() {return hexSize;}
    public float getHexWidth() {return hexWidth;}
//    public AssetManager getAssetManager() {return assetManager;}    //Pointless?
    
    
    /**
     * Constructor to initialize the Manager, generateTiles() have to be call to generate a Chunk/TileGroup.
     * @see generateJMESpatialTiles()
     * @param assetManager Must be set.
     */
    TilesManager(AssetManager assetManager){
        this.assetManager = assetManager;
    }
    
    /**
     * Generate a chunk, the odd row is set to false as defaul (odd variable cant be NULL).
     * @param count Number of tiles to link with the chunk. (!= 0, min value == 1).
     * @param gridPosition Currently not used, have to be rework.
     * @return Newly generated chunk.
     */
    Spatial generateTiles(int count, int gridPosition) {
        return generateJMESpatialTiles(getMergedTiles(false, count, gridPosition), gridPosition);
    }
    
    /**
     * Generate a chunk, the odd row have to be set as a boolean.
     * @param count Number of tiles to link with the chunk. (!= 0, min value == 1).
     * @param gridPosition Currently not used, have to be rework.
     * @param odd Currently not used, have to be rework.
     * @return A newly generated chunk.
     */
    Spatial generateTiles(int count, int gridPosition, boolean odd) {
        return generateJMESpatialTiles(getMergedTiles(odd, count, gridPosition), gridPosition);
    }
    
    /**
     * Texture Splatter have to be used to handle texture. 
     * @todo Texture splatting to handle texturing. jme terrain mat' maybe could do the trick.
     * @param odd @deprecated
     * @param count 
     * @param gridPosition @deprecated
     * @return Generated Mesh.
     */
    private Mesh getMergedTiles(boolean odd, int count, int gridPosition){
        
        Vector3f[] triVertices = getTriVerticesPosition(odd, count, gridPosition);
        Vector3f[] quadVertices = getQuadVerticesPosition(odd, count, gridPosition);
        Vector2f[] triTexCoord = getTriTexCoord(count);
        Vector2f[] quadTexCoord = getQuadTexCoord(count);
        
        int[] triIndex = getTriIndex(count);
        int[] quadIndex = getQuadIndex();
        
        Mesh[] chunk = {new Mesh(), new Mesh()};
        
        chunk[0].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(triVertices));
        chunk[0].setBuffer(VertexBuffer.Type.TexCoord, 2 , BufferUtils.createFloatBuffer(triTexCoord));
        chunk[0].setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(triIndex));
        
        chunk[1].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(quadVertices));
        chunk[1].setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(quadTexCoord));
        chunk[1].setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(quadIndex));
    
        ArrayList<Geometry> geoChunk = new ArrayList<Geometry>();
        geoChunk.add(new Geometry("chunk "+gridPosition, chunk[0]));
        geoChunk.add(new Geometry("chunk "+gridPosition, chunk[1]));
        
        Mesh finalChunk = new Mesh();
        GeometryBatchFactory.mergeGeometries(geoChunk, finalChunk);
        
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
        
//      XY == World position
//      ++++++++++++++++++++++++++++++++++++++ = Y-
//      +++++[03]++++[07]++++[11]++++[15]+++++
//      +[02]++++[06]++++[10]++++[14]++++[16]+
//      +++++[XY]++++[XY]++++[XY]++++[XY]+++++
//      +[01]++++[05]++++[09]++++[13]++++[17]+
//      +++++[00]++++[04]++++[08]++++[12]+++++
//      ++++++++++++++++++++++++++++++++++++++ = Y+
         
        return index;
    }
    
    private Vector3f[] getTriVerticesPosition(boolean odd, int count, int gridPosition) {
        Vector3f[] vertices = new Vector3f[count*4+2];
        float currentGridPosition =  (float)(((float)(gridPosition) * (float)(hexSize)*2));
        if(gridPosition != 0){
            currentGridPosition *= 0.75f;
        }
        int j = 0;
        float currentHex;
        for(int i = 0; i < count*4; i+=4){
            currentHex = j*hexWidth;
            if(odd){
                currentHex += hexWidth/2;
            }
            vertices[i] = new Vector3f(currentHex, 0, currentGridPosition + hexSize);
            vertices[i+1] = new Vector3f(currentHex -(hexWidth/2), 0, currentGridPosition +(hexSize/2));
            vertices[i+2] = new Vector3f(currentHex -(hexWidth/2), 0, currentGridPosition -(hexSize/2));
            vertices[i+3] = new Vector3f(currentHex, 0, currentGridPosition - hexSize);
            j++;
        }
        currentHex = j*hexWidth;
        if(odd){
            currentHex += hexWidth/2;
        }
        vertices[count*4] = new Vector3f(currentHex - (hexWidth/2), 0, currentGridPosition -(hexSize/2));
        vertices[count*4+1] = new Vector3f(currentHex - (hexWidth/2), 0, currentGridPosition +(hexSize/2));
        
        return vertices;
    }
    
    private Vector3f[] getQuadVerticesPosition(boolean odd, int count, int gridPosition){
        Vector3f[] vertices = new Vector3f[4];
        float currentGridPosition = (float)(((float)(gridPosition) * (float)(hexSize)*2));
        float decallage = 0f;
        if(gridPosition != 0){
            currentGridPosition *= 0.75f;
        }
        if(odd){
            decallage = (hexWidth/2);
        }
        vertices[0] = new Vector3f(decallage -(hexWidth/2), 0, currentGridPosition + (hexSize/2));
        vertices[1] = new Vector3f((count * hexWidth)-(hexWidth/2) + decallage, 0, currentGridPosition + (hexSize/2));
        vertices[2] = new Vector3f((count * hexWidth)-(hexWidth/2) + decallage, 0, currentGridPosition - (hexSize/2));
        vertices[3] = new Vector3f(decallage -(hexWidth/2), 0, currentGridPosition - (hexSize/2));
        
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
 
    /**
     * @deprecated Not the right place.
     * @param finalChunk
     * @param gridPosition
     * @return 
     */
    private Spatial generateJMESpatialTiles(Mesh finalChunk, int gridPosition) {
        Geometry finalGeometryChunk = new Geometry(Integer.toString(gridPosition), finalChunk);
        Material material = assetManager.loadMaterial("Materials/hexMat.j3m");
        finalGeometryChunk.setLocalTranslation(Vector3f.ZERO);
        finalGeometryChunk.setMaterial(material);
        finalGeometryChunk.setShadowMode(RenderQueue.ShadowMode.Receive);
        finalGeometryChunk.getMesh().createCollisionData();
        finalGeometryChunk.getMesh().updateBound();
//        material.getAdditionalRenderState().setWireframe(true);    //Debuging purpose.
        return (Spatial)finalGeometryChunk;
    }

    /**
     * @deprecated not the right place.
     */
//    Spatial generateEmptyZone(Vector2Int mapSize, HexTile[][] hexTiles) {
//        
//        Spatial[] chunk = new Geometry[mapSize.y];
//        Node result = new Node("EditorZone");
//
//        for(int y = 0; y < mapSize.y; y++){
//            for(int x = 0; x < mapSize.x; x++){
//                hexTiles[x][y] = new HexTile(ElementalAttribut.NATURE);
//            }
//            if(y % 2 == 1) {              //if is odd row
//                chunk[y] = generateTiles(mapSize.x, y, true);
//            } else {
//                chunk[y] = generateTiles(mapSize.x, y);
//            }
//            result.attachChild(chunk[y]);
//        }
////        GeometryBatchFactory.optimize(result);
//        return result;
//    }
    
}