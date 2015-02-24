package core.event;

import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public interface TileSelectionListener {
    void tileSelectUpdate(HexCoordinate selectionList);
}
