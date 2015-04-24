package org.multiversekingesapi.render;

import com.simsilica.es.PersistentComponent;
import org.multiversekingesapi.SubSystem;

/**
 *
 * @author Eike Foede, roah
 */
public class RenderComponent extends AbstractRender implements PersistentComponent {

    private final SubSystem subSystem;

    /**
     * Component used for rendering. rotation == Rotation.A && SubSytem == null 
     * && visibility == true.
     *
     * @param name Object name to render.
     * @param renderType type of rendering to use.
     */
    public RenderComponent(String name, RenderType renderType) {
        this(name, renderType, null, true);
    }

    /**
     * Component used for rendering. visibility == true.
     *
     * @param name object name to render.
     * @param renderType type of rendering.
     * @param system does is to be visible.
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

    public RenderComponent cloneAndHide() {
        return new RenderComponent(getName(), getRenderType(), subSystem, false);
    }

    public RenderComponent cloneAndShow() {
        return new RenderComponent(getName(), getRenderType(), subSystem, true);
    }
}
