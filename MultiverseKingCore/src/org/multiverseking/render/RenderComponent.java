package org.multiverseking.render;

import com.jme3.scene.control.Control;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import org.multiverseking.core.utility.SubSystem;

/**
 *
 * @author Eike Foede, roah
 */
public class RenderComponent extends AbstractRender implements EntityComponent {

    private final SubSystem subSystem;
    private final Control[] control;
    private final EntityId parent;

    /**
     * Component used for rendering. rotation == Rotation.A && SubSytem == null 
     * && visibility == true.
     *
     * @param name Object name to render.
     * @param renderType type of rendering to use.
     */
    public RenderComponent(String name, RenderType renderType) {
        this(name, renderType, null, true, null);
    }

    /**
     * Component used for rendering. visibility == true.
     *
     * @param name object name to render.
     * @param renderType type of rendering.
     * @param system does is to be visible.
     */
    public RenderComponent(String name, RenderType renderType, SubSystem system) {
        this(name, renderType, system, true, null);
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
        this(name, renderType, system, isVisible, null);
    }
    
    /**
     * component used for the rendering.
     *
     * @param name Object name to render.
     * @param renderType type of rendering to use.
     * @param system is this object belong to another system.
     * @param isVisible does is to be visible.
     * @param control spacial control to add when instancing.
     */
    public RenderComponent(String name, RenderType renderType, SubSystem system, boolean isVisible, Control... control) {
        this(name, renderType, system, isVisible, null, control);
    }
    
    /**
     * component used for the rendering.
     *
     * @param name Object name to render.
     * @param renderType type of rendering to use.
     * @param system is this object belong to another system.
     * @param isVisible does is to be visible.
     * @param parent entity this one will always follow.
     * @param control spacial control to add when instancing.
     */
    public RenderComponent(String name, RenderType renderType, SubSystem system, boolean isVisible, EntityId parent, Control... control) {
        super(name, renderType, isVisible);
        this.subSystem = system;
        this.control = control;
        this.parent = parent;
    }

    public SubSystem getSubSystem() {
        return subSystem;
    }

    public Control[] getControl() {
        return control;
    }
    
    public EntityId getParent() {
        return parent;
    }

    public RenderComponent cloneAndHide() {
        return new RenderComponent(getName(), getRenderType(), subSystem, false, parent, control);
    }

    public RenderComponent cloneAndShow() {
        return new RenderComponent(getName(), getRenderType(), subSystem, true, parent, control);
    }

    public RenderComponent setParent(EntityId id) {
        return new RenderComponent(getName(), getRenderType(), subSystem, isVisible(), parent, control);
    }

    public RenderComponent setParent(EntityId parent, boolean isVisible) {
        return new RenderComponent(getName(), getRenderType(), subSystem, isVisible, parent, control);
    }
}
