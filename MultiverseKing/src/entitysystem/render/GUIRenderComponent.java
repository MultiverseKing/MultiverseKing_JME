package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 * Contain What the object is on the field.
 *
 * @author roah
 */
public class GUIRenderComponent implements PersistentComponent {

    public enum EntityType {
        TITAN,
        UNIT,
        ENVIRONMENT;
    }
    /**
     * Value used to know the GUI to use with.
     */
    private EntityType entityType;

    public GUIRenderComponent(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}