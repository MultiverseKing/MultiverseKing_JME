package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 * TODO: Comments
 * Used by the card system and the render system.
 * @author Eike Foede, Roah
 */
public class RenderComponent implements PersistentComponent {

    private String name;

    public RenderComponent() {
    }

    public RenderComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
