package org.multiversekingesapi.field.position;

import com.simsilica.es.EntityComponent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Rotation;
import org.multiversekingesapi.SubSystem;

/**
 *
 * @author Eike Foede, roah
 */
public class HexPositionComponent implements EntityComponent {

    private final SubSystem system;
    private final HexCoordinate position;
    private final Rotation rotation;

    public HexPositionComponent(HexCoordinate position) {
        this(position, Rotation.A, null);
    }

    public HexPositionComponent(HexCoordinate position, Rotation rotation) {
        this(position, rotation, null);
    }

    public HexPositionComponent(HexCoordinate position, Rotation rotation, SubSystem system) {
        this.position = position;
        this.rotation = rotation;
        this.system = system;
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
     * SubSystem who will take into charge the positioning.
     * /!\ When settings this the HexPosition System will not handle the positioning,
     * it have to be handled by the subSystem registered.
     */
    public SubSystem getSubSystem() {
        return system;
    }

    @Override
    public HexPositionComponent clone() {
        return new HexPositionComponent(position, rotation, system);
    }

    /**
     * Clone component with modifiate rotation.
     *
     * @param rot new rotation.
     * @return the cloned component.
     */
    public HexPositionComponent clone(Rotation rot) {
        return new HexPositionComponent(position, rot, system);
    }

    /**
     * Clone component with modifiate position.
     *
     * @param pos new position.
     * @return the cloned component.
     */
    public HexPositionComponent clone(HexCoordinate pos) {
        return new HexPositionComponent(pos, rotation, system);
    }

    /**
     * Clone component removing the SubSystem Constraint.
     * 
     * @return the cloned component.
     */
    public EntityComponent cloneWithoutSubSystem() {
        return new HexPositionComponent(position, rotation);
    }

    /**
     * Clone component removing the SubSystem Constraint
     * && setting a new position.
     * 
     * @param position new position.
     * @return the cloned component.
     */
    EntityComponent cloneWithoutSubSystem(HexCoordinate position) {
        return new HexPositionComponent(position, rotation);
    }
    
    /**
     * Clone component removing the SubSystem Constraint
     * && setting a new rotation.
     * 
     * @param rot new rotation.
     * @return the cloned component.
     */
    EntityComponent cloneWithoutSubSystem(Rotation rot) {
        return new HexPositionComponent(position, rot);
    }
}
