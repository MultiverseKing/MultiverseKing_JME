package org.multiverseking.render;

/**
 * Used to regroup data about rendering.
 * @author roah
 */
public abstract class AbstractRender {
    private final String name;
    private final RenderType renderType;
    private final boolean isVisible;
    
    public AbstractRender(String name, RenderType renderType,boolean isVisible) {
        this.name = name;
        this.renderType = renderType;
        this.isVisible = isVisible;
    }

    public String getName() {
        return name;
    }

    public RenderType getRenderType() {
        return renderType;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public enum RenderType{
        Unit,
        Titan,
        Core,
        Environment,
        Ability,
        Equipement,
        Debug;
    }
}
