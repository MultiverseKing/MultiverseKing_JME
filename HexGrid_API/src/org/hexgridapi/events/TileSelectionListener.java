package org.hexgridapi.events;

import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public interface TileSelectionListener {

    void onTileSelectionUpdate(HexCoordinate currentSelection);
}
