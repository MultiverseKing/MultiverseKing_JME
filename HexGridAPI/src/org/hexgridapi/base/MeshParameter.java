package org.hexgridapi.base;

import com.jme3.math.FastMath;
import com.jme3.scene.Mesh;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * Used to generate the needed data used by the MeshManager.
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
    private ArrayList<Byte> height = new ArrayList<Byte>();
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
    private String currentElement;
    private int currentIndex;

    /**
     * Generate all parameter needed to create a grid from defined value.
     *
     * @param mapData Reference.
     * @param chunkSize max radius.
     * @param onlyGround generate only the top face ?
     * @param chunkShape shape to use when generating the chunk.
     */
    public MeshParameter(MapData mapData) {
        this.mapData = mapData;
    }

    private void initialize(HexCoordinate centerPosition, int radius, boolean onlyGround) {
        clear();
        this.onlyGround = onlyGround;
        boolean[][] isVisited = getVisitedList(radius);
        /**
         * x && y == coord local
         */
        int posInList = 0;
        for (int y = 0; y < isVisited.length; y++) {
            for (int x = 0; x < isVisited.length; x++) {
                if (!isVisited[x][y]) {
                    HexTile currentTile;
                    String textValue;
                    boolean currentIsInRange;
                    HexCoordinate currentTileMapCoord; //global coord -> used in map data and range check
                    if (centerPosition != null && radius > 0) {
                        currentTileMapCoord = centerPosition.add(x + ((centerPosition.getAsOffset().y&1) == 0 && (radius&1) != 0? - radius : -radius), y - radius);
                        currentTile = mapData.getTile(currentTileMapCoord);
                        currentIsInRange = getIsInRange(radius, null, x, y);
                    } else {
                        currentTileMapCoord = new HexCoordinate(HexCoordinate.OFFSET, x, y);
                        currentTile = mapData.getTile(currentTileMapCoord);
                        currentIsInRange = false;
                    }
                    if (currentTile == null || centerPosition != null && !currentIsInRange) {
                        textValue = mapData.getTextureValue((byte) -2); //Value used to not generate mesh on that position.
                    } else if (centerPosition != null) {
                        textValue = mapData.getTextureValue((byte) -1);  //Value used to use the areaRange texture.
                    } else {
                        textValue = mapData.getTextureValue(currentTile.getTextureKey());
                    }
                    if (elementTypeRef.isEmpty() || !elementTypeRef.containsKey(textValue)) {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        elementTypeRef.put(textValue, list);
                    }
                    elementTypeRef.get(textValue).add(posInList);
                    this.add(new Vector2Int(x, y), new Vector2Int(1, 1), (currentTile == null ? null : currentTile.getHeight()));
                    setSizeX(centerPosition, radius, posInList, isVisited, currentTile, currentIsInRange);
                    posInList++;
                }
            }
        }
    }

    private void setSizeX(HexCoordinate centerPos, int radius, int posInList, boolean[][] isVisited, HexTile currentTile, boolean currentIsInRange) {
        for (int x = 1; x < isVisited.length - position.get(posInList).x; x++) {
            boolean alreadyVisited = isVisited[position.get(posInList).x + x][position.get(posInList).y];
            HexCoordinate nextTileMapCoord = getNextTileCoord(centerPos, radius, posInList, x, 0);
            HexTile nextTile = mapData.getTile(nextTileMapCoord);
            if (!alreadyVisited && currentTile == null && nextTile == null
                    || !alreadyVisited && centerPos == null && currentTile != null && nextTile != null
                    && currentTile.getTextureKey() == nextTile.getTextureKey() && currentTile.getHeight() == nextTile.getHeight()
                    || !alreadyVisited && centerPos != null && currentTile != null && nextTile != null
                    && getIsInRange(radius, posInList, x, 0) == currentIsInRange
                    && currentTile.getHeight() == nextTile.getHeight()) {
                this.size.get(posInList).x++;
                isVisited[x + position.get(posInList).x][position.get(posInList).y] = true;
            } else {
                setSizeY(centerPos, radius, posInList, isVisited, currentTile, currentIsInRange);
                return;
            }
        }
        setSizeY(centerPos, radius, posInList, isVisited, currentTile, currentIsInRange);
    }

    private void setSizeY(HexCoordinate centerPos, int radius, int posInList, boolean[][] isVisited, HexTile currentTile, boolean currentIsInRange) {
        for (int y = 1; y < isVisited.length - position.get(posInList).y; y++) {
            //We check if the next Y line got the same properties
            for (int x = 0; x < size.get(posInList).x; x++) {
                boolean alreadyVisited = isVisited[position.get(posInList).x + x][y + position.get(posInList).y];
                HexCoordinate nextTileMapCoord = getNextTileCoord(centerPos, radius, posInList, x, y);
                HexTile nextTile = mapData.getTile(nextTileMapCoord);
                if (alreadyVisited || currentTile == null && nextTile != null || currentTile != null && nextTile == null
                        || centerPos == null && currentTile != null && nextTile != null
                        && !mapData.getTextureValue(nextTile.getTextureKey()).equals(mapData.getTextureValue(currentTile.getTextureKey()))
                        || centerPos == null && currentTile != null && nextTile != null && nextTile.getHeight() != currentTile.getHeight()
                        || centerPos != null && currentTile != null && nextTile != null
                        && getIsInRange(radius, posInList, x, y) != currentIsInRange
                        || centerPos != null && currentTile != null && nextTile != null
                        && nextTile.getHeight() != currentTile.getHeight()) {
                    //if one tile didn't match the requirement we stop the search
                    return;
                }
            }
            //all tile meet the requirement we increase the size Y
            size.get(posInList).y++;
            //we set that line as visited so we don't do any operation later for them
            for (int x = 0; x < size.get(posInList).x; x++) {
                isVisited[position.get(posInList).x + x][position.get(posInList).y + y] = true;
            }
        }
    }

    private boolean getIsInRange(int radius, Integer posInList, int x, int y){
        return new HexCoordinate(HexCoordinate.OFFSET, radius, radius).hasInRange(
                new HexCoordinate(HexCoordinate.OFFSET, (posInList != null ? position.get(posInList).x : 0) + x, (posInList != null ? position.get(posInList).y : 0) + y), radius);
    }
    
    private HexCoordinate getNextTileCoord(HexCoordinate centerPos, int radius, int posInList, int x, int y) {
        if (centerPos != null) {
            return centerPos.add(new Vector2Int(x + position.get(posInList).x
                    + ((centerPos.getAsOffset().y&1) != 0 && (radius&1) == 0 ? - radius : -radius), 
                    y - radius + position.get(posInList).y));
//            currentTileMapCoord = centerPosition.add(x -radius, y - radius);
//            nextTileMapCoord = nextTileMapCoord.add(centerPos.getAsOffset().x + -radius, centerPos.getAsOffset().y - radius);
        } else {
            return new HexCoordinate(HexCoordinate.OFFSET, x + position.get(posInList).x, y + position.get(posInList).y);
        }
    }

    private boolean[][] getVisitedList(int radius) {
        int chunkSize = (radius < 1 ? HexSetting.CHUNK_SIZE : (radius * 2) + 1);
        boolean[][] isVisited = new boolean[chunkSize][chunkSize];
        for (int y = 0; y < chunkSize; y++) {
            for (int x = 0; x < chunkSize; x++) {
                isVisited[x][y] = false;
            }
        }
        return isVisited;
    }

    private void add(Vector2Int position, Vector2Int size, Byte height) {
        this.position.add(position);
        this.size.add(size);
        this.height.add((height == null ? 0 : height));
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public HashMap<String, Mesh> getMesh(boolean onlyGround) {
        initialize(null, 0, onlyGround);
        return getMesh();
    }

    public HashMap<String, Mesh> getMesh(HexCoordinate centerPosition, int radius, boolean onlyGround) {
        initialize(centerPosition, radius, onlyGround);
        return getMesh();
    }

    private HashMap<String, Mesh> getMesh() {
        HashMap<String, Mesh> mesh = new HashMap<String, Mesh>(elementTypeRef.size());
        for (String value : elementTypeRef.keySet()) {
            currentElement = value;
            currentIndex = -1;
            mesh.put(value, MeshManager.getInstance().getMesh(this));
        }
        return mesh;
    }

    /**
     * @return index list of all mesh of the current element.
     */
    public ArrayList<Integer> getElementMeshIndex() {
        return this.elementTypeRef.get(currentElement);
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
        return position.get(elementTypeRef.get(currentElement).get(currentIndex));
    }

    /**
     * @return the size of the current element mesh visited.
     */
    public Vector2Int getSizeParam() {
        return size.get(elementTypeRef.get(currentElement).get(currentIndex));
    }

    /**
     * @return height of the current element mesh visited.
     */
    public byte getHeightParam() {
        return height.get(elementTypeRef.get(currentElement).get(currentIndex));
    }

    /**
     * set to true if the depth isn't needed.
     *
     * @return
     */
    public boolean onlyGround() {
        return onlyGround;
    }

    /**
     * wish side of the mesh should be rendered on other term.
     *
     * @todo : update
     * @return
     */
    public Boolean[][] getCulling() {
        int current = elementTypeRef.get(currentElement).get(currentIndex);

        Boolean[][] neightborsCull = new Boolean[size.get(current).x][6];
        for (int j = 0; j < size.get(current).x; j++) {
//            HexCoordinate[] coords = new HexCoordinate(HexCoordinate.OFFSET, position.get(current)).getNeighbours();
//            for (byte k = 0; k < 6; k++) {
//                if(coords[k].getAsOffset().x == mapData.getHexSettings().getCHUNK_SIZE()){
//                    
//                }
//            }
            HexTile[] neightbors = mapData.getNeightbors(new HexCoordinate(HexCoordinate.OFFSET,
                    position.get(current).x + j, position.get(current).y));
            for (byte k = 0; k < 6; k++) {
                if (height.get(current) >= 0) {
                    if (neightbors[k] != null) {
                        if (neightbors[k].getHeight() >= height.get(current)) {
                            neightborsCull[j][k] = false;
                        } else {
                            neightborsCull[j][k] = true;
                        }
                    } else {
                        neightborsCull[j][k] = false;
                    }
                } else {
                    if (neightbors[k] != null) {
                        if (neightbors[k].getHeight() >= height.get(current)) {
                            neightborsCull[j][k] = true;
                        } else {
                            neightborsCull[j][k] = false;
                        }
                    } else {
                        neightborsCull[j][k] = true;
                    }
                }
            }
        }
        return neightborsCull;
    }

    /**
     * How many mesh param this element have.
     *
     * @return
     */
    public int getElementMeshCount() {
        return elementTypeRef.get(currentElement).size();
    }

    /**
     * Return true if there is another mesh to generate for the current element.
     *
     * @return
     */
    public boolean hasNext() {
        currentIndex++;
        if (elementTypeRef.get(currentElement).size() > currentIndex) {
            return true;
        } else {
            return false;
        }
    }
    // </editor-fold>

    private void clear() {
        elementTypeRef.clear();
        height.clear();
        position.clear();
        size.clear();
    }
}
