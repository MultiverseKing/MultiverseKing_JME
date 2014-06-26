package entitysystem.field.position;

import com.simsilica.es.PersistentComponent;
import utility.HexCoordinate;

/**
 *
 * @author Eike Foede, roah
 */
public class MoveToComponent implements PersistentComponent {

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
}
