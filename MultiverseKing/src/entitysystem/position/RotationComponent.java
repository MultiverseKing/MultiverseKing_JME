package entitysystem.position;

import com.simsilica.es.PersistentComponent;
import utility.Rotation;

/**
 * TODO: Comments
 * @author Eike Foede
 */
public class RotationComponent implements PersistentComponent {

    private Rotation rotation;

    public RotationComponent() {
    }

    public RotationComponent(Rotation rotation) {
        this.rotation = rotation;
    }

    public Rotation getRotation() {
        return rotation;
    }
}