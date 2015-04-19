package org.multiversekingesapi.render;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.PersistentComponent;
import org.multiversekingesapi.SubSystem;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class RenderComponent extends Render implements PersistentComponent {

    private final SubSystem subSystem;

    /**
     * Component used for rendering. SubSytem == null && visibility == true &&
     * rotation == Rotation.A.
     * 
     * @param name Object name to render.
     * @param renderType type of rendering to use.
     */
    public RenderComponent(String name, RenderType renderType) {
        this(name, renderType, null, true);
    }

    /**
     * Component used for rendering. visibility == true && rotation == Rotation.A.
     * 
     * @param name object name to render.
     * @param renderType type of rendering.
     * @param system  does is to be visible.
     */
    public RenderComponent(String name, RenderType renderType, SubSystem system) {
        this(name, renderType, system, true);
    }
    
    /**
     * component used for the rendering.
     *
     * @param name Object name to render.
     * @param renderType type of rendering to use.
     * @param system is this object belong to another system.
     * @param isVisible does is to be visible.
     */
    public RenderComponent(String name, RenderType renderType, SubSystem system, boolean isVisible) {
        super(name, renderType, isVisible);
        this.subSystem = system;
    }

    public SubSystem getSubSystem() {
        return subSystem;
    }
    
    public EntityComponent cloneAndHide() {
        return new RenderComponent(getName(), getRenderType(), subSystem, false);
    }

    public EntityComponent cloneAndShow() {
        return new RenderComponent(getName(), getRenderType(), subSystem, true);
    }
}
