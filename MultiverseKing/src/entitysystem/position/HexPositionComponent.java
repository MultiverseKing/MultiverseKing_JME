package entitysystem.position;

import com.simsilica.es.PersistentComponent;
import utility.HexCoordinate;

/**
 *
 * @author Eike Foede
 */
public class HexPositionComponent implements PersistentComponent {

    private final HexCoordinate position;
//Rotation?
    public HexPositionComponent(HexCoordinate position) {
        this.position = position;
    }

    public HexCoordinate getPosition() {
        return position;
    }
}
