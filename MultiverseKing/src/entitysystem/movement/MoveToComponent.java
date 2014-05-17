package entitysystem.movement;

import entitysystem.ExtendedComponent;
import utility.HexCoordinate;

/**
 *
 * @author Eike Foede, roah
 */
public class MoveToComponent implements ExtendedComponent {

    private final HexCoordinate position;

    /**
     *
     * @param position
     */
    public MoveToComponent(HexCoordinate position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public HexCoordinate getPosition() {
        return position;
    }

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
