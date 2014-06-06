package entitysystem.field.render;

import com.simsilica.es.PersistentComponent;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class RenderComponent implements PersistentComponent {

    private String name;

    public RenderComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
