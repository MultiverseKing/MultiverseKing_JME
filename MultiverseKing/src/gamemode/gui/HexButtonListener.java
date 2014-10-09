package gamemode.gui;

import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public interface HexButtonListener {
    public void onButtonTrigger(HexCoordinate pos, boolean selected);
}
