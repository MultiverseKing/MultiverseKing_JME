package org.multiversekingesapi.field.position;

/**
 *
 * @author roah
 */
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 * A position in 3d space
 *
 * @author Eike Foede
 */
public class WorldPositionComponent implements EntityComponent {

    private Vector3f position;

    public WorldPositionComponent() {
    }

    public WorldPositionComponent(Vector3f position) {
        this.position = position;
    }

    public WorldPositionComponent(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }
}
