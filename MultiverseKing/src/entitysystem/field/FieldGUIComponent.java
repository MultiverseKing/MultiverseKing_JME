package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 * Contain What the object is on the field.
 *
 * @author roah
 */
public class FieldGUIComponent implements PersistentComponent {

    public enum EntityType {
        NULL,
        TITAN,
        UNIT,
        ENVIRONMENT;
    }
    /**
     * Value used to know the GUI to use with.
     */
    private EntityType entityType;

    public FieldGUIComponent(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}