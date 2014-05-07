package entitysystem.position;

import com.simsilica.es.PersistentComponent;
import utility.Rotation;

/**
 * TODO: Comments
 * @author Eike Foede
 */
public class RotationComponent implements PersistentComponent {

    private Rotation rotation;

    /**
     *
     */
    public RotationComponent() {
    }

    /**
     *
     * @param rotation
     */
    public RotationComponent(Rotation rotation) {
        this.rotation = rotation;
    }

    /**
     *
     * @return
     */
    public Rotation getRotation() {
        return rotation;
    }
}