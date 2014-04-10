package entitysystem.movement;

import com.simsilica.es.PersistentComponent;
import utility.HexCoordinate;

/**
 *
 * @author Eike Foede
 */
public class MoveToComponent implements PersistentComponent {

    private final HexCoordinate position;

    public MoveToComponent(HexCoordinate position) {
        this.position = position;
    }

    public HexCoordinate getPosition() {
        return position;
    }
}
