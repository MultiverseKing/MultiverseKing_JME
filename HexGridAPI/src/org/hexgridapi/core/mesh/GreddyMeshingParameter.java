package org.hexgridapi.core.mesh;

import com.jme3.scene.Mesh;
import java.util.HashMap;
import java.util.Iterator;
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
public final class GreddyMeshingParameter {

    private final MapData mapData;
    /**
     * Contain all list of parameter for a specifate element.
     */
    private HashMap<String, GreddyMeshingElementMeshData> elementMeshData = new HashMap<String, GreddyMeshingElementMeshData>();
    private int groundHeight = 0;
    private Vector2Int inspectedChunk;
    private boolean onlyGround;
    private Shape shapeType; //Used for culling.
    private String inspectedTexture;
    private final boolean useArrayTexture;
    private Iterator<String> meshIterator;

    public GreddyMeshingParameter(MapData mapData, boolean useArrayTexture) {
        this.mapData = mapData;
        this.useArrayTexture = useArrayTexture;
    }

    private void initialize(HexCoordinate centerPosition, int radius, boolean onlyGround, Shape shapeType) {
        clear();
        this.onlyGround = onlyGround;
        this.shapeType = shapeType;
        boolean[][] isVisited = getVisitedList(radius);
        Vector2Int chunkInitTile = HexGrid.getInitialChunkTile(inspectedChunk).toOffset();
        /**
         * x && y == coord local
         */
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
                    } else {
                        currentTileMapCoord = new HexCoordinate(Coordinate.OFFSET, x + chunkInitTile.x, y + chunkInitTile.y);
                        currentTile = mapData.getTile(currentTileMapCoord);
                        currentIsInRange = false;
                    }
                    if (currentTile == null || centerPosition != null && !currentIsInRange) {
                        textValue = mapData.getTextureValue(-1); //Value used to not generate mesh on that position.
                    } else {
                        textValue = mapData.getTextureValue(currentTile.getTextureKey());
                    }
                    Integer tileHeight = currentTile == null ? null : currentTile.getHeight();

                    if (elementMeshData.isEmpty() || !elementMeshData.containsKey(textValue)) {
                        elementMeshData.put(textValue, new GreddyMeshingElementMeshData(new Vector2Int(x, y), tileHeight));
                    } else {
                        elementMeshData.get(textValue).add(new Vector2Int(x, y), tileHeight);
                    }

                    if (tileHeight != null && tileHeight < groundHeight + HexSetting.CHUNK_DEPTH) {
                        groundHeight = tileHeight;
                    }

                    setSize(centerPosition, radius, isVisited, elementMeshData.get(textValue),
                            currentTile, currentIsInRange, chunkInitTile);
                }
            }
        }
    }

    private void setSize(HexCoordinate centerPos, int radius, boolean[][] isVisited, GreddyMeshingElementMeshData currentGreddyData,
            HexTile currentTile, boolean currentIsInRange, Vector2Int chunkOffset) {

        // Define the size on X.
        for (int x = 1; x < isVisited.length - currentGreddyData.getLastAddedPosition().x; x++) {
            boolean alreadyVisited = isVisited[currentGreddyData.getLastAddedPosition().x + x][currentGreddyData.getLastAddedPosition().y];
            HexTile nextTile = mapData.getTile(getNextTileCoord(centerPos, radius, currentGreddyData.getLastAddedPosition(), x, 0, chunkOffset));
            if (!alreadyVisited && currentTile == null && nextTile == null
                    || !alreadyVisited && centerPos == null && currentTile != null && nextTile != null
                    && currentTile.getTextureKey() == nextTile.getTextureKey() && currentTile.getHeight() == nextTile.getHeight()
                    || !alreadyVisited && centerPos != null && currentTile != null && nextTile != null
                    && getIsInRange(radius, currentGreddyData.getLastAddedPosition(), x, 0) == currentIsInRange
                    && currentTile.getHeight() == nextTile.getHeight()) {
                currentGreddyData.expandSizeX();
                isVisited[currentGreddyData.getLastAddedPosition().x + x][currentGreddyData.getLastAddedPosition().y] = true;
            } else {
                break;
            }
        }
        // Define the size on Y.
        for (int y = 1; y < isVisited.length - currentGreddyData.getLastAddedPosition().y; y++) {
            //We check if the next Y line got the same properties
            for (int x = 0; x < currentGreddyData.getLastAddedSize().x; x++) {
                boolean alreadyVisited = isVisited[currentGreddyData.getLastAddedPosition().x + x][currentGreddyData.getLastAddedPosition().y + y];
                HexTile nextTile = mapData.getTile(getNextTileCoord(centerPos, radius, currentGreddyData.getLastAddedPosition(), x, y, chunkOffset));
                if (alreadyVisited || currentTile == null && nextTile != null || currentTile != null && nextTile == null
                        || centerPos == null && currentTile != null && nextTile != null
                        && !mapData.getTextureValue(nextTile.getTextureKey()).equals(mapData.getTextureValue(currentTile.getTextureKey()))
                        || centerPos == null && currentTile != null && nextTile != null && nextTile.getHeight() != currentTile.getHeight()
                        || centerPos != null && currentTile != null && nextTile != null
                        && getIsInRange(radius, currentGreddyData.getLastAddedPosition(), x, y) != currentIsInRange
                        || centerPos != null && currentTile != null && nextTile != null
                        && nextTile.getHeight() != currentTile.getHeight()) {
                    //if one tile didn't match the requirement we stop the search
                    return;
                }
            }
            //all tile meet the requirement we increase the size Y
            currentGreddyData.expandSizeY();
            //we set that line as visited so we don't do any operation later for them
            Vector2Int lastAddedPosition = currentGreddyData.getLastAddedPosition();
            for (int x = 0; x < currentGreddyData.getLastAddedSize().x; x++) {
                isVisited[lastAddedPosition.x + x][lastAddedPosition.y + y] = true;
            }
        }
    }

    private boolean getIsInRange(int radius, Vector2Int position, int x, int y) {
        return new HexCoordinate(Coordinate.OFFSET, radius, radius)
                .hasInRange(new HexCoordinate(Coordinate.OFFSET,
                (position != null ? position.x : 0) + x,
                (position != null ? position.y : 0) + y), radius);
    }

    private HexCoordinate getNextTileCoord(HexCoordinate centerPos, int radius, Vector2Int position, int x, int y, Vector2Int chunkOffset) {
        if (centerPos != null) {
            Vector2Int coord = new Vector2Int(
                    x + (position != null ? position.x : 0) - radius,
                    y + (position != null ? position.y : 0) - radius);
            if ((radius & 1) == 0) {
                if ((centerPos.toOffset().y & 1) == 0) {
                    return centerPos.add(coord);
                } else {
                    if ((coord.y & 1) == 0) {
                        return centerPos.add(coord);
                    } else {
                        return centerPos.add(coord.x + 1, coord.y);
                    }
                }
            } else {
                if ((centerPos.toOffset().y & 1) == 0) {
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
            return new HexCoordinate(Coordinate.OFFSET,
                    x + position.x + chunkOffset.x,
                    y + position.y + chunkOffset.y);
        }
    }

    private boolean[][] getVisitedList(int radius) {
        int chunkSize = radius < 1 ? HexSetting.CHUNK_SIZE : (radius * 2) + 1;
        boolean[][] isVisited = new boolean[chunkSize][chunkSize];
        return isVisited;
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
    public HashMap<String, Mesh> getMesh(boolean onlyGround, Vector2Int inspectedChunk) {
        this.inspectedChunk = inspectedChunk;
        initialize(null, 0, onlyGround, Shape.SQUARE);
        return getMesh();
    }

    /**
     * Generate a mesh from a specifiate radius.
     *
     * @param centerPosition
     * @param radius must be greater than 0 (exclusive)
     * @param onlyGround generate side face ?
     * @return
     */
    public HashMap<String, Mesh> getMesh(HexCoordinate centerPosition, int radius, boolean onlyGround, Vector2Int inspectedChunk) {
        this.inspectedChunk = inspectedChunk;
        if (radius <= 0) {
            radius = 1;
        }
        initialize(centerPosition, radius, onlyGround, Shape.CIRCLE);
        return getMesh();
    }

    private HashMap<String, Mesh> getMesh() {
        if (!(mapData.getMode().equals(MapData.GhostMode.GHOST)
                || mapData.getMode().equals(MapData.GhostMode.GHOST_PROCEDURAL))) {
            elementMeshData.remove(mapData.getTextureValue(-1));
        }
        HashMap<String, Mesh> mesh = new HashMap<String, Mesh>(elementMeshData.size());
        meshIterator = elementMeshData.keySet().iterator();

        if (useArrayTexture) {
            String value = meshIterator.next();
            inspectedTexture = value; //Send this to the generator
            mesh.put("mesh", MeshGenerator.getInstance().getMesh(this));
        } else {
            while (meshIterator.hasNext()) {
                String value = meshIterator.next();
                inspectedTexture = value; //Send this to the generator
                mesh.put(value, MeshGenerator.getInstance().getMesh(this));
            }
        }
        return mesh;
    }

    /**
     * @return position in chunk of the current element mesh visited.
     */
    public Vector2Int getCurrentPositionParam() {
        return elementMeshData.get(inspectedTexture).getPosition();
    }

    /**
     * @return the size of the current element mesh visited.
     */
    public Vector2Int getCurrentSizeParam() {
        return elementMeshData.get(inspectedTexture).getSize();
    }

    /**
     * @return height of the current element mesh visited.
     */
    public int getCurrentHeightParam() {
        return elementMeshData.get(inspectedTexture).getHeight();
    }

    /**
     * @return height of the current element mesh visited.
     */
    public int getCurrentTextureIDParam() {
        if (inspectedTexture.equals("NO_TILES")
                || inspectedTexture.equals("EMPTY_TEXTURE_KEY")) {
            return 0;
        }
        return mapData.getTextureKey(inspectedTexture);
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
        return groundHeight + HexSetting.CHUNK_DEPTH;
    }

    /**
     * How many mesh param this element have.
     *
     * @return
     */
    public int getElementMeshCount() {
        return elementMeshData.get(inspectedTexture).size();
    }

    /**
     * Return true if there is another mesh to generate for the current element.
     *
     * @return
     */
    public boolean hasNext() {
        boolean hasNext = elementMeshData.get(inspectedTexture).hasNext();
        if (useArrayTexture) {
            if (!hasNext && meshIterator.hasNext()) {
                inspectedTexture = meshIterator.next();
                return elementMeshData.get(inspectedTexture).hasNext();
            }
        }
        return hasNext;
    }
    // </editor-fold>

    /**
     * Culling data corresponding to the currently inspected Mesh
     *
     * @return
     */
    public CullingData getCullingData() {
        return new CullingData();
    }

    /**
     * Internal use.
     *
     * @todo Since HexGridAPI_v.1.1.9.preAlpha the culling on chunk edge is
     * always set to false, this only serve to avoid calculation when editing
     * the grid once generated, there is no use of it if the grid isn't mean
     * to be edited once generated. An improvement is needed to let the user
     * chose if it will edit or not the grid once generated so we know if this
     * have to be enabled or not.
     */
    public class CullingData {

        //0 == top; 1 == bot; 2 = left; 3 = right;
        //if culling == true do not show the face.
        boolean[][][] culling = new boolean[4][][];

        private CullingData() {
            GreddyMeshingElementMeshData inspectedMesh = elementMeshData.get(inspectedTexture);
            boolean isOddStart = (inspectedMesh.getPosition().y & 1) == 0;

            Vector2Int chunkInitTile = HexGrid.getInitialChunkTile(inspectedChunk).toOffset();
            HexCoordinate coord = new HexCoordinate(Coordinate.OFFSET,
                    inspectedMesh.getPosition().x + chunkInitTile.x, inspectedMesh.getPosition().y + chunkInitTile.y);
            for (int i = 0; i < 4; i++) {
                int currentSize = (i == 0 || i == 1 ? inspectedMesh.getSize().x : inspectedMesh.getSize().y);
                culling[i] = new boolean[currentSize][3];
                for (int j = 0; j < currentSize; j++) {

                    if (i == 0) { // top chunk = -(Z)
                        if (shapeType.equals(Shape.SQUARE) && inspectedMesh.getPosition().y == 0) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            HexTile[] neightbors = mapData.getNeightbors(coord.add(j, 0));
                            culling[i][j][0] = neightbors[2] == null || neightbors[2].getHeight() < inspectedMesh.getHeight() ? false : true; // top left
                            culling[i][j][1] = neightbors[1] == null || neightbors[1].getHeight() < inspectedMesh.getHeight() ? false : true; // top right
                            culling[i][j][2] = false;
                        }
                    } else if (i == 1) { //bot chunk = (Z)
                        if (shapeType.equals(Shape.SQUARE) && inspectedMesh.getPosition().y == HexSetting.CHUNK_SIZE - 1) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            HexTile[] neightbors = mapData.getNeightbors(coord.add(j, inspectedMesh.getSize().y - 1));
                            culling[i][j][0] = neightbors[4] == null || neightbors[4].getHeight() < inspectedMesh.getHeight() ? false : true; // bot left
                            culling[i][j][1] = neightbors[5] == null || neightbors[5].getHeight() < inspectedMesh.getHeight() ? false : true; // bot right
                            culling[i][j][2] = false;
                        }
                    } else if (i == 2) { // left chunk = -(X)
                        if (shapeType.equals(Shape.SQUARE) && inspectedMesh.getPosition().x == 0) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            HexTile[] neightbors = mapData.getNeightbors(coord.add(0, j));
                            culling[i][j][0] = neightbors[3] == null || neightbors[3].getHeight() < inspectedMesh.getHeight() ? false : true; // left
                            if (isOddStart && (j & 1) == 0) {
                                culling[i][j][1] = j != 0 && neightbors[2] == null || j != 0 && neightbors[2].getHeight() < inspectedMesh.getHeight() ? false : true; // top left
                                culling[i][j][2] = j != currentSize - 1 && neightbors[4] == null || j != currentSize - 1 && neightbors[4].getHeight() < inspectedMesh.getHeight() ? false : true; // bot left
                            } else if (!isOddStart && (j & 1) != 0) {
                                culling[i][j][1] = neightbors[2] == null || neightbors[2].getHeight() < inspectedMesh.getHeight() ? false : true; // top left
                                culling[i][j][2] = j != currentSize - 1 && neightbors[4] == null || j != currentSize - 1 && neightbors[4].getHeight() < inspectedMesh.getHeight() ? false : true; // bot left
                            } else {
                                culling[i][j][1] = false; // top left ignored
                                culling[i][j][2] = false; // bot left ignored
                            }
                        }
                    } else { // right chunk = (X)
                        if (shapeType.equals(Shape.SQUARE) && inspectedMesh.getPosition().x == HexSetting.CHUNK_SIZE - 1) {
                            culling[i][j][0] = false; // top left
                            culling[i][j][1] = false; // top right
                            culling[i][j][2] = false;
                        } else {
                            HexTile[] neightbors = mapData.getNeightbors(coord.add(inspectedMesh.getSize().x - 1, j));
                            culling[i][j][0] = neightbors[0] == null || neightbors[0].getHeight() < inspectedMesh.getHeight() ? false : true; // right
                            if (!isOddStart && (j & 1) == 0) {
                                culling[i][j][1] = j != 0 && neightbors[1] == null || j != 0 && neightbors[1].getHeight() < inspectedMesh.getHeight() ? false : true; // top right
                                culling[i][j][2] = j != currentSize - 1 && neightbors[5] == null || j != currentSize - 1 && neightbors[5].getHeight() < inspectedMesh.getHeight() ? false : true; // bot right
                            } else if (isOddStart && (j & 1) != 0) {
                                culling[i][j][1] = neightbors[1] == null || neightbors[1].getHeight() < inspectedMesh.getHeight() ? false : true; // top right
                                culling[i][j][2] = j != currentSize - 1 && neightbors[5] == null || j != currentSize - 1 && neightbors[5].getHeight() < inspectedMesh.getHeight() ? false : true; // bot right
                            } else {
                                culling[i][j][1] = false; // top right ignored
                                culling[i][j][2] = false; // bot right ignored
                            }
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
        elementMeshData.clear();
        groundHeight = 0;
    }

    private enum Shape {

        SQUARE,
        CIRCLE;
    }
}
