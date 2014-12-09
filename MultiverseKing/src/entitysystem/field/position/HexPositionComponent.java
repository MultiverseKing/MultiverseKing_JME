package entitysystem.field.position;

import com.simsilica.es.EntityComponent;
import entitysystem.render.utility.Curve;
import com.simsilica.es.PersistentComponent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Rotation;

/**
 *
 * @author Eike Foede
 */
public class HexPositionComponent implements PersistentComponent {

    private final Curve curve;
    private final HexCoordinate position;
    private final Rotation rotation;

    public HexPositionComponent(HexCoordinate position) {
        this(position, Rotation.A, null);
    }
    
    public HexPositionComponent(HexCoordinate position, Rotation rotation) {
        this(position, rotation, null);
    }
    
    public HexPositionComponent(HexCoordinate position,Rotation rotation, Curve curve) {
        this.position = position;
        this.rotation = rotation;
        this.curve = curve;
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
     *
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
     * Create a movement with modifiate rotation.
     *
     * @param rot new rotation.
     * @return the cloned component.
     */
    public HexPositionComponent clone(Rotation rot) {
        return new HexPositionComponent(position, rot, curve);
    }

    /**
     * Create a movement with modifiate position.
     *
     * @param pos new position.
     * @return the cloned component.
     */
    public HexPositionComponent clone(HexCoordinate pos) {
        return new HexPositionComponent(pos, rotation, curve);
    }

    /**
     * Create interpolation from this component.
     *
     * @param CurveType interpolation type.
     * @return the cloned component.
     */
    public HexPositionComponent movement(Curve curve) {
        return new HexPositionComponent(position, rotation, curve);
    }

    public EntityComponent cloneWithoutCurve() {
        return new HexPositionComponent(position, rotation);
    }

    EntityComponent cloneWithoutCurve(HexCoordinate position) {
        return new HexPositionComponent(position, rotation);
    }

    EntityComponent cloneWithoutCurve(HexCoordinate position, Rotation rotation) {
        return new HexPositionComponent(position, rotation);
    }
}
