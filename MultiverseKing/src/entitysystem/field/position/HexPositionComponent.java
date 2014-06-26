package entitysystem.field.position;

import com.simsilica.es.EntityComponent;
import entitysystem.render.utility.Curve;
import com.simsilica.es.PersistentComponent;
import utility.HexCoordinate;
import utility.Rotation;

/**
 *
 * @author Eike Foede
 */
public class HexPositionComponent implements PersistentComponent {
    
    private final Curve curve;
    private final HexCoordinate position;
    private final Rotation rotation;

    public HexPositionComponent(HexCoordinate position, Rotation rotation, Curve curve) {
        this.position = position;
        this.rotation = rotation;
        this.curve = curve;
    }
    /**
     *
     * @param position
     */
    public HexPositionComponent(HexCoordinate position, Rotation rotation) {
        this.position = position;
        this.rotation = rotation;
        this.curve = null;
    }

    /**
     * Where is the entity on hexMap.
     */
    public HexCoordinate getPosition() {
        return position;
    }

    /**
     * Direction the entity is facing.
     *
     * @see Rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Curve to use when the entity position is updated.
     * @return 
     */
    public Curve getCurve() {
        return curve;
    }
    
    @Override
    public HexPositionComponent clone() {
        return new HexPositionComponent(position, rotation, curve);
    }

    /**
     * Create a interpolateTo with modifiate rotation.
     *
     * @param rot new rotation.
     * @return the cloned component.
     */
    public HexPositionComponent clone(Rotation rot) {
        return new HexPositionComponent(position, rot, curve);
    }

    /**
     * Create a interpolateTo with modifiate position.
     *
     * @param pos new position.
     * @return the cloned component.
     */
    public HexPositionComponent clone(HexCoordinate pos) {
        return new HexPositionComponent(pos, rotation, curve);
    }
    
    /**
     * Create an interpolation from this component position to another position.
     *
     * @param CurveType interpolation type.
     * @param position destination Position.
     * @return the cloned component.
     */
    public HexPositionComponent interpolateTo(Curve curve, HexCoordinate position) {
        return new HexPositionComponent(position, rotation, curve);
    }

    public EntityComponent cloneWithoutCurve() {
        return new HexPositionComponent(position, rotation);
    }
}
