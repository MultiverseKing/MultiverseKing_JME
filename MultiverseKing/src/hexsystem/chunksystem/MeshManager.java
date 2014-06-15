package hexsystem.chunksystem;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import hexsystem.HexSettings;
import java.util.ArrayList;
import utility.Vector2Int;

/**
 * @todo improve method to not have the use of System.ArrayCopy
 * @author roah
 */
public class MeshManager {

    private final float hexRadius = HexSettings.HEX_RADIUS;
    private final float hexWidth = HexSettings.HEX_WIDTH;
    private final float floorHeight = HexSettings.FLOOR_OFFSET;

    /**
     * Generate a mesh accordingly to meshParam.
     *
     * @param meshParam parameter to use.
     * @return newly generated Mesh.
     */
    public Mesh getMesh(MeshParameter meshParam) {
        ArrayList<Vector3f[]> vertices = new ArrayList<Vector3f[]>();
        ArrayList<Vector2f[]> texCoord = new ArrayList<Vector2f[]>();
        ArrayList<int[]> index = new ArrayList<int[]>();

        int mergedVerticeCount = 0;
        int mergedtextCoordCount = 0;
        int mergedIndexCount = 0;

        while (meshParam.hasNext()) {
            Vector3f[] groundTriVert = getTriVerticesPosition(meshParam.getPositionParam(), meshParam.getSizeParam(), meshParam.getHeightParam());
            Vector2f[] groundTriTex = getTriTexCoord(meshParam.getSizeParam());
            int[] groundTriIndice = getTriIndex(meshParam.getSizeParam(), mergedVerticeCount);

            Vector3f[] groundQuadVert = getQuadVerticesPosition(meshParam.getPositionParam(), meshParam.getSizeParam(), meshParam.getHeightParam());
            Vector2f[] groundQuadTex = getQuadTexCoord(meshParam.getSizeParam());
            int[] groundQuadIndice = getQuadIndex(meshParam.getSizeParam(), mergedVerticeCount + groundTriVert.length);

            Vector3f[] groundVert = new Vector3f[groundTriVert.length + groundQuadVert.length];
            Vector2f[] groundTex = new Vector2f[groundTriTex.length + groundQuadTex.length];
            int[] groundIndice = new int[groundTriIndice.length + groundQuadIndice.length];

            System.arraycopy(groundTriVert, 0, groundVert, 0, groundTriVert.length);
            System.arraycopy(groundQuadVert, 0, groundVert, groundTriVert.length, groundQuadVert.length);
            System.arraycopy(groundTriTex, 0, groundTex, 0, groundTriTex.length);
            System.arraycopy(groundQuadTex, 0, groundTex, groundTriTex.length, groundQuadTex.length);
            System.arraycopy(groundTriIndice, 0, groundIndice, 0, groundTriIndice.length);
            System.arraycopy(groundQuadIndice, 0, groundIndice, groundTriIndice.length, groundQuadIndice.length);

            mergedtextCoordCount += groundTex.length;
            mergedVerticeCount += groundVert.length;
            mergedIndexCount += groundIndice.length;

            if (!meshParam.onlyGround() && meshParam.getHeightParam() != 0) {
                Vector3f[] sideVert = getSideVerticesPosition(meshParam.getPositionParam(), meshParam.getSizeParam(), meshParam.getHeightParam());
                Vector3f[] vertCombi = new Vector3f[sideVert.length + groundVert.length];
                System.arraycopy(groundVert, 0, vertCombi, 0, groundVert.length);
                System.arraycopy(sideVert, 0, vertCombi, groundVert.length, sideVert.length);
                vertices.add(vertCombi);

                Vector2f[] sideTex = getSideVerticestexCoord(meshParam.getSizeParam(), meshParam.getHeightParam());
                Vector2f[] texCombi = new Vector2f[sideTex.length + groundTex.length];
                System.arraycopy(groundTex, 0, texCombi, 0, groundTex.length);
                System.arraycopy(sideTex, 0, texCombi, groundTex.length, sideTex.length);
                texCoord.add(texCombi);

                int[] sideIndice = getSideIndex(meshParam.getSizeParam().x, mergedVerticeCount, meshParam.getCulling());
                int[] indiceCombi = new int[sideIndice.length + groundIndice.length];
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
        Vector2f[] mergedtextCoord = new Vector2f[mergedtextCoordCount];
        int[] mergedIndex = new int[mergedIndexCount];

        mergedVerticeCount = 0;
        mergedtextCoordCount = 0;
        mergedIndexCount = 0;
        for (int i = 0; i < meshParam.getElementMeshCount(); i++) {
            System.arraycopy(vertices.get(i), 0, mergedVertices, mergedVerticeCount, vertices.get(i).length);
            System.arraycopy(texCoord.get(i), 0, mergedtextCoord, mergedtextCoordCount, texCoord.get(i).length);
            System.arraycopy(index.get(i), 0, mergedIndex, mergedIndexCount, index.get(i).length);

            mergedVerticeCount += vertices.get(i).length;
            mergedtextCoordCount += texCoord.get(i).length;
            mergedIndexCount += index.get(i).length;
        }
        return setCollisionBound(setAllBuffer(mergedVertices, mergedtextCoord, mergedIndex));
    }

    private Vector3f[] getTriVerticesPosition(Vector2Int offset, Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[size.x * 4 + 2];
        int j = 0;
        float currentHexPos;
        for (int i = 0; i < size.x * 4; i += 4) {
            currentHexPos = j * hexWidth + (offset.x * hexWidth);
            if ((size.y + offset.y & 1) == 0) {
                vertices[i] = new Vector3f(currentHexPos + hexWidth / 2, height * floorHeight,
                        -hexRadius + (offset.y * (hexRadius * 1.5f)));
                vertices[i + 1] = new Vector3f(currentHexPos, height * floorHeight,
                        -(hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            } else {
                vertices[i] = new Vector3f(currentHexPos, height * floorHeight, -hexRadius + (offset.y * (hexRadius * 1.5f)));
                vertices[i + 1] = new Vector3f(currentHexPos - hexWidth / 2, height * floorHeight, -(hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            }
            if ((size.y + offset.y & 1) == 0) {
                currentHexPos = (j * hexWidth) + (hexWidth / 2) + (offset.x * hexWidth);
                vertices[i + 2] = new Vector3f(currentHexPos - hexWidth / 2, height * floorHeight,
                        ((hexRadius * size.y) - hexRadius / 2) + ((size.y - 1) * (hexRadius / 2)) + (offset.y * (hexRadius * 1.5f)));
                vertices[i + 3] = new Vector3f(currentHexPos, height * floorHeight,
                        (hexRadius * size.y) + ((size.y - 1) * (hexRadius / 2)) + (offset.y * (hexRadius * 1.5f)));
            } else {
                vertices[i + 2] = new Vector3f(currentHexPos - (hexWidth / 2), height * floorHeight,
                        ((hexRadius * size.y) - hexRadius / 2) + ((size.y - 1) * (hexRadius / 2)) + (offset.y * (hexRadius * 1.5f)));
                vertices[i + 3] = new Vector3f(currentHexPos, height * floorHeight,
                        (hexRadius * size.y) + ((size.y - 1) * (hexRadius / 2)) + (offset.y * (hexRadius * 1.5f)));
            }
            j++;
        }

        currentHexPos = (j * hexWidth) + (offset.x * hexWidth);
        if ((size.y + offset.y & 1) == 0) {
            vertices[size.x * 4] = new Vector3f(currentHexPos, height * floorHeight,
                    -hexRadius / 2 + (offset.y * (hexRadius * 1.5f)));
        } else {
            vertices[size.x * 4] = new Vector3f(currentHexPos - hexWidth / 2, height * floorHeight,
                    -hexRadius / 2 + (offset.y * (hexRadius * 1.5f)));
        }
        if ((size.y + offset.y & 1) == 0) {
            vertices[size.x * 4 + 1] = new Vector3f(currentHexPos, height * floorHeight,
                    ((hexRadius * size.y) - hexRadius / 2) + ((size.y - 1) * (hexRadius / 2)) + (offset.y * (hexRadius * 1.5f)));
        } else {
            vertices[size.x * 4 + 1] = new Vector3f(currentHexPos - hexWidth / 2, height * floorHeight,
                    ((hexRadius * size.y) - hexRadius / 2) + ((size.y - 1) * (hexRadius / 2)) + (offset.y * (hexRadius * 1.5f)));
        }

        return vertices;
    }

    private Vector3f[] getQuadVerticesPosition(Vector2Int offset, Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[(4 * size.y) + ((size.y - 1) * 2)];
        int index = 0;

        //generate the first quad
        if (((offset.y) & 1) == 0) { //even
            vertices[index] = new Vector3f(-(hexWidth / 2) + (offset.x * hexWidth),
                    height * floorHeight, -(hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            vertices[index + 1] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (offset.x * hexWidth),
                    height * floorHeight, -(hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            vertices[index + 2] = new Vector3f(-(hexWidth / 2) + (offset.x * hexWidth),
                    height * floorHeight, (hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            vertices[index + 3] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (offset.x * hexWidth),
                    height * floorHeight, (hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
        } else {
            vertices[index] = new Vector3f((offset.x * hexWidth), height * floorHeight,
                    -(hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            vertices[index + 1] = new Vector3f((size.x * hexWidth) + (offset.x * hexWidth),
                    height * floorHeight, -(hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            vertices[index + 2] = new Vector3f((offset.x * hexWidth), height * floorHeight,
                    (hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
            vertices[index + 3] = new Vector3f((size.x * hexWidth) + (offset.x * hexWidth),
                    height * floorHeight, (hexRadius / 2) + (offset.y * (hexRadius * 1.5f)));
        }
        index += 4;
        for (int i = 1; i < size.y; i++) {
            float Ypos = (hexRadius * i) + ((hexRadius / 2) * i) + (offset.y * (hexRadius * 1.5f));
            if (((i + offset.y) & 1) == 0) { //even
                vertices[index] = new Vector3f((offset.x * hexWidth), height * floorHeight,
                        (hexRadius / 2) + hexRadius * (i - 1) + ((hexRadius / 2) * (i - 1)) + (offset.y * (hexRadius * 1.5f)));
                vertices[index + 1] = new Vector3f((size.x * hexWidth) + (offset.x * hexWidth),
                        height * floorHeight, (hexRadius / 2) + (hexRadius * (i - 1)) + ((hexRadius / 2) * (i - 1)) + (offset.y * (hexRadius * 1.5f)));
                vertices[index + 2] = new Vector3f(-(hexWidth / 2) + (offset.x * hexWidth),
                        height * floorHeight, -(hexRadius / 2) + Ypos);
                vertices[index + 3] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (offset.x * hexWidth),
                        height * floorHeight, -(hexRadius / 2) + Ypos);
                vertices[index + 4] = new Vector3f(-(hexWidth / 2) + (offset.x * hexWidth),
                        height * floorHeight, (hexRadius / 2) + Ypos);
                vertices[index + 5] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (offset.x * hexWidth),
                        height * floorHeight, (hexRadius / 2) + Ypos);
            } else {
                vertices[index] = new Vector3f(-(hexWidth / 2) + (offset.x * hexWidth), height * floorHeight,
                        (hexRadius / 2) + (hexRadius * (i - 1)) + ((hexRadius / 2) * (i - 1)) + (offset.y * (hexRadius * 1.5f)));
                vertices[index + 1] = new Vector3f((size.x * hexWidth) - (hexWidth / 2) + (offset.x * hexWidth),
                        height * floorHeight, (hexRadius / 2) + (hexRadius * (i - 1)) + ((hexRadius / 2) * (i - 1)) + (offset.y * (hexRadius * 1.5f)));
                vertices[index + 2] = new Vector3f((offset.x * hexWidth), height * floorHeight,
                        -(hexRadius / 2) + Ypos);
                vertices[index + 3] = new Vector3f((size.x * hexWidth) + (offset.x * hexWidth),
                        height * floorHeight, -(hexRadius / 2) + Ypos);
                vertices[index + 4] = new Vector3f((offset.x * hexWidth), height * floorHeight,
                        (hexRadius / 2) + Ypos);
                vertices[index + 5] = new Vector3f((size.x * hexWidth) + (offset.x * hexWidth),
                        height * floorHeight, (hexRadius / 2) + Ypos);
            }
            index += 6;
        }
        return vertices;
    }

    private int[] getQuadIndex(Vector2Int size, int offset) {
        int[] index = new int[(size.y * 2 * 3) + ((size.y - 1) * 2 * 3)];

        index[0] = 2 + offset;
        index[1] = 1 + offset;
        index[2] = 0 + offset;

        index[3] = 2 + offset;
        index[4] = 3 + offset;
        index[5] = 1 + offset;

        int i = 6;
        for (int j = offset; j < (size.y) * 6 - 6; j += 6) {

            index[i] = j + 4;
            index[i + 1] = j + 6;
            index[i + 2] = j + 5;

            index[i + 3] = j + 5;
            index[i + 4] = j + 6;
            index[i + 5] = j + 7;

            index[i + 6] = j + 6;
            index[i + 7] = j + 8;
            index[i + 8] = j + 7;

            index[i + 9] = j + 7;
            index[i + 10] = j + 8;
            index[i + 11] = j + 9;

            i += 12;
        }
//        System.err.println((size.y)*4);
        return index;
    }

    private Vector2f[] getQuadTexCoord(Vector2Int size) {
        Vector2f[] texCoord = new Vector2f[(4 * size.y) + ((size.y - 1) * 2)];

        texCoord[0] = new Vector2f(0f, 0.25f);
        texCoord[1] = new Vector2f(size.x, 0.25f);
        texCoord[2] = new Vector2f(0f, 0.75f);
        texCoord[3] = new Vector2f(size.x, 0.75f);

        int index = 4;
        for (int i = 1; i < size.y; i++) {

            if ((i & 1) == 0) {
                texCoord[index] = new Vector2f(0.5f, 0.002f);
                texCoord[index + 1] = new Vector2f(size.x + 0.5f, 0.002f);
                texCoord[index + 2] = new Vector2f(0f, 0.25f);
                texCoord[index + 3] = new Vector2f(size.x, 0.25f);
                texCoord[index + 4] = new Vector2f(0f, 0.75f);
                texCoord[index + 5] = new Vector2f(size.x, 0.75f);
            } else {
                texCoord[index] = new Vector2f(-0.5f, 0.002f);
                texCoord[index + 1] = new Vector2f(-(size.x + 0.5f), 0.002f);
                texCoord[index + 2] = new Vector2f(-1f, 0.25f);
                texCoord[index + 3] = new Vector2f(-(size.x + 1f), 0.25f);
                texCoord[index + 4] = new Vector2f(-1f, 0.75f);
                texCoord[index + 5] = new Vector2f(-(size.x + 1f), 0.75f);
            }
            index += 6;
        }

        return texCoord;
    }

    private Vector2f[] getTriTexCoord(Vector2Int size) {
        Vector2f[] texCoord = new Vector2f[size.x * 4 + 2];
        int j = 0;
        for (int k = 0; k < 2; k++) {
            j = 0;
            for (int i = 0; i < size.x * 4; i += 4) {
                texCoord[i] = new Vector2f(j + 0.5f, 0f);
                texCoord[i + 1] = new Vector2f(j, 0.25f);
                texCoord[i + 2] = new Vector2f(j, 0.75f);//Y+
                texCoord[i + 3] = new Vector2f(j + 0.5f, 1f);
                j++;
            }
        }
        texCoord[size.x * 4] = new Vector2f(j, 0.25f);
        texCoord[size.x * 4 + 1] = new Vector2f(j, 0.75f);
        return texCoord;
    }

    private int[] getTriIndex(Vector2Int size, int offset) {
        int[] index = new int[(size.x * 2) * 3];

        int j = offset;
        int i = 0;
        while (i < (size.x * 2) * 3 - 6) {
            index[i] = j;
            index[i + 1] = j + 1;
            index[i + 2] = j + 5;

            index[i + 3] = j + 2;
            index[i + 4] = j + 3;
            index[i + 5] = j + 6;
            j += 4;
            i += 6;
        }

        index[i] = j;
        index[i + 1] = j + 1;
        index[i + 2] = size.x * 4 + offset;

        index[i + 3] = j + 2;
        index[i + 4] = j + 3;
        index[i + 5] = size.x * 4 + 1 + offset;

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

    private Vector3f[] getSideVerticesPosition(Vector2Int position, Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[(size.x * 4 + 3) * 2];
        float posX;
        float posY = ((position.y) * hexRadius * 1.5f);
        float posZ;
        for (int i = 0; i < size.x; i++) {
            int r = i * 8;
            if (((position.y) & 1) == 0) { //even
                posX = (position.x * hexWidth) + (i * hexWidth);
                posZ = 0;
            } else {
                posX = (position.x * hexWidth) + (i * hexWidth) + (hexWidth / 2);
                posZ = 0.001f;
            }
            vertices[r] = new Vector3f(posX, height * floorHeight + posZ, hexRadius + posY);
            vertices[r + 1] = new Vector3f(posX - (hexWidth / 2), height * floorHeight + posZ, (hexRadius / 2) + posY);
            vertices[r + 2] = new Vector3f(posX, height * floorHeight + posZ, -hexRadius + posY);
            vertices[r + 3] = new Vector3f(posX - (hexWidth / 2), height * floorHeight + posZ, -(hexRadius / 2) + posY);

            vertices[r + 4] = new Vector3f(posX, posZ, hexRadius + posY);
            vertices[r + 5] = new Vector3f(posX - (hexWidth / 2), posZ, (hexRadius / 2) + posY);
            vertices[r + 6] = new Vector3f(posX, posZ, -hexRadius + posY);
            vertices[r + 7] = new Vector3f(posX - (hexWidth / 2), posZ, -(hexRadius / 2) + posY);
        }
        posX = position.x * hexWidth + size.x * hexWidth + ((((position.y) & 1) == 0 ? -hexWidth / 2 : 0));
        vertices[vertices.length - 6] = new Vector3f(posX, height * floorHeight + (((position.y) & 1) == 0 ? 0 : 0.001f), hexRadius / 2 + posY);
        vertices[vertices.length - 5] = new Vector3f(posX, height * floorHeight + (((position.y) & 1) == 0 ? 0 : 0.001f), -hexRadius / 2 + posY);
        vertices[vertices.length - 4] = new Vector3f(posX, (((position.y) & 1) == 0 ? 0 : 0.001f), hexRadius / 2 + posY);
        vertices[vertices.length - 3] = new Vector3f(posX, (((position.y) & 1) == 0 ? 0 : 0.001f), -hexRadius / 2 + posY);

        vertices[vertices.length - 2] = new Vector3f(posX, height * floorHeight + (((position.y) & 1) == 0 ? 0 : 0.001f), -hexRadius / 2 + posY);
        vertices[vertices.length - 1] = new Vector3f(posX, (((position.y) & 1) == 0 ? 0 : 0.001f), -hexRadius / 2 + posY);

        return vertices;
    }

    private int[] getSideIndex(int sizeX, int offset, Boolean[][] isNeightborsCull) {
        int[] index = new int[((sizeX * 4) + 2) * 2 * 3]; //2 tri * 3 point

        if (isNeightborsCull[0][3]) {
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
            if (isNeightborsCull[i][4]) {
                index[r] = j;
                index[r + 1] = j + 1;
                index[r + 2] = j + 4;

                index[r + 3] = j + 1;
                index[r + 4] = j + 5;
                index[r + 5] = j + 4;
            }

            if (isNeightborsCull[i][2]) {
                index[r + 6] = j + 2;
                index[r + 7] = j + 6;
                index[r + 8] = j + 3;

                index[r + 9] = j + 3;
                index[r + 10] = j + 6;
                index[r + 11] = j + 7;
            }

            if (i < sizeX - 1) {
                if (isNeightborsCull[i][5]) {
                    index[r + 12] = j + 0;
                    index[r + 13] = j + 4;
                    index[r + 14] = j + 9;

                    index[r + 15] = j + 9;
                    index[r + 16] = j + 4;
                    index[r + 17] = j + 13;
                }
                if (isNeightborsCull[i][1]) {
                    index[r + 18] = j + 11;
                    index[r + 19] = j + 6;
                    index[r + 20] = j + 2;

                    index[r + 21] = j + 11;
                    index[r + 22] = j + 15;
                    index[r + 23] = j + 6;
                }
            } else {
                if (isNeightborsCull[i][5]) {
                    index[r + 12] = j + 0;
                    index[r + 13] = j + 4;
                    index[r + 14] = j + 8;

                    index[r + 15] = j + 4;
                    index[r + 16] = j + 10;
                    index[r + 17] = j + 8;
                }

                if (isNeightborsCull[i][1]) {
                    index[r + 18] = j + 6;
                    index[r + 19] = j + 2;
                    index[r + 20] = offset + ((sizeX * 4 + 3) * 2) - 3;

                    index[r + 21] = j + 2;
                    index[r + 22] = offset + ((sizeX * 4 + 3) * 2) - 5;
                    index[r + 23] = offset + ((sizeX * 4 + 3) * 2) - 3;
                }
            }
            j += 8;
        }

        if (isNeightborsCull[sizeX - 1][0]) {
            index[index.length - 6] = offset + ((sizeX * 4 + 3) * 2) - 6;
            index[index.length - 5] = offset + ((sizeX * 4 + 3) * 2) - 4;
            index[index.length - 4] = offset + ((sizeX * 4 + 3) * 2) - 2;

            index[index.length - 3] = offset + ((sizeX * 4 + 3) * 2) - 4;
            index[index.length - 2] = offset + ((sizeX * 4 + 3) * 2) - 1;
            index[index.length - 1] = offset + ((sizeX * 4 + 3) * 2) - 2;
        }
        return index;
    }

    private Vector2f[] getSideVerticestexCoord(Vector2Int size, int height) {
        Vector2f[] texCoord = new Vector2f[(size.x * 4 + 3) * 2];
        float h = height * 0.5f;
        int j = 0;
        for (int i = 0; i < size.x; i++) {
            texCoord[j] = new Vector2f(0, 0.25f);
            texCoord[j + 1] = new Vector2f(0, 0.75f);
            texCoord[j + 2] = new Vector2f(0, 0.75f);
            texCoord[j + 3] = new Vector2f(0, 0.25f);

            texCoord[j + 4] = new Vector2f(h, 0.25f);
            texCoord[j + 5] = new Vector2f(h, 0.75f);
            texCoord[j + 6] = new Vector2f(h, 0.75f);
            texCoord[j + 7] = new Vector2f(h, 0.25f);
            j += 8;
        }
        texCoord[texCoord.length - 6] = new Vector2f(0, 0.75f);
        texCoord[texCoord.length - 5] = new Vector2f(0, 0.25f);
        texCoord[texCoord.length - 4] = new Vector2f(h, 0.75f);
        texCoord[texCoord.length - 3] = new Vector2f(h, 0.25f);

        texCoord[texCoord.length - 2] = new Vector2f(0, 0.25f);
        texCoord[texCoord.length - 1] = new Vector2f(h, 0.25f);

        return texCoord;
    }

    private Mesh setCollisionBound(Mesh meshToUpdate) {
        meshToUpdate.createCollisionData();
        meshToUpdate.updateBound();
        return meshToUpdate;
    }

    private Mesh setAllBuffer(Vector3f[] vertices, Vector2f[] texCoord, int[] index) {
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        return result;
    }
}