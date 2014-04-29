package archives;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import hexsystem.HexSettings;
import java.util.ArrayList;
import utility.Vector2Int;

/**
 * Generate the mesh of the hexMap, this work with unshaded array shader.
 * @todo improve MeshManagerV1 && MeshManagerV2 to generate the mesh without needing of unshaded array shader.
 * @author roah
 */
public class MeshManager {

    private final float hexSize;        //hex radius.
    private final float hexWidth;       //Make life easier.
    private final float floorHeight;    //how much the result should be upped.

    public MeshManager(HexSettings settings) {
        this.hexSize = settings.getHEX_RADIUS();
        this.hexWidth = FastMath.sqrt(3) * hexSize;
        this.floorHeight = settings.getFloorHeight();
    }

    /**
     * Create a flat grid at specifiate position, specifiate size and specifiate
     * attribut.
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
     * Create a grid at specifiate position, size, height and texture
     * coordinate.
     * @param position where to start the mesh.
     * @param size number of tile inside the mesh.
     * @param height height of the mesh.
     * @param element texture to use.
     * @return newly generated tile grid mesh.
     */
    Mesh getMesh(Vector2Int position, Vector2Int size, int height, int element) {
        Vector3f[] vertices = getVerticesPosition(position, size, height);
        Vector3f[] texCoord = getTexCoord(size, element);
        int[] index = getIndex(size.y, 0);

        return setCollisionBound(setAllBuffer(vertices, texCoord, index));
    }

//    /**
//     * 
//     * @param meshParam
//     * @return only hex side mesh
//     * @deprecated
//     */
//    Mesh getHeight(MeshParameter meshParam) {
//        ArrayList<Vector3f[]> vertices = new ArrayList<Vector3f[]>();
//        ArrayList<Vector3f[]> texCoord = new ArrayList<Vector3f[]>();
//        ArrayList<int[]> index = new ArrayList<int[]>();
//
//        int mergedVerticeCount = 0;
//        int mergedtextCoordCount = 0;
//        int mergedIndexCount = 0;
//
//        for (int i = 0; i < meshParam.size(); i++) {
//            if (meshParam.getHeight(i) != 0) {
//                Vector3f[] vert = getSideVerticesPosition(meshParam.getPosition(i), meshParam.getSize(i), meshParam.getHeight(i));
//                Vector3f[] tex = getSideVerticestexCoord(meshParam.getSize(i), meshParam.getHeight(i), meshParam.getElementType(i));
//                int[] indice = getSideIndex(meshParam.getSize(i).x, mergedVerticeCount, meshParam.getCulling(i));
//
//                vertices.add(vert);
//                texCoord.add(tex);
//                index.add(indice);
//
//                mergedVerticeCount += vert.length;
//                mergedtextCoordCount += tex.length;
//                mergedIndexCount += indice.length;
//            }
//        }
//        Vector3f[] mergedVertices = new Vector3f[mergedVerticeCount];
//        Vector3f[] mergedtextCoord = new Vector3f[mergedtextCoordCount];
//        int[] mergedIndex = new int[mergedIndexCount];
//
//        mergedVerticeCount = 0;
//        mergedtextCoordCount = 0;
//        mergedIndexCount = 0;
//        for (int i = 0; i < vertices.size(); i++) {
//            System.arraycopy(vertices.get(i), 0, mergedVertices, mergedVerticeCount, vertices.get(i).length);
//            System.arraycopy(texCoord.get(i), 0, mergedtextCoord, mergedtextCoordCount, texCoord.get(i).length);
//            System.arraycopy(index.get(i), 0, mergedIndex, mergedIndexCount, index.get(i).length);
//
//            mergedVerticeCount += vertices.get(i).length;
//            mergedtextCoordCount += texCoord.get(i).length;
//            mergedIndexCount += index.get(i).length;
//        }
//        return setCollisionBound(setAllBuffer(mergedVertices, mergedtextCoord, mergedIndex));
////        return null;
//    }

    Mesh getMergedMesh(MeshParameter meshParam) {
        ArrayList<Vector3f[]> vertices = new ArrayList<Vector3f[]>();
        ArrayList<Vector3f[]> texCoord = new ArrayList<Vector3f[]>();
        ArrayList<int[]> index = new ArrayList<int[]>();

        int mergedVerticeCount = 0;
        int mergedtextCoordCount = 0;
        int mergedIndexCount = 0;

        for (int i = 0; i < meshParam.size(); i++) {
            Vector3f[] groundVert = getVerticesPosition(meshParam.getPosition(i), meshParam.getSize(i), meshParam.getHeight(i));
            Vector3f[] groundTex = getTexCoord(meshParam.getSize(i), meshParam.getElementType(i));
            int[] groundIndice = getIndex(meshParam.getSize(i).y, mergedVerticeCount);

            mergedtextCoordCount += groundTex.length;
            mergedVerticeCount += groundVert.length;
            mergedIndexCount += groundIndice.length;

            if(meshParam.getHeight(i) != 0){
                Vector3f[] sideVert = getSideVerticesPosition(meshParam.getPosition(i), meshParam.getSize(i), meshParam.getHeight(i));
                Vector3f[] vertCombi = new Vector3f[sideVert.length+groundVert.length];
                System.arraycopy(groundVert, 0, vertCombi, 0, groundVert.length);
                System.arraycopy(sideVert, 0, vertCombi, groundVert.length, sideVert.length);
                vertices.add(vertCombi);
                
                Vector3f[] sideTex = getSideVerticestexCoord(meshParam.getSize(i), meshParam.getHeight(i), meshParam.getElementType(i));
                Vector3f[] texCombi = new Vector3f[sideTex.length+groundTex.length];
                System.arraycopy(groundTex, 0, texCombi, 0, groundTex.length);
                System.arraycopy(sideTex, 0, texCombi, groundTex.length, sideTex.length);
                texCoord.add(texCombi);
                
                int[] sideIndice = getSideIndex(meshParam.getSize(i).x, mergedVerticeCount, meshParam.getCulling(i));
                int[] indiceCombi = new int[sideIndice.length+groundIndice.length];
                System.arraycopy(groundIndice, 0, indiceCombi, 0, groundIndice.length);
                System.arraycopy(sideIndice, 0, indiceCombi, groundIndice.length, sideIndice.length);
                index.add(indiceCombi);
                
                mergedIndexCount += sideIndice.length;
                mergedtextCoordCount += sideTex.length;
                mergedVerticeCount += sideVert.length;
            } else {
                vertices.add(groundVert);
                texCoord.add(groundTex);
                index.add(groundIndice);
            }

        }
        Vector3f[] mergedVertices = new Vector3f[mergedVerticeCount];
        Vector3f[] mergedtextCoord = new Vector3f[mergedtextCoordCount];
        int[] mergedIndex = new int[mergedIndexCount];

        mergedVerticeCount = 0;
        mergedtextCoordCount = 0;
        mergedIndexCount = 0;
        for (int i = 0; i < meshParam.size(); i++) {
            System.arraycopy(vertices.get(i), 0, mergedVertices, mergedVerticeCount, vertices.get(i).length);
            System.arraycopy(texCoord.get(i), 0, mergedtextCoord, mergedtextCoordCount, texCoord.get(i).length);
            System.arraycopy(index.get(i), 0, mergedIndex, mergedIndexCount, index.get(i).length);

            mergedVerticeCount += vertices.get(i).length;
            mergedtextCoordCount += texCoord.get(i).length;
            mergedIndexCount += index.get(i).length;
        }
        return setCollisionBound(setAllBuffer(mergedVertices, mergedtextCoord, mergedIndex));
    }

    private Vector3f[] getVerticesPosition(Vector2Int position, Vector2Int size, float height) {
        Vector3f[] vertices = new Vector3f[4 * size.y];
        int index = 0;
        for (int i = 0; i < size.y; i++) {
            if ((position.y + i & 1) == 0) { //even
                vertices[index] = new Vector3f(-(hexWidth / 2) + (position.x * hexWidth), height * floorHeight, hexSize + ((position.y + i) * (hexSize * 1.5f)));
                vertices[index + 1] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (position.x * hexWidth), height * floorHeight, hexSize + ((position.y + i) * (hexSize * 1.5f)));
                vertices[index + 2] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (position.x * hexWidth), height * floorHeight, -hexSize + ((position.y + i) * (hexSize * 1.5f)));
                vertices[index + 3] = new Vector3f(-(hexWidth / 2) + (position.x * hexWidth), height * floorHeight, -hexSize + ((position.y + i) * (hexSize * 1.5f)));
            } else {
                vertices[index] = new Vector3f((position.x * hexWidth), height * floorHeight + 0.001f, hexSize + ((position.y + i) * (hexSize * 1.5f)));
                vertices[index + 1] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth), height * floorHeight + 0.001f, hexSize + ((position.y + i) * (hexSize * 1.5f)));
                vertices[index + 2] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth), height * floorHeight + 0.001f, -hexSize + ((position.y + i) * (hexSize * 1.5f)));
                vertices[index + 3] = new Vector3f((position.x * hexWidth), height * floorHeight + 0.001f, -hexSize + ((position.y + i) * (hexSize * 1.5f)));
            }
            index += 4;
        }

        return vertices;
    }

    private Vector3f[] getSideVerticesPosition(Vector2Int position, Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[(size.x *4+3)*2];
        float posX;
        float posY = ((position.y) * hexSize * 1.5f);
        float posZ;
        for(int i = 0; i < size.x; i++){
            int r = i * 8;
            if(((position.y)&1) == 0){ //even
                posX = (position.x*hexWidth) + (i*hexWidth);
                posZ = 0;
            } else {
                posX = (position.x*hexWidth) + (i*hexWidth)+(hexWidth/2);
                posZ = 0.001f;
            }
            vertices[r] = new Vector3f(posX, height*floorHeight+posZ, hexSize + posY);
            vertices[r+1] = new Vector3f(posX-(hexWidth/2), height*floorHeight+posZ, (hexSize/2) + posY);
            vertices[r+2] = new Vector3f(posX, height*floorHeight+posZ, -hexSize + posY);
            vertices[r+3] = new Vector3f(posX-(hexWidth/2), height*floorHeight+posZ, -(hexSize/2) + posY);
            
            vertices[r+4] = new Vector3f(posX, posZ, hexSize + posY);
            vertices[r+5] = new Vector3f(posX-(hexWidth/2), posZ, (hexSize/2) + posY);
            vertices[r+6] = new Vector3f(posX, posZ, -hexSize + posY);
            vertices[r+7] = new Vector3f(posX-(hexWidth/2), posZ, -(hexSize/2) + posY);
        }
        posX = position.x*hexWidth+size.x*hexWidth+((((position.y)&1) == 0 ? -hexWidth/2 : 0));
        vertices[vertices.length-6] = new Vector3f(posX, height*floorHeight+(((position.y)&1) == 0 ? 0 : 0.001f), hexSize/2 + posY);
        vertices[vertices.length-5] = new Vector3f(posX, height*floorHeight+(((position.y)&1) == 0 ? 0 : 0.001f), -hexSize/2 + posY);
        vertices[vertices.length-4] = new Vector3f(posX, (((position.y)&1) == 0 ? 0 : 0.001f), hexSize/2 + posY);
        vertices[vertices.length-3] = new Vector3f(posX, (((position.y)&1) == 0 ? 0 : 0.001f), -hexSize/2 + posY);
        
        vertices[vertices.length-2] = new Vector3f(posX, height*floorHeight+(((position.y)&1) == 0 ? 0 : 0.001f), -hexSize/2 + posY);
        vertices[vertices.length-1] = new Vector3f(posX, (((position.y)&1) == 0 ? 0 : 0.001f), -hexSize/2 + posY);
        
        return vertices;
    }

    private int[] getIndex(int sizeY, int offset) {
        int[] index = new int[sizeY * 2 * 3];
        int j = 0;
        for (int i = 0; i < sizeY * 4; i += 4) {
            index[j] = i + 2 + offset;
            index[j + 1] = i + 3 + offset;
            index[j + 2] = i + offset;

            index[j + 3] = i + 1 + offset;
            index[j + 4] = i + 2 + offset;
            index[j + 5] = i + offset;

            j += 6;
        }
        return index;
    }

    private int[] getSideIndex(int sizeX, int offset, Boolean[][] isNeightborsCull) {
        int[] index = new int[((sizeX * 4) + 2) * 2 * 3]; //2 tri * 3 point

        if(isNeightborsCull[0][3]){
            index[0] = offset + 1;
            index[1] = offset + 7;
            index[2] = offset + 5;

            index[3] = offset + 1;
            index[4] = offset + 3;
            index[5] = offset + 7;
        }
        
        int j = offset;
        for (int i = 0; i < sizeX; i++) {
            int r = (i * 24) + 6;
            if(isNeightborsCull[i][4]){
                index[r] = j;
                index[r + 1] = j + 1;
                index[r + 2] = j + 4;

                index[r + 3] = j + 1;
                index[r + 4] = j + 5;
                index[r + 5] = j + 4;
            }

            if(isNeightborsCull[i][2]){
                index[r + 6] = j + 2;
                index[r + 7] = j + 6;
                index[r + 8] = j + 3;

                index[r + 9] = j + 3;
                index[r + 10] = j + 6;
                index[r + 11] = j + 7;
            }
            
            if(i < sizeX-1){
                if(isNeightborsCull[i][5]){
                    index[r + 12] = j + 0;
                    index[r + 13] = j + 4;
                    index[r + 14] = j + 9;

                    index[r + 15] = j + 9;
                    index[r + 16] = j + 4;
                    index[r + 17] = j + 13;
                }
                if(isNeightborsCull[i][1]){
                    index[r + 18] = j + 11;
                    index[r + 19] = j + 6;
                    index[r + 20] = j + 2;

                    index[r + 21] = j + 11;
                    index[r + 22] = j + 15;
                    index[r + 23] = j + 6;
                }
            } else {
                if(isNeightborsCull[i][5]){
                    index[r + 12] = j + 0;
                    index[r + 13] = j + 4;
                    index[r + 14] = j + 8;

                    index[r + 15] = j + 4;
                    index[r + 16] = j + 10;
                    index[r + 17] = j + 8;
                }

                if(isNeightborsCull[i][1]){
                    index[r + 18] = j + 6;
                    index[r + 19] = j + 2;
                    index[r + 20] = offset + ((sizeX *4+3)*2)-3;

                    index[r + 21] = j + 2;
                    index[r + 22] = offset + ((sizeX *4+3)*2)-5;
                    index[r + 23] = offset + ((sizeX *4+3)*2)-3;
                }
            }
            j += 8;
        }

        if(isNeightborsCull[sizeX-1][0]){
            index[index.length - 6] = offset + ((sizeX *4+3)*2)-6;
            index[index.length - 5] = offset + ((sizeX *4+3)*2)-4;
            index[index.length - 4] = offset + ((sizeX *4+3)*2)-2;

            index[index.length - 3] = offset + ((sizeX *4+3)*2)-4;
            index[index.length - 2] = offset + ((sizeX *4+3)*2)-1;
            index[index.length - 1] = offset + ((sizeX *4+3)*2)-2;
        }
        return index;
    }

    private Vector3f[] getTexCoord(Vector2Int size, int element) {
        Vector3f[] texCoord = new Vector3f[4 * size.y];
        int index = 0;
        for (int i = 0; i < size.y; i++) {
            texCoord[index] = new Vector3f(0f, 0f, element);
            texCoord[index + 1] = new Vector3f(size.x, 0f, element);
            texCoord[index + 2] = new Vector3f(size.x, 1f, element);
            texCoord[index + 3] = new Vector3f(0f, 1f, element);
            index += 4;
        }
        return texCoord;
    }

    private Vector3f[] getSideVerticestexCoord(Vector2Int size, int height, int x) {
        Vector3f[] texCoord = new Vector3f[(size.x *4+3)*2];
        float h = height;
        int j = 0;
        for(int i = 0; i < size.x; i++){
            texCoord[j] = new Vector3f(0, 0.25f, x);
            texCoord[j+1] = new Vector3f(0, 0.75f, x);
            texCoord[j+2] = new Vector3f(0, 0.75f, x);
            texCoord[j+3] = new Vector3f(0, 0.25f, x);

            texCoord[j+4] = new Vector3f(h, 0.25f, x);
            texCoord[j+5] = new Vector3f(h, 0.75f, x);
            texCoord[j+6] = new Vector3f(h, 0.75f, x);
            texCoord[j+7] = new Vector3f(h, 0.25f, x);
            j+=8;
        }
        texCoord[texCoord.length-6] = new Vector3f(0, 0.75f, x);
        texCoord[texCoord.length-5] = new Vector3f(0, 0.25f, x);
        texCoord[texCoord.length-4] = new Vector3f(h, 0.75f, x);
        texCoord[texCoord.length-3] = new Vector3f(h, 0.25f, x);
        
        texCoord[texCoord.length-2] = new Vector3f(0, 0.25f, x);
        texCoord[texCoord.length-1] = new Vector3f(h, 0.25f, x);
        
        return texCoord;
    }
    
    private Mesh setCollisionBound(Mesh meshToUpdate) {
        meshToUpdate.createCollisionData();
        meshToUpdate.updateBound();
        return meshToUpdate;
    }

    private Mesh setAllBuffer(Vector3f[] vertices, Vector3f[] texCoord, int[] index) {
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 3, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        return result;
    }

    
}