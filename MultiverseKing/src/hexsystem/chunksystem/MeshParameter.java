package hexsystem.chunksystem;

import hexsystem.HexTile;
import hexsystem.MapData;
import java.util.ArrayList;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MeshParameter {

    private final MapData mapData;
    private Vector2Int subChunkWorldGridPosOffset;
    private ArrayList<Vector2Int> position = new ArrayList<Vector2Int>();
    private ArrayList<Vector2Int> size = new ArrayList<Vector2Int>();
    private ArrayList<Byte> elementType = new ArrayList<Byte>();
    private ArrayList<Byte> height = new ArrayList<Byte>();
    private boolean onlyGround;

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
     * @param subChunkSize
     * @param subChunkWorldGridPos
     * @param onlyGround
     */
    public void initialize(int subChunkSize, HexCoordinate subChunkWorldGridPos, boolean onlyGround) {
        this.subChunkWorldGridPosOffset = subChunkWorldGridPos.getAsOffset();
        int i = 0;
        boolean initParam = false;
        for (int y = 0; y < subChunkSize; y++) {
            if (initParam) {
                initParam = false;
                i++;
            }
            for (int x = 0; x < subChunkSize - 1; x++) {
                HexTile tile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, subChunkWorldGridPosOffset.x + x, subChunkWorldGridPosOffset.y + y));
                HexTile nearTile = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, subChunkWorldGridPosOffset.x + x + 1, subChunkWorldGridPosOffset.y + y));
                if (!initParam) {
                    this.add(new Vector2Int(x, y), new Vector2Int(1, 1), (byte) tile.getElement().ordinal(), (byte) tile.getHeight());
                    initParam = true;
                }
                if (nearTile.getElement() == tile.getElement()
                        && nearTile.getHeight() == tile.getHeight()) {
                    this.extendsSizeX(i);
                } else {
                    i++;
                    this.add(new Vector2Int(x + 1, y), new Vector2Int(1, 1), (byte) nearTile.getElement().ordinal(), (byte) nearTile.getHeight());
                }
            }
        }
    }

    private void add(Vector2Int position, Vector2Int size, byte elementType, byte height) {
        this.position.add(position);
        this.size.add(size);
        this.elementType.add(elementType);
        this.height.add(height);
    }

    private void extendsSizeX(int i) {
        size.get(i).x++;
    }

    public Vector2Int getPosition(int i) {
        return position.get(i);
    }

    public Vector2Int getSize(int i) {
        return size.get(i);
    }

    public byte getElementType(int i) {
        return elementType.get(i);
    }

    public byte getHeight(int i) {
        return height.get(i);
    }

    public int size() {
        return this.size.size();
    }

    public Boolean[][] getCulling(int i) {
        Boolean[][] neightborsCull = new Boolean[size.get(i).x][6];
        for (int j = 0; j < size.get(i).x; j++) {
            HexTile[] neightbors = mapData.getNeightbors(new HexCoordinate(HexCoordinate.OFFSET, position.get(i).x + subChunkWorldGridPosOffset.x + j, position.get(i).y + subChunkWorldGridPosOffset.y));
            for (byte k = 0; k < 6; k++) {
                if (neightbors[k] != null) {
                    if (neightbors[k].getHeight() >= height.get(i)) {
                        neightborsCull[j][k] = false;
                    } else {
                        neightborsCull[j][k] = true;
                    }
                } else {
                    neightborsCull[j][k] = false;
                }
            }
        }
        return neightborsCull;
    }

    public boolean onlyGround() {
        return onlyGround;
    }
}
