package hexsystem.chunksystem;

import hexsystem.HexTile;
import hexsystem.MapData;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Set;
import utility.HexCoordinate;
import utility.Vector2Int;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class MeshParameter {

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
    private EnumMap<ElementalAttribut, ArrayList<Integer>> elementTypeRef = new EnumMap<ElementalAttribut, ArrayList<Integer>>(ElementalAttribut.class);
    /**
     * Used to define which algorithm to use with meshmanager.
     */
    private boolean onlyGround;
    /**
     * Current element param returned.
     */
    private ElementalAttribut currentElement;
    private int currentIndex;

    /**
     * Need to be initialized before use.
     *
     * @param mapData
     */
    public MeshParameter(MapData mapData) {
        this.mapData = mapData;
    }

    /**
     * Call This before sending the param else it will fail.
     *
     * @param chunkSize
     * @param onlyGround
     */
    public void initialize(int chunkSize, boolean onlyGround) {
        this.onlyGround = onlyGround;
        boolean[][] isVisited = new boolean[chunkSize][chunkSize];
        for (int y = 0; y < chunkSize; y++) {
            for (int x = 0; x < chunkSize; x++) {
                isVisited[x][y] = false;
            }
        }

        int posInList = 0;
        for (int y = 0; y < chunkSize; y++) {
            for (int x = 0; x < chunkSize; x++) {
                if (!isVisited[x][y]) {
                    HexTile currentTile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, x, y));
                    if (elementTypeRef.isEmpty() || !elementTypeRef.containsKey(currentTile.getElement())) {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        elementTypeRef.put(currentTile.getElement(), list);
                    }
                    elementTypeRef.get(currentTile.getElement()).add(posInList);
                    this.add(new Vector2Int(x, y), new Vector2Int(1, 1), (byte) currentTile.getHeight());
                    setSize(chunkSize, posInList, isVisited, currentTile);
                    posInList++;
                }
            }
        }
    }

    private void add(Vector2Int position, Vector2Int size, byte height) {
        this.position.add(position);
        this.size.add(size);
        this.height.add(height);
    }

    private void setSize(int chunksize, int posInList, boolean[][] isVisited, HexTile currentTile) {
        for (int x = 1; x < chunksize - position.get(posInList).x; x++) {
            HexTile nextTile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, x + position.get(posInList).x, position.get(posInList).y));
            if (nextTile.getElement() == currentTile.getElement()
                    && nextTile.getHeight() == currentTile.getHeight()) {
                this.size.get(posInList).x++;
                isVisited[x + position.get(posInList).x][position.get(posInList).y] = true;
            } else {
                setSizeY(posInList, isVisited, currentTile);
                return;
            }
        }
        //todo
        setSizeY(posInList, isVisited, currentTile);
    }

    private void setSizeY(int posInList, boolean[][] isVisited, HexTile currentTile) {
        for (int y = 1; y < size.get(posInList).y; y++) {
            //We check if the next Y line got the same properties
            for (int x = 0; x < size.get(posInList).x; x++) {
                HexTile nextTile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, x + position.get(posInList).x, position.get(posInList).y));
                if (nextTile.getElement() != currentTile.getElement()
                        || nextTile.getHeight() != currentTile.getHeight()) {
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

    /**
     * @return index list of all mesh of the current element.
     */
    public ArrayList<Integer> getElementMeshIndex() {
        return this.elementTypeRef.get(currentElement);
    }

    /**
     * @return a list of all element.
     */
    public Set<ElementalAttribut> getAllElementInList() {
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
     * Set the current element you looking for to be initialized.
     *
     * @param e current element.
     * @return current instance of meshParameter, (less typing ^_^)
     */
    public MeshParameter setElement(ElementalAttribut e) {
        this.currentElement = e;
        this.currentIndex = -1;
        return this;
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
}
