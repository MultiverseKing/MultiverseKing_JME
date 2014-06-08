package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class EntityRenderComponent implements PersistentComponent {

    private String name;

    public EntityRenderComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
