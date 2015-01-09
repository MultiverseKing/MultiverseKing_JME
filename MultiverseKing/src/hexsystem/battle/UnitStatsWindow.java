package hexsystem.battle;

import gui.PropertiesWindowOld;
import com.simsilica.es.Entity;
import entitysystem.loader.TitanLoader;
import entitysystem.loader.TitanLoader.InitialTitanStatsComponent;
import entitysystem.render.RenderComponent;
import tonegod.gui.core.ElementManager;

/**
 * Used to show unit stats on the screen.
 * @author roah
 */
class UnitStatsWindow extends PropertiesWindowOld {

    UnitStatsWindow(ElementManager screen, BattleTrainingSystem system) {
        this(screen, "Unit", system, 5);
    }

    UnitStatsWindow(ElementManager screen, String UID, BattleTrainingSystem system, int maxField) {
        super(screen, UID, system, maxField + 5);
    }

    @Override
    protected void showWindow(Entity e) {
        InitialTitanStatsComponent comp = e.get(TitanLoader.InitialTitanStatsComponent.class);
        setWindowTitle(windowTitle + e.get(RenderComponent.class).getName());
        addMinMaxField("Health Point", comp.getHealPoint(), comp.getHealPoint());
        addMinMaxField("Atb Value", comp.getMaxAtb(), comp.getMaxAtb());
        addField("Speed", comp.getSpeed());
        addField("Move Range", comp.getMoveRange());
        addField("Move Speed", comp.getMoveSpeed());
    }
}
