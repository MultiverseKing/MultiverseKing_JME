package entitysystem.field.gui;

import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class UnitStatsWindow extends PropertiesWindow {
    
    public UnitStatsWindow(ElementManager screen, GUIRenderSystem system) {
        this(screen, "Unit", system);
    }
    
    public UnitStatsWindow(ElementManager screen, String UID, GUIRenderSystem system) {
        super(screen, UID, system);
        setUnitField();
    }
    
    private void setUnitField(){
        setWindowTitle(windowTitle + system.GetEntityName());
        addMinMaxField("Health Point", system.getHealth());
        addMinMaxField("Atb Value", system.getAtbValue());
        addField("Move Range", system.getMovePointValue());
        addField("Move Speed", system.getMoveSpeedValue());
    }
}
