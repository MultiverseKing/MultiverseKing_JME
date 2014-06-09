package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class RenderComponent implements PersistentComponent {

    public enum EntityType {

        TITAN,
        UNIT,
        ENVIRONMENT;
    }
    private String name;
    /**
     * Value used to know the GUI to use with.
     */
    private EntityType entityType;

    public RenderComponent(String name, EntityType entityType) {
        this.name = name;
        this.entityType = entityType;
    }

    public String getName() {
        return name;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
