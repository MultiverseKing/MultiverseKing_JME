package entitysystem.field.gui;

import com.simsilica.es.Entity;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
class PropertiesMenu {
    private final GUIRenderSystem system;
    private final Screen screen;
    private Window current;

    public PropertiesMenu(GUIRenderSystem system, Screen screen) {
        this.system = system;
        this.screen = screen;
    }
    
    void show(Entity e){
        switch(e.get(GUIRenderComponent.class).getEntityType()){
            case ENVIRONMENT:
                /**
                 * @todo
                 */
                break;
            case TITAN:
                if(current != null && current instanceof TitanStatsWindow == false){
                    screen.removeElement(current);
                }
                current = new TitanStatsWindow(screen, system);
                break;
            case UNIT:
                /**
                 * @todo
                 */
                break;
        }
        screen.addElement(current);
    }
}
