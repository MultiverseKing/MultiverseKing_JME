package org.multiversekingesapi.field.position;

import com.simsilica.es.EntityComponent;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author Eike Foede, roah
 */
public class MoveToComponent implements EntityComponent {

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
