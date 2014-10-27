package org.hexgridapi.base;

import com.jme3.scene.Mesh;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Set;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;
import org.hexgridapi.utility.ElementalAttribut;

/**
 * Used to generate the needed data used by the MeshManager.
 *
 * @author roah
 */
public final class MeshParameter {

    public enum Shape {

        SQUARE,
        CIRCLE;
    }
    private static final MeshManager meshManager = new MeshManager();
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

    private void initialize(boolean onlyGround, Shape chunkShape){
        switch (chunkShape) {
            case CIRCLE:
                /**
                 * @todo:
                 */
                break;
            case SQUARE:
                initializeSquare(onlyGround);
                break;
        }
        
    }
    
    private void initializeSquare(boolean onlyGround) {
        this.onlyGround = onlyGround;
        int chunkSize = HexSetting.CHUNK_SIZE;
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

    public HashMap<ElementalAttribut, Mesh> getMesh(boolean onlyGround, Shape chunkShape) {
        clear();
        initialize(onlyGround, chunkShape);
        HashMap<ElementalAttribut, Mesh> mesh = new HashMap<ElementalAttribut, Mesh>(elementTypeRef.size());
        for (ElementalAttribut e : elementTypeRef.keySet()) {
            currentElement = e;
            currentIndex = -1;
            mesh.put(e, meshManager.getMesh(this));
        }
        return mesh;
    }
    
    private void clear(){
        elementTypeRef.clear();
        height.clear();
        position.clear();
        size.clear();
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
