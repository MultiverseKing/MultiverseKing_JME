package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class CoreRenderComponent implements PersistentComponent {
    
    private String name;
    

    public CoreRenderComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
