package gui.hexgrid;

import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public interface HexButtonListener {

    public void onButtonTrigger(HexCoordinate pos, boolean selected);
}
