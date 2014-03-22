package entitysystem.position;

import com.jme3.math.Quaternion;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.PersistentComponent;

/**
 *TODO: Comments
 * @author Eike Foede
 */
public class RotationComponent implements PersistentComponent {

    private Quaternion rotation;

    public RotationComponent() {
    }

    public RotationComponent(Quaternion rotation) {
        this.rotation = rotation;
    }

    public Quaternion getRotation() {
        return rotation;
    }
}
