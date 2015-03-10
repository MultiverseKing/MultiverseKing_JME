package org.hexgridapi.core.mesh;

import com.jme3.scene.Mesh;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.core.MapData;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.HexCoordinate.Coordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used to generate the needed data used by the MeshManager to generate all
 * mesh contained in the chunk, mesh are split by used texture.
 * 1 texture can have mutilple mesh since it's split by height.
 *
 * @author roah
 */
public final class MeshParameter {

    /**
     * Reference used to generate the parameter.
     */
    private final MapData mapData;
    /**
     * Mesh parameter needed to generate the mesh.
     */
    private ArrayList<Vector2Int> position = new ArrayList<Vector2Int>();
    private ArrayList<Vector2Int> size = new ArrayList<Vector2Int>();
    private ArrayList<Integer> height = new ArrayList<Integer>();
    /**
     * Contain all list of parameter for a specifate element.
     */
    private HashMap<String, ArrayList<Integer>> elementTypeRef = new HashMap<String, ArrayList<Integer>>(2, 1.0f);
    /**
     * Used to define which algorithm to use with meshmanager.
     */
    private boolean onlyGround;
    /**
     * Current element param returned.
     */
    private String inspectedTexture;
    private int inspectedMesh;
    private int groundHeight = 0;
    private Vector2Int inspectedChunk;

    /**
     * Generate all parameter needed to create a grid from defined value.
     *
     * @param mapData Reference used to get the data from.
     */
    public MeshParameter(MapData mapData) {
        this.mapData = mapData;
    }

    private void initialize(HexCoordinate centerPosition, int radius, boolean onlyGround) {
        clear();
        this.onlyGround = onlyGround;
        boolean[][] isVisited = getVisitedList(radius);
        Vector2Int chunkInitTile = HexGrid.getTileFromChunk(new Vector2Int(), inspectedChunk);
        /**
         * x && y == coord local
         */
        int elementID = 0;
        for (int y = 0; y < isVisited.length; y++) {
            for (int x = 0; x < isVisited.length; x++) {
                if (!isVisited[x][y]) {
                    HexTile currentTile;
                    String textValue;
                    boolean currentIsInRange;
                    HexCoordinate currentTileMapCoord; //global coord -> used in map data and range check
                    if (centerPosition != null && radius > 0) {
                        currentTileMapCoord = getNextTileCoord(centerPosition, radius, null, x, y, chunkInitTile);
                        currentTile = mapData.getTile(currentTileMapCoord);
                        currentIsInRange = getIsInRange(radius, null, x, y);
//                    } else if (mode.equals(GhostMode.FULL)) {
//                        currentTile = null;
//                        currentIsInRange = false;
                    } else {
                        currentTileMapCoord = new HexCoordinate(Coordinate.OFFSET, x + chunkInitTile.x, y + chunkInitTile.y);
                        currentTile = mapData.getTile(currentTileMapCoord);
                        currentIsInRange = false;
                    }
                    if (currentTile == null || centerPosition != null && !currentIsInRange) {
                        textValue = mapData.getTextureValue(-2); //Value used to not generate mesh on that position.
                    } else if (centerPosition != null) {
                        textValue = mapData.getTextureValue(-1);  //Value used to use the selection texture.
                    } else {
                        textValue = mapData.getTextureValue(currentTile.getTextureKey());
                    }
                    if (elementTypeRef.isEmpty() || !elementTypeRef.containsKey(textValue)) {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        elementTypeRef.put(textValue, list);
                    }
                    elementTypeRef.get(textValue).add(elementID);

                    Integer tileHeight = currentTile == null ? null : currentTile.getHeight();
                    if (tileHeight != null && tileHeight < groundHeight) {
                        groundHeight = tileHeight;
                    }
//                    if(!mode.equals(GhostMode.FULL)){
//                        tileHeight = currentTile == null ? null : currentTile.getHeight();
//                    } else {
//                        tileHeight = HexSetting.GROUND_HEIGHT;
//                    }
//                    Integer tileHeight = !mode.equals(GhostMode.FULL) ? currentTile == null ? null : currentTile.getHeight() : HexSetting.GROUND_HEIGHT;

                    this.add(new Vector2Int(x, y), new Vector2Int(1, 1), tileHeight);
                    setSizeX(centerPosition, radius, elementID, isVisited, currentTile, currentIsInRange, chunkInitTile);
                    elementID++;
                }
            }
        }
    }

    private void setSizeX(HexCoordinate centerPos, int radius, int inspectedID, boolean[][] isVisited, HexTile currentTile, boolean currentIsInRange, Vector2Int chunkOffset) {
        for (int x = 1; x < isVisited.length - position.get(inspectedID).x; x++) {
            boolean alreadyVisited = isVisited[position.get(inspectedID).x + x][position.get(inspectedID).y];
            HexTile nextTile = mapData.getTile(getNextTileCoord(centerPos, radius, inspectedID, x, 0, chunkOffset));
//            if (mode.equals(GhostMode.FULL)) {
//                nextTile = null;
//            } else {
//                nextTile = mapData.getTile(getNextTileCoord(centerPos, radius, posInList, x, 0));
//            }
            if (!alreadyVisited && currentTile == null && nextTile == null
                    || !alreadyVisited && centerPos == null && currentTile != null && nextTile != null
                    && currentTile.getTextureKey() == nextTile.getTextureKey() && currentTile.getHeight() == nextTile.getHeight()
                    || !alreadyVisited && centerPos != null && currentTile != null && nextTile != null
                    && getIsInRange(radius, inspectedID, x, 0) == currentIsInRange
                    && currentTile.getHeight() == nextTile.getHeight()) {
                this.size.get(inspectedID).x++;
                isVisited[x + position.get(inspectedID).x][position.get(inspectedID).y] = true;
            } else {
                setSizeY(centerPos, radius, inspectedID, isVisited, currentTile, currentIsInRange, chunkOffset);
                return;
            }
        }
        setSizeY(centerPos, radius, inspectedID, isVisited, currentTile, currentIsInRange, chunkOffset);
    }

    private void setSizeY(HexCoordinate centerPos, int radius, int inspectedID, boolean[][] isVisited, HexTile currentTile, boolean currentIsInRange, Vector2Int chunkOffset) {
        for (int y = 1; y < isVisited.length - position.get(inspectedID).y; y++) {
            //We check if the next Y line got the same properties
            for (int x = 0; x < size.get(inspectedID).x; x++) {
                boolean alreadyVisited = isVisited[position.get(inspectedID).x + x][position.get(inspectedID).y + y];
                HexTile nextTile = mapData.getTile(getNextTileCoord(centerPos, radius, inspectedID, x, y, chunkOffset));
//                if (mode.equals(GhostMode.FULL)) {
//                    nextTile = null;
//                } else {
//                    nextTile = mapData.getTile(getNextTileCoord(centerPos, radius, posInList, x, y));
//                }
                if (alreadyVisited || currentTile == null && nextTile != null || currentTile != null && nextTile == null
                        || centerPos == null && currentTile != null && nextTile != null
                        && !mapData.getTextureValue(nextTile.getTextureKey()).equals(mapData.getTextureValue(currentTile.getTextureKey()))
                        || centerPos == null && currentTile != null && nextTile != null && nextTile.getHeight() != currentTile.getHeight()
                        || centerPos != null && currentTile != null && nextTile != null
                        && getIsInRange(radius, inspectedID, x, y) != currentIsInRange
                        || centerPos != null && currentTile != null && nextTile != null
                        && nextTile.getHeight() != currentTile.getHeight()) {
                    //if one tile didn't match the requirement we stop the search
                    return;
                }
            }
            //all tile meet the requirement we increase the size Y
            size.get(inspectedID).y++;
            //we set that line as visited so we don't do any operation later for them
            for (int x = 0; x < size.get(inspectedID).x; x++) {
                isVisited[position.get(inspectedID).x + x][position.get(inspectedID).y + y] = true;
            }
        }
    }

    private boolean getIsInRange(int radius, Integer inspectedID, int x, int y) {
        return new HexCoordinate(Coordinate.OFFSET, radius, radius).hasInRange(
                new HexCoordinate(Coordinate.OFFSET, (inspectedID != null ? position.get(inspectedID).x : 0) + x, (inspectedID != null ? position.get(inspectedID).y : 0) + y), radius);
    }

    private HexCoordinate getNextTileCoord(HexCoordinate centerPos, int radius, Integer inspectedID, int x, int y, Vector2Int chunkOffset) {
        if (centerPos != null) {
            Vector2Int coord = new Vector2Int(x + (inspectedID != null ? position.get(inspectedID).x : 0) - radius, y + (inspectedID != null ? position.get(inspectedID).y : 0) - radius);
            if ((radius & 1) == 0) {
                if ((centerPos.getAsOffset().y & 1) == 0) {
                    return centerPos.add(coord);
                } else {
                    if ((coord.y & 1) == 0) {
                        return centerPos.add(coord);
                    } else {
                        return centerPos.add(coord.x + 1, coord.y);
                    }
                }
            } else {
                if ((centerPos.getAsOffset().y & 1) == 0) {
                    if ((coord.y & 1) == 0) {
                        return centerPos.add(coord);
                    } else {
                        return centerPos.add(coord.x - 1, coord.y);
                    }
                } else {
                    return centerPos.add(coord);
                }
            }
        } else {
            return new HexCoordinate(Coordinate.OFFSET, x + position.get(inspectedID).x + chunkOffset.x, y + position.get(inspectedID).y + chunkOffset.y);
        }
    }

    private boolean[][] getVisitedList(int radius) {
        int chunkSize = radius < 1 ? HexSetting.CHUNK_SIZE : (radius * 2) + 1;
        boolean[][] isVisited = new boolean[chunkSize][chunkSize];
        for (int y = 0; y < chunkSize; y++) {
            for (int x = 0; x < chunkSize; x++) {
                isVisited[x][y] = false;
            }
        }
        return isVisited;
    }

    private void add(Vector2Int position, Vector2Int size, Integer height) {
        this.position.add(position);
        this.size.add(size);
        this.height.add(height == null ? 0 : height);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Generate one or multiple mesh corresponding to the data contained in
     * mapData if not ghost, the count of generated mesh is equals
     * the amount of texture.
     *
     * @param onlyGround generate side face ?
     * @return list of all generated mesh. (1 mesh by texture)
     */
    public HashMap<String, Mesh> getMesh(boolean onlyGround, boolean debugMode, Vector2Int inspectedChunk) {
        this.inspectedChunk = inspectedChunk;
        initialize(null, 0, onlyGround);
        return getMesh(debugMode);
    }

    /**
     * Generate a mesh from a specifiate radius.
     *
     * @param centerPosition
     * @param radius must be greater than 0 (exclusive)
     * @param onlyGround generate side face ?
     * @return
     */
    public HashMap<String, Mesh> getMesh(HexCoordinate centerPosition, int radius, boolean onlyGround, boolean debugMode, Vector2Int inspectedChunk) {
        this.inspectedChunk = inspectedChunk;
        if (radius <= 0) {
            radius = 1;
        }
        initialize(centerPosition, radius, onlyGround);
        return getMesh(debugMode);
    }

    private HashMap<String, Mesh> getMesh(boolean debugMode) {
        HashMap<String, Mesh> mesh = new HashMap<String, Mesh>(elementTypeRef.size());
        for (String value : elementTypeRef.keySet()) {
            if (value.equals("NO_TILE") && debugMode || !value.equals("NO_TILE")) {
                inspectedTexture = value;
                inspectedMesh = -1;
                mesh.put(value, MeshGenerator.getInstance().getMesh(this));
            }
        }
        return mesh;
    }

    /**
     * @return index list of all mesh of the current element.
     */
    public ArrayList<Integer> getElementMeshIndex() {
        return this.elementTypeRef.get(inspectedTexture);
    }

    /**
     * @return a list of all element.
     */
    public Set<String> getAllElementInList() {
        return this.elementTypeRef.keySet();
    }

    /**
     * @return position in chunk of the current element mesh visited.
     */
    public Vector2Int getPositionParam() {
        return position.get(elementTypeRef.get(inspectedTexture).get(inspectedMesh));
    }

    /**
     * @return the size of the current element mesh visited.
     */
    public Vector2Int getSizeParam() {
        return size.get(elementTypeRef.get(inspectedTexture).get(inspectedMesh));
    }

    /**
     * @return height of the current element mesh visited.
     */
    public int getHeightParam() {
        return height.get(elementTypeRef.get(inspectedTexture).get(inspectedMesh));
    }

    /**
     * set to true if the depth isn't needed.
     *
     * @return
     */
    public boolean onlyGround() {
        return onlyGround;
    }

    public int getGroundHeight() {
        return groundHeight;
    }

    /**
     * How many mesh param this element have.
     *
     * @return
     */
    public int getElementMeshCount() {
        return elementTypeRef.get(inspectedTexture).size();
    }

    /**
     * Return true if there is another mesh to generate for the current element.
     *
     * @return
     */
    public boolean hasNext() {
        inspectedMesh++;
        if (elementTypeRef.get(inspectedTexture).size() > inspectedMesh) {
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>

    /**
     * Culling data corresponding to the currently inspected Mesh
     * @return 
     */
    public CullingData getCullingData() {
        return new CullingData();
    }

    /**
     * Internal use.
     */
    public class CullingData {

        //0 == top; 1 == bot; 2 = left; 3 = right;
        //if culling == true do not show the face.
        boolean[][][] culling = new boolean[4][][];

        private CullingData() {
            int inspectedID = elementTypeRef.get(inspectedTexture).get(inspectedMesh);
            boolean isOddStart = (position.get(inspectedID).y & 1) == 0;
            
            Vector2Int chunkInitTile = HexGrid.getTileFromChunk(new Vector2Int(), inspectedChunk);
            HexCoordinate coord = new HexCoordinate(Coordinate.OFFSET,
                    position.get(inspectedID).x + chunkInitTile.x, position.get(inspectedID).y + chunkInitTile.y);
            for (int i = 0; i < 4; i++) {
                int currentSize = (i == 0 || i == 1 ? size.get(inspectedID).x : size.get(inspectedID).y);
                culling[i] = new boolean[currentSize][3];
                for (int j = 0; j < currentSize; j++) {
                    
                    if (i == 0) { // top chunk = -(Z)
                        HexTile[] neightbors = mapData.getNeightbors(coord.add(j, 0));
                        culling[i][j][0] = neightbors[2] == null || neightbors[2].getHeight() < height.get(inspectedID) ? false : true; // top left
                        culling[i][j][1] = neightbors[1] == null || neightbors[1].getHeight() < height.get(inspectedID) ? false : true; // top right
                        culling[i][j][2] = true;
                    } else if (i == 1) { //bot chunk = (Z)
                        HexTile[] neightbors = mapData.getNeightbors(coord.add(j, size.get(inspectedID).y-1));
                        culling[i][j][0] = neightbors[4] == null || neightbors[4].getHeight() < height.get(inspectedID) ? false : true; // bot left
                        culling[i][j][1] = neightbors[5] == null || neightbors[5].getHeight() < height.get(inspectedID) ? false : true; // bot right
                        culling[i][j][2] = true;
                    } else if (i == 2) { // left chunk = -(X)
                        HexTile[] neightbors = mapData.getNeightbors(coord.add(0, j));
                        culling[i][j][0] = neightbors[3] == null || neightbors[3].getHeight() < height.get(inspectedID) ? false : true; // left
                        if (isOddStart && (j & 1) == 0) {
                            culling[i][j][1] = j != 0 && neightbors[2] == null || j != 0 && neightbors[2].getHeight() < height.get(inspectedID) ? false : true; // top left
                            culling[i][j][2] = j != currentSize - 1 && neightbors[4] == null || j != currentSize - 1 && neightbors[4].getHeight() < height.get(inspectedID) ? false : true; // bot left
                        } else if (!isOddStart && (j & 1) != 0) {
                            culling[i][j][1] = neightbors[2] == null || neightbors[2].getHeight() < height.get(inspectedID) ? false : true; // top left
                            culling[i][j][2] = j != currentSize - 1 && neightbors[4] == null || j != currentSize - 1 && neightbors[4].getHeight() < height.get(inspectedID) ? false : true; // bot left
                        } else {
                            culling[i][j][1] = true; // top left ignored
                            culling[i][j][2] = true; // bot left ignored
                        }
                    } else { // right chunk = (X)
                        HexTile[] neightbors = mapData.getNeightbors(coord.add(size.get(inspectedID).x - 1, j));
                        culling[i][j][0] = neightbors[0] == null || neightbors[0].getHeight() < height.get(inspectedID) ? false : true; // right
                        if (!isOddStart && (j & 1) == 0) {
                            culling[i][j][1] = j != 0 && neightbors[1] == null || j != 0 && neightbors[1].getHeight() < height.get(inspectedID) ? false : true; // top right
                            culling[i][j][2] = j != currentSize - 1 && neightbors[5] == null || j != currentSize - 1 && neightbors[5].getHeight() < height.get(inspectedID) ? false : true; // bot right
                        } else if (isOddStart && (j & 1) != 0) {
                            culling[i][j][1] = neightbors[1] == null || neightbors[1].getHeight() < height.get(inspectedID) ? false : true; // top right
                            culling[i][j][2] = j != currentSize - 1 && neightbors[5] == null || j != currentSize - 1 && neightbors[5].getHeight() < height.get(inspectedID) ? false : true; // bot right
                        } else {
                            culling[i][j][1] = true; // top right ignored
                            culling[i][j][2] = true; // bot right ignored
                        }
                    }

                }
            }

        }

        /**
         * return the culling on the desired location.
         *
         * @param pos of the inspected side.
         * @param tilePosition position of the til inside the chunk.
         * @param facePos needed face of the selected tile.
         * @return true if the face have to be culled.
         */
        public boolean getCulling(Position pos, int tilePosition, Position facePos) {
            int index;
            if (pos.equals(Position.TOP)) {
                if (facePos.equals(Position.TOP_LEFT)) {
                    index = 0;
                } else if (facePos.equals(Position.TOP_RIGHT)) {
                    index = 1;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else if (pos.equals(Position.BOTTOM)) {
                if (facePos.equals(Position.BOT_LEFT)) {
                    index = 0;
                } else if (facePos.equals(Position.BOT_RIGHT)) {
                    index = 1;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else if (pos.equals(Position.LEFT)) {
                if (facePos.equals(Position.LEFT)) {
                    index = 0;
                } else if (facePos.equals(Position.TOP_LEFT)) {
                    index = 1;
                } else if (facePos.equals(Position.BOT_LEFT)) {
                    index = 2;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else if (pos.equals(Position.RIGHT)) {
                if (facePos.equals(Position.RIGHT)) {
                    index = 0;
                } else if (facePos.equals(Position.TOP_RIGHT)) {
                    index = 1;
                } else if (facePos.equals(Position.BOT_RIGHT)) {
                    index = 2;
                } else {
                    throw new UnsupportedOperationException(facePos + " is not allowed in the current context.");
                }
            } else {
                throw new UnsupportedOperationException(pos + " is not allowed in the current context.");
            }
            return culling[pos.ordinal()][tilePosition][index];
        }
    }

    public enum Position {

        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOT_LEFT,
        BOT_RIGHT;
    }

    private void clear() {
        elementTypeRef.clear();
        height.clear();
        position.clear();
        size.clear();
        groundHeight = 0;
    }
}
