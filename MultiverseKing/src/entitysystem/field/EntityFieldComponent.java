package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 * Contain What the object is on the field.
 *
 * @author roah
 */
public class EntityFieldComponent implements PersistentComponent {

    public enum EntityType {

        TITAN,
        UNIT,
        ENVIRONMENT;
    }
    private EntityType entityType;

    public EntityFieldComponent(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}