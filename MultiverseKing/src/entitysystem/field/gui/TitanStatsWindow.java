package entitysystem.field.gui;

import com.simsilica.es.Entity;
import entitysystem.loader.TitanLoader.InitialTitanStatsComponent;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class TitanStatsWindow extends UnitStatsWindow {
    
    public TitanStatsWindow(ElementManager screen, GUIRenderSystem system) {
        super(screen, "Titan", system, 4);
    }

    @Override
    protected void showWindow(Entity e) {
        InitialTitanStatsComponent comp = e.get(InitialTitanStatsComponent.class);
        addField("Influence Range", comp.getInfluenceRange());
        addMinMaxField("Atb Burst", comp.getMaxAtbBurst(), comp.getMaxAtbBurst());
        addMinMaxField("Energy", comp.getMaxEnergy(), comp.getMaxEnergy());
        addField("Weapon Slots", comp.getWeaponSlots());
        super.showWindow(e);
    }
}
