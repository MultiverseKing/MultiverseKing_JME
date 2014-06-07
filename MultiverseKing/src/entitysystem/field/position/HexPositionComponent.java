package entitysystem.field.position;

import com.simsilica.es.PersistentComponent;
import utility.HexCoordinate;
import utility.Rotation;

/**
 *
 * @author Eike Foede
 */
public class HexPositionComponent implements PersistentComponent {

    private final HexCoordinate position;
    private Rotation rotation;

    /**
     *
     * @param position
     */
    public HexPositionComponent(HexCoordinate position, Rotation rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Where is the entity on hexMap.
     */
    public HexCoordinate getPosition() {
        return position;
    }

    /**
     * Direction hes facing.
     *
     * @see Rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    @Override
    public HexPositionComponent clone() {
        return new HexPositionComponent(position, rotation);
    }

    /**
     * Create a clone with modifiate rotation.
     *
     * @param rot new rotation.
     * @return the cloned component.
     */
    public HexPositionComponent clone(Rotation rot) {
        return new HexPositionComponent(position, rot);
    }

    /**
     * Create a clone with modifiate position.
     *
     * @param pos new position.
     * @return the cloned component.
     */
    public HexPositionComponent clone(HexCoordinate pos) {
        return new HexPositionComponent(pos, rotation);
    }
}
