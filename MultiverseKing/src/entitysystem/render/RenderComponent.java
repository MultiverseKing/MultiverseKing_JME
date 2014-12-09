package entitysystem.render;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.PersistentComponent;
import entitysystem.SubSystem;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class RenderComponent implements PersistentComponent {
    
    private String name;
    private boolean isVisible;
    private SubSystem system;
    private Type type;

    public RenderComponent(String name, Type type) {
        this(name, type, null, true);
    }

    public RenderComponent(String name, Type type, SubSystem system) {
        this(name, type, system, true);
    }
    
    public RenderComponent(String name, Type type, SubSystem system, boolean isVisible) {
        this.name = name;
        this.type = type;
        this.system = system;
        this.isVisible = isVisible;
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

    public boolean isVisible() {
        return isVisible;
    }
    
    public EntityComponent cloneAndHide() {
        return new RenderComponent(name, type, system, false);
    }
    
    public EntityComponent cloneAndShow() {
        return new RenderComponent(name, type, system, false);
    }
    
    public enum Type{
        Units,
        Titan,
        Core,
        Environments,
        Debug;
    }
}
