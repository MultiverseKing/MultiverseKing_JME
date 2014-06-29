package entitysystem.field.gui;

import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class TitanStatsWindow extends UnitStatsWindow {
    
    public TitanStatsWindow(ElementManager screen, GUIRenderSystem system) {
        super(screen, "Titan", system);
        setEntity();
    }
    
    private void setEntity() {
        addField("Influence Range", system.getInfluenceRange());
        addMinMaxField("Atb Burst", system.getAtbBurstValue());
        addField("Atb Burst Speed", system.getAtbBurstSpeed());
        addMinMaxField("Energy", system.getEnergyValue());
        addField("Energy Refill Speed", system.getEnergyRefillSpeed());
        addField("Weapon Slots", system.getWeaponSlotCount());
    }
}
