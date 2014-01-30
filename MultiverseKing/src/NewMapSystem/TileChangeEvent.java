package NewMapSystem;

/**
 *
 * @author Eike Foede
 */
public class TileChangeEvent {

    private int x;
    private int y;
    private HexTile oldTile;
    private HexTile newTile;

    public TileChangeEvent(int x, int y, HexTile oldTile, HexTile newTile) {
        this.x = x;
        this.y = y;
        this.oldTile = oldTile;
        this.newTile = newTile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HexTile getOldTile() {
        return oldTile;
    }

    public HexTile getNewTile() {
        return newTile;
    }
}
