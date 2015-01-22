package org.hexgridapi.base;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import org.hexgridapi.base.MeshParameter.CullingData;
import org.hexgridapi.utility.Vector2Int;

/**
 * Maths used to generate the room grid.
 *
 * @todo improve method to not have to use System.ArrayCopy
 * @author roah
 */
public final class MeshManager {

    private MeshManager() {
    }

    private static class Holder {

        private static final MeshManager instance = new MeshManager();
    }

    public static MeshManager getInstance() {
        return Holder.instance;
    }
    private static final float hexRadius = HexSetting.HEX_RADIUS;
    private static float hexWidth = HexSetting.HEX_WIDTH;
    private static float floorHeight = HexSetting.FLOOR_OFFSET;

    public Mesh getSingleMesh() {
        Vector2Int offset = new Vector2Int();
        Vector2Int size = new Vector2Int(1, 1);

        Vector3f[] triVert = getTriVerticesPosition(offset, size, 0);
        Vector2f[] triText = getTriTexCoord(size);
        int[] triIndex = getTriIndex(size, 0);

        Vector3f[] quadVert = getQuadVerticesPosition(offset, size, 0);
        Vector2f[] quadText = getQuadTexCoord(offset, size);
        int[] quadIndex = getQuadIndex(size, 0);


        Vector3f[] mergedVertices = new Vector3f[triVert.length + quadVert.length];
        Vector2f[] mergedtextCoord = new Vector2f[triText.length + quadText.length];
        int[] mergedIndex = new int[triIndex.length + quadIndex.length];

        System.arraycopy(triVert, 0, mergedVertices, 0, triVert.length);
        System.arraycopy(quadVert, 0, mergedVertices, triVert.length, quadVert.length);

        System.arraycopy(triText, 0, mergedtextCoord, 0, triText.length);
        System.arraycopy(quadText, 0, mergedtextCoord, triText.length, quadText.length);

        System.arraycopy(triIndex, 0, mergedIndex, 0, triIndex.length);
        System.arraycopy(quadIndex, 0, mergedIndex, triIndex.length, quadIndex.length);

        return setCollisionBound(setAllBuffer(mergedVertices, mergedtextCoord, mergedIndex));
    }

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
            Vector2f[] groundQuadTex = getQuadTexCoord(meshParam.getPositionParam(), meshParam.getSizeParam());
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
//                Vector3f[] sideVert = getSideVerticesPosition(meshParam.getPositionParam(), meshParam.getSizeParam(), meshParam.getHeightParam());
                Vector3f[] sideVert = getSideVerticesPositionUpdate(groundVert, meshParam.getHeightParam());
                Vector3f[] vertCombi = new Vector3f[sideVert.length + groundVert.length];
                System.arraycopy(groundVert, 0, vertCombi, 0, groundVert.length);
                System.arraycopy(sideVert, 0, vertCombi, groundVert.length, sideVert.length);
                vertices.add(vertCombi);

                Vector2f[] sideTex = getSideVerticestexCoord(meshParam.getSizeParam(), meshParam.getHeightParam());
                Vector2f[] texCombi = new Vector2f[sideTex.length + groundTex.length];
                System.arraycopy(groundTex, 0, texCombi, 0, groundTex.length);
                System.arraycopy(sideTex, 0, texCombi, groundTex.length, sideTex.length);
                texCoord.add(texCombi);

                int[] sideIndice = getSideIndexUpdate((meshParam.getPositionParam().y & 1) == 0, meshParam.getSizeParam(), mergedVerticeCount - groundVert.length, groundVert.length, meshParam.getCullingData());
//                int[] sideIndice = getSideIndex(meshParam.getSizeParam().x, mergedVerticeCount, meshParam.getCulling());
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

    private static Vector3f[] getTriVerticesPosition(Vector2Int position, Vector2Int size, int height) {
        Vector3f[] vertices = new Vector3f[size.x * 4 + 2];
        int j = 0;
        float currentHexPos;
        float startTriOffsetA;
        float startTriOffsetB;
        float endTriOffsetA = 0;
        float endTriOffsetB = -hexWidth / 2;

        if ((position.y & 1) == 0) {
            startTriOffsetA = -hexWidth / 2;
            startTriOffsetB = 0;
        } else {
            startTriOffsetA = 0;
            startTriOffsetB = hexWidth / 2;
        }
        if (((position.y + size.y - 1) & 1) != 0) {
            endTriOffsetB = 0;
            endTriOffsetA = +hexWidth / 2;
        }

        for (int i = 0; i < size.x * 4; i += 4) {
            currentHexPos = j * hexWidth + (position.x * hexWidth);
            vertices[i] = new Vector3f(currentHexPos + startTriOffsetA, height * floorHeight, -(hexRadius / 2) + (position.y * hexRadius * 1.5f));
            vertices[i + 1] = new Vector3f(currentHexPos + startTriOffsetB, height * floorHeight, -hexRadius + (position.y * hexRadius * 1.5f));
            vertices[i + 2] = new Vector3f(currentHexPos + endTriOffsetA, height * floorHeight,
                    (hexRadius * size.y) + ((size.y - 1) * (hexRadius / 2)) + (position.y * hexRadius * 1.5f));
            vertices[i + 3] = new Vector3f(currentHexPos + endTriOffsetB, height * floorHeight,
                    ((hexRadius * size.y) - hexRadius / 2) + ((size.y - 1) * (hexRadius / 2)) + (position.y * hexRadius * 1.5f));
            j++;
        }

        currentHexPos = j * hexWidth + position.x * hexWidth;
        vertices[size.x * 4] = new Vector3f(currentHexPos, height * floorHeight,
                -hexRadius / 2 + (position.y * hexRadius * 1.5f));
        vertices[size.x * 4 + 1] = new Vector3f(currentHexPos, height * floorHeight,
                ((hexRadius * size.y) - hexRadius / 2) + ((size.y - 1) * (hexRadius / 2)) + (position.y * (hexRadius * 1.5f)));

        if (((size.y - 1 + position.y) & 1) == 0) {
            vertices[size.x * 4 + 1].x -= hexWidth / 2;
        }
        if ((position.y & 1) == 0) {
            vertices[size.x * 4].x -= hexWidth / 2;
        }
        return vertices;
    }

    private static Vector3f[] getQuadVerticesPosition(Vector2Int position, Vector2Int size, int height) {
        //2 Quad by Row so 6 vertice by row, first row contain only 1 quad so 4 vertice.
        Vector3f[] vertices = new Vector3f[(4 * size.y) + ((size.y - 1) * 2)];
        int index = 0;

        //generate the first quad
        vertices[index] = new Vector3f((position.x * hexWidth), height * floorHeight,
                -(hexRadius / 2) + (position.y * hexRadius * 1.5f));
        vertices[index + 1] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth),
                height * floorHeight, -(hexRadius / 2) + (position.y * hexRadius * 1.5f));
        vertices[index + 2] = new Vector3f((position.x * hexWidth), height * floorHeight,
                (hexRadius / 2) + (position.y * hexRadius * 1.5f));
        vertices[index + 3] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth),
                height * floorHeight, (hexRadius / 2) + (position.y * hexRadius * 1.5f));
        if (((position.y) & 1) == 0) { //even
            vertices[index].x -= hexWidth / 2;
            vertices[index + 1].x -= hexWidth / 2;
            vertices[index + 2].x -= hexWidth / 2;
            vertices[index + 3].x -= hexWidth / 2;
        }
        index += 4;
        for (int i = 1; i < size.y; i++) {
            float Ypos = (hexRadius * 1.5f * i) + (position.y * hexRadius * 1.5f);
            vertices[index] = new Vector3f((position.x * hexWidth) - (hexWidth / 2), height * floorHeight,
                    Ypos - hexRadius);
            vertices[index + 1] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth) - (hexWidth / 2),
                    height * floorHeight, Ypos - hexRadius);
            vertices[index + 2] = new Vector3f((position.x * hexWidth),
                    height * floorHeight, -(hexRadius / 2) + Ypos);
            vertices[index + 3] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth),
                    height * floorHeight, -(hexRadius / 2) + Ypos);
            vertices[index + 4] = new Vector3f((position.x * hexWidth),
                    height * floorHeight, (hexRadius / 2) + Ypos);
            vertices[index + 5] = new Vector3f((size.x * hexWidth) + (position.x * hexWidth),
                    height * floorHeight, (hexRadius / 2) + Ypos);
            if (((i + position.y) & 1) == 0) { //even
//                System.err.println("even Quad");
                vertices[index].x += (hexWidth / 2);
                vertices[index + 1].x += (hexWidth / 2);
                vertices[index + 2].x -= (hexWidth / 2);
                vertices[index + 3].x -= (hexWidth / 2);
                vertices[index + 4].x -= (hexWidth / 2);
                vertices[index + 5].x -= (hexWidth / 2);
            } else {
//                vertices[index].x -= (hexWidth / 2);
//                vertices[index + 1].x -= (hexWidth / 2);
//                System.err.println("odd Quad");
            }
            index += 6;
        }
        return vertices;
    }

    private static int[] getQuadIndex(Vector2Int size, int offset) {
        int[] index = new int[(size.y * 2 * 3) + ((size.y - 1) * 2 * 3)];

        index[0] = 2 + offset;
        index[1] = 1 + offset;
        index[2] = 0 + offset;

        index[3] = 2 + offset;
        index[4] = 3 + offset;
        index[5] = 1 + offset;

        int i = 6;
        for (int j = offset; j < size.y * 6 - 6 + offset; j += 6) {

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
        return index;
    }

    private static Vector2f[] getQuadTexCoord(Vector2Int position, Vector2Int size) {
        Vector2f[] texCoord = new Vector2f[(4 * size.y) + ((size.y - 1) * 2)];

        texCoord[0] = new Vector2f(0f, 0.25f);
        texCoord[1] = new Vector2f(size.x, 0.25f);
        texCoord[2] = new Vector2f(0f, 0.75f);
        texCoord[3] = new Vector2f(size.x, 0.75f);

        int index = 4;
        for (int i = 1 + position.y; i < size.y + position.y; i++) {

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

    private static Vector2f[] getTriTexCoord(Vector2Int size) {
        Vector2f[] texCoord = new Vector2f[size.x * 4 + 2];
        int j = 0;
        for (int k = 0; k < 2; k++) {
            j = 0;
            for (int i = 0; i < size.x * 4; i += 4) {
                texCoord[i] = new Vector2f(j, 0.25f);
                texCoord[i + 1] = new Vector2f(j + 0.5f, 0f);
                texCoord[i + 2] = new Vector2f(j + 0.5f, 0.015f);
                texCoord[i + 3] = new Vector2f(j, 0.265f);//Y+
                j++;
            }
        }
        texCoord[size.x * 4] = new Vector2f(j, 0.25f);
        texCoord[size.x * 4 + 1] = new Vector2f(j, 0.265f);
        return texCoord;
    }

    private static int[] getTriIndex(Vector2Int size, int offset) {
        int[] index = new int[size.x * 6];
        int i = 0;
        for (int x = 0; x < size.x - 1; x++) {
            i = x * 6;
            index[i] = offset + x * 4;
            index[i + 1] = offset + (x + 1) * 4;
            index[i + 2] = offset + x * 4 + 1;

            index[i + 3] = offset + x * 4 + 2;
            index[i + 4] = offset + (x + 1) * 4 + 3;
            index[i + 5] = offset + x * 4 + 1 + 2;
        }
        i = (size.x - 1) * 6;
        index[i] = offset + (size.x - 1) * 4;
        index[i + 1] = offset + size.x * 4;
        index[i + 2] = offset + (size.x - 1) * 4 + 1;

        index[i + 3] = offset + (size.x - 1) * 4 + 2;
        index[i + 4] = offset + size.x * 4 + 1;
        index[i + 5] = offset + (size.x - 1) * 4 + 1 + 2;

//      [XY] == Tile position
//      ++++++++++++++++++++++++++++++++++++++ = Y-
//      +++++[01]++++[05]++++[09]++++[13]+++++
//      +[00]++++[04]++++[08]++++[12]++++[16]+
//      +++++[XY]++++[XY]++++[XY]++++[XY]+++++
//      +[02]++++[06]++++[10]++++[14]++++[17]+
//      +++++[03]++++[07]++++[11]++++[15]+++++
//      ++++++++++++++++++++++++++++++++++++++ = Y+

        return index;
    }

    private Vector3f[] getSideVerticesPositionUpdate(Vector3f[] groundVert, byte height) {
        Vector3f[] sideVert = new Vector3f[groundVert.length];
        for (int i = 0; i < groundVert.length; i++) {
            sideVert[i] = new Vector3f(groundVert[i].x, groundVert[i].y - height * floorHeight, groundVert[i].z);
        }
        return sideVert;
    }

    private static Vector3f[] getSideVerticesPosition(Vector2Int position, Vector2Int size, int height) {
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

    private static int[] getSideIndexUpdate(boolean isOddStart, Vector2Int size, int arrayOffset, int groundOffset, CullingData culling) {
        if (arrayOffset < 0) {
            arrayOffset = 0;
        }
        int Ytri = ((size.y - 1 + size.y) * 2) * 2;
        int Xtri = (size.x * 4) * 2;
        int[] index = new int[(Xtri + Ytri) * 3];
        int i = 0;
        /**
         * generate top/bot face.
         */
        for (int x = 0; x < size.x; x++) {
            int vert = x * 4 + arrayOffset;
            if (!culling.getCulling(MeshParameter.Position.TOP, x, MeshParameter.Position.TOP_LEFT)) {
                index[i] = vert;
                index[i + 1] = vert + 1;
                index[i + 2] = vert + groundOffset;

                index[i + 3] = vert + groundOffset;
                index[i + 4] = vert + 1;
                index[i + 5] = vert + 1 + groundOffset;
            }
            if (!culling.getCulling(MeshParameter.Position.TOP, x, MeshParameter.Position.TOP_RIGHT)) {
                index[i + 6] = (x + 1) * 4 + arrayOffset;
                index[i + 7] = (x + 1) * 4 + arrayOffset + groundOffset;
                index[i + 8] = vert + 1;

                index[i + 9] = vert + 1;
                index[i + 10] = x != size.x - 1 ? (x + 1) * 4 + arrayOffset + groundOffset : size.x * 4 + arrayOffset + groundOffset;
                index[i + 11] = vert + 1 + groundOffset;
            }
            if (!culling.getCulling(MeshParameter.Position.BOTTOM, x, MeshParameter.Position.BOT_LEFT)) {
                index[i + 12] = vert + 2;
                index[i + 13] = vert + 1 + 2;
                index[i + 14] = vert + groundOffset + 2;

                index[i + 15] = vert + groundOffset + 2;
                index[i + 16] = vert + 1 + 2;
                index[i + 17] = vert + 1 + groundOffset + 2;
            }
            if (!culling.getCulling(MeshParameter.Position.BOTTOM, x, MeshParameter.Position.BOT_RIGHT)) {
                index[i + 18] = x != size.x - 1 ? (x + 1) * 4 + arrayOffset + groundOffset + 2 + 1 : size.x * 4 + arrayOffset + 1 + groundOffset;
                index[i + 19] = x != size.x - 1 ? (x + 1) * 4 + arrayOffset + 2 + 1 : size.x * 4 + arrayOffset + 1;
                index[i + 20] = vert + 2;

                index[i + 21] = vert + 2;
                index[i + 22] = vert + 2 + groundOffset;
                index[i + 23] = x != size.x - 1 ? (x + 1) * 4 + arrayOffset + groundOffset + 2 + 1 : size.x * 4 + arrayOffset + groundOffset + 1;
            }
            i += 24;
        }
        /**
         * generate left/right face.
         */
        int offsetB = size.x * 4 + 2 + arrayOffset;
        if (!culling.getCulling(MeshParameter.Position.LEFT, 0, MeshParameter.Position.LEFT)) {
            index[i] = offsetB;
            index[i + 1] = offsetB + groundOffset;
            index[i + 2] = offsetB + 2;

            index[i + 3] = offsetB + 2;
            index[i + 4] = offsetB + groundOffset;
            index[i + 5] = offsetB + groundOffset + 2;
        }

        if (!culling.getCulling(MeshParameter.Position.RIGHT, 0, MeshParameter.Position.RIGHT)) {
            index[i + 6] = offsetB + 1;
            index[i + 7] = offsetB + 3;
            index[i + 8] = offsetB + groundOffset + 1;

            index[i + 9] = offsetB + 3;
            index[i + 10] = offsetB + groundOffset + 3;
            index[i + 11] = offsetB + groundOffset + 1;
        }
        offsetB += 4;
        i += 12;
        for (int y = 1; y < size.y; y++) {
            int vert = (y - 1) * 6;
            if (!culling.getCulling(MeshParameter.Position.LEFT, y, MeshParameter.Position.LEFT)) {
                index[i] = offsetB + vert + 4;
                index[i + 1] = offsetB + vert + 2;
                index[i + 2] = offsetB + groundOffset + vert + 2;
//
                index[i + 3] = offsetB + vert + 4;
                index[i + 4] = offsetB + groundOffset + vert + 2;
                index[i + 5] = offsetB + groundOffset + vert + 4;
            }

            if (!culling.getCulling(MeshParameter.Position.LEFT, y, MeshParameter.Position.TOP_LEFT)
                    || !culling.getCulling(MeshParameter.Position.LEFT, y - 1, MeshParameter.Position.BOT_LEFT)) {
                index[i + 6] = offsetB + vert;
                index[i + 7] = offsetB + groundOffset + vert;
                index[i + 8] = offsetB + vert + 2;

                index[i + 9] = offsetB + vert + 2;
                index[i + 10] = offsetB + groundOffset + vert;
                index[i + 11] = offsetB + groundOffset + vert + 2;
            }

            if (!culling.getCulling(MeshParameter.Position.RIGHT, y, MeshParameter.Position.RIGHT)) {
                index[i + 12] = offsetB + vert + 3;
                index[i + 13] = offsetB + vert + 5;
                index[i + 14] = offsetB + groundOffset + vert + 3;
//
                index[i + 15] = offsetB + vert + 5;
                index[i + 16] = offsetB + groundOffset + vert + 5;
                index[i + 17] = offsetB + groundOffset + vert + 3;
            }

            if (!culling.getCulling(MeshParameter.Position.RIGHT, y, MeshParameter.Position.TOP_RIGHT)
                    || !culling.getCulling(MeshParameter.Position.RIGHT, y - 1, MeshParameter.Position.BOT_RIGHT)) {
                index[i + 18] = offsetB + vert + 1;
                index[i + 19] = offsetB + vert + 3;
                index[i + 20] = offsetB + groundOffset + vert + 1;

                index[i + 21] = offsetB + vert + 3;
                index[i + 22] = offsetB + groundOffset + vert + 3;
                index[i + 23] = offsetB + groundOffset + vert + 1;
            }
            i += 24;
        }

//         ++[00]++++++++++[01]++++ 
//         ++++[02]++++++++++[02]++ 
//         ++++++++++++++++++++++++
//         ++++[04]++++++++++[05]++

        return index;
    }

    private static int[] getSideIndex(int sizeX, int offset, Boolean[][] isNeightborsCull) {
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

    private static Vector2f[] getSideVerticestexCoord(Vector2Int size, int height) {
        Vector2f[] texCoord = new Vector2f[(size.x * 4 + 3) * 2];
        float h = height * 0.5f;
        int j = 0;
        for (int i = 0; i < size.x; i++) {
            texCoord[j] = new Vector2f(0, 0.75f);
            texCoord[j + 1] = new Vector2f(0, 1f);
            texCoord[j + 2] = new Vector2f(0, 1f);
            texCoord[j + 3] = new Vector2f(0, 0.75f);

            texCoord[j + 4] = new Vector2f(h, 0.75f);
            texCoord[j + 5] = new Vector2f(h, 1f);
            texCoord[j + 6] = new Vector2f(h, 1f);
            texCoord[j + 7] = new Vector2f(h, 0.75f);
            j += 8;
        }
        texCoord[texCoord.length - 6] = new Vector2f(0, 1f);
        texCoord[texCoord.length - 5] = new Vector2f(0, 0.75f);
        texCoord[texCoord.length - 4] = new Vector2f(h, 1f);
        texCoord[texCoord.length - 3] = new Vector2f(h, 0.75f);

        texCoord[texCoord.length - 2] = new Vector2f(0, 0.75f);
        texCoord[texCoord.length - 1] = new Vector2f(h, 0.75f);

        return texCoord;
    }

    private static Mesh setCollisionBound(Mesh meshToUpdate) {
        meshToUpdate.createCollisionData();
        meshToUpdate.updateBound();
        return meshToUpdate;
    }

    private static Mesh setAllBuffer(Vector3f[] vertices, Vector2f[] texCoord, int[] index) {
        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        return result;
    }
}