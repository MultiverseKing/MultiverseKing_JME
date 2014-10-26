package entitysystem.render;

import com.simsilica.es.PersistentComponent;
import entitysystem.SubSystem;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class RenderComponent implements PersistentComponent {
    
    private String name;
    private SubSystem system;
    private Type type;

    public RenderComponent(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public RenderComponent(String name, Type type, SubSystem system) {
        this.name = name;
        this.type = type;
        this.system = system;
    }

    public String getName() {
        return name;
    }

    public SubSystem getSystem() {
        return system;
    }

    public Type getType() {
        return type;
    }
    
    public enum Type{
        Units,
        Debug;
    }
}
