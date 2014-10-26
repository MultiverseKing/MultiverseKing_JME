package hexsystem.battle;

import gui.PropertiesWindow;
import com.simsilica.es.Entity;
import entitysystem.loader.TitanLoader;
import entitysystem.loader.TitanLoader.InitialTitanStatsComponent;
import entitysystem.render.RenderComponent;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
class UnitStatsWindow extends PropertiesWindow {

    UnitStatsWindow(ElementManager screen, BattleSystem system) {
        this(screen, "Unit", system, 5);
    }

    UnitStatsWindow(ElementManager screen, String UID, BattleSystem system, int maxField) {
        super(screen, UID, system, maxField + 5);
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
