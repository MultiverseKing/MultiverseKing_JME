package entitysystem.field.gui;

import com.simsilica.es.Entity;
import entitysystem.loader.TitanLoader;
import entitysystem.loader.TitanLoader.InitialTitanStatsComponent;
import entitysystem.render.RenderComponent;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class UnitStatsWindow extends PropertiesWindow {
    
    public UnitStatsWindow(ElementManager screen, GUIRenderSystem system) {
        this(screen, "Unit", system, 5);
    }
    
    public UnitStatsWindow(ElementManager screen, String UID, GUIRenderSystem system, int maxField) {
        super(screen, UID, system, maxField+5);
    }

    @Override
    protected void showWindow(Entity e) {
        InitialTitanStatsComponent comp = e.get(TitanLoader.InitialTitanStatsComponent.class);
        setWindowTitle(windowTitle + e.get(RenderComponent.class).getName());
        addMinMaxField("Health Point", comp.getHealthPoint(), comp.getHealthPoint());
        addMinMaxField("Atb Value", comp.getMaxAtb(), comp.getMaxAtb());
        addField("Speed", comp.getSpeed());
        addField("Move Range", comp.getMoveRange());
        addField("Move Speed", comp.getMoveSpeed());
    }
}
