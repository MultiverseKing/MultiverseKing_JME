package entitysystem.position;

import com.jme3.math.Vector3f;
import com.simsilica.es.PersistentComponent;

/**
 * A position in 3d space
 *
 * @author Eike Foede
 */
public class PositionComponent implements PersistentComponent {

    private Vector3f position;

    /**
     *
     */
    public PositionComponent() {
    }

    /**
     *
     * @param position
     */
    public PositionComponent(Vector3f position) {
        this.position = position;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public PositionComponent(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        return position;
    }
}
