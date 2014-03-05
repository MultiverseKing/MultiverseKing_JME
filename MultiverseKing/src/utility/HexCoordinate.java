/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Roah with the help of : ArtemisArt => http://artemis.art.free.fr/ &&
 * http://www.redblobgames.com --Changed version by toboi-- This Class is only
 * used as a converter system so we can simplifie algorithm.
 */
public final class HexCoordinate {

    public static final int OFFSET = 0;
    public static final int AXIAL = 1;
    public static final int CUBIC = 2;
    /**
     * Axial position in Grid. q == x
     */
    public int q;
    /**
     * Axial position in Grid. r == z (or Y)
     */
    public int r;

    private static Vector2Int offsetToAxial(Vector2Int data) {
        return new Vector2Int(data.x - (data.y - (data.y & 1)) / 2, data.y);
    }

    private static Vector2Int cubicToAxial(Vector3Int data) {
        return new Vector2Int(data.x, data.z);
    }

    public Vector3Int getAsCubic() {
        return new Vector3Int(q, -q - r, r);
    }

    public Vector2Int getAsOffset() {
        return new Vector2Int(q + (r - (r & 1)) / 2, r);
    }

    public Vector2Int getAsAxial() {
        return new Vector2Int(q, r);
    }

    /**
     * Only for internal use
     *
     * @param q
     * @param r
     */
    private HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public HexCoordinate(int type, Vector2Int pos) {
        if (type == OFFSET) {
            pos = offsetToAxial(pos);
        }
        q = pos.x;
        r = pos.y;
    }

    public HexCoordinate(int type, int x, int y) {
        this(type, new Vector2Int(x, y));
    }

    public HexCoordinate(int type, Vector3Int pos) {
        if (type != CUBIC) {
            throw new UnsupportedOperationException("Only TYPE_CUBIC expects a Vector3Int!");
        }
        Vector2Int posAxial = cubicToAxial(pos);
        q = posAxial.x;
        r = posAxial.y;
    }

    /**
     * @return faf
     */
    @Override
    public String toString() {
        return q + "|" + r;
    }

    public HexCoordinate[] getNeighbours() {
        Class c = HexCoordinate.class;

        HexCoordinate[] neighbours = new HexCoordinate[]{
            new HexCoordinate(q + 1, r),
            new HexCoordinate(q + 1, r - 1),
            new HexCoordinate(q, r - 1),
            new HexCoordinate(q - 1, r),
            new HexCoordinate(q - 1, r + 1),
            new HexCoordinate(q, r + 1)
        };
        return neighbours;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HexCoordinate){
            HexCoordinate coord = (HexCoordinate) obj;
            Vector2Int axial = coord.getAsAxial();
            if(axial.x == q && axial.y == r){
                return true;
            }
        }
        return false;
    }
    
}
