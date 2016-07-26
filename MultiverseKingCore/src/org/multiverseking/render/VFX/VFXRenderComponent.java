package org.multiverseking.render.VFX;

import com.simsilica.es.EntityComponent;
import org.multiverseking.core.utility.SubSystem;

/**
 *
 * @author Eike Foede, roah
 */
public class VFXRenderComponent implements EntityComponent {

    private final String name;
    private final SubSystem subSystem;
    private final boolean isVisible;

    /**
     * Component used for rendering. rotation == Rotation.A && SubSytem == null 
     * && visibility == true.
     *
     * @param name Object name to render.
     */
    public VFXRenderComponent(String name) {
        this(name, null, true);
    }

    /**
     * Component used for rendering. visibility == true.
     *
     * @param name object name to render.
     * @param system does is to be visible.
     */
    public VFXRenderComponent(String name, SubSystem system) {
        this(name, system, true);
    }

    /**
     * component used for the rendering.
     *
     * @param name Object name to render.
     * @param system is this object belong to another system.
     * @param isVisible does is to be visible.
     */
    public VFXRenderComponent(String name, SubSystem system, boolean isVisible) {
        this.name = name;
        this.subSystem = system;
        this.isVisible = isVisible;
    }
    /**
     * component used for the rendering.
     *
     * @param name Object name to render.
     * @param system is this object belong to another system.
     * @param mode does is to be visible.
     * @param isVisible does is to be visible.
     */
    public VFXRenderComponent(String name, SubSystem system, EmitterMode mode, boolean isVisible) {
        this.name = name;
        this.subSystem = system;
        this.isVisible = isVisible;
    }

    public SubSystem getSubSystem() {
        return subSystem;
    }

    public String getName() {
        return name;
    }

    public boolean isIsVisible() {
        return isVisible;
    }

    public VFXRenderComponent cloneAndHide() {
        return new VFXRenderComponent(getName(), subSystem, false);
    }

    public VFXRenderComponent cloneAndShow() {
        return new VFXRenderComponent(getName(), subSystem, true);
    }
    
    public enum EmitterMode {
        pulse,
        instance
    }
}
