package hexsystem.battle;

import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityId;
import org.multiversekingesapi.render.RenderComponent.RenderType;
import gui.CameraTrackWindow;
import hexsystem.battle.BattleTrainingSystem.Action;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 * Show a menu listing all action the user can do with the selected object. (if handled by the battle system)
 * @author roah
 */
public class ContextualMenu extends CameraTrackWindow {

    private final BattleTrainingSystem system;
    private EntityId inspectedEntityId = null;
    private RenderType currentType;

    ContextualMenu(Screen screen, Camera camera, BattleTrainingSystem guiSystem) {
        super(screen, camera);
        this.system = guiSystem;
        screenElement = new Menu(screen, new Vector2f(), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                system.setAction(inspectedEntityId, (Action)value);
            }
        };
    }
    
    void show(EntityId id, RenderType type, HexCoordinate position){
        boolean show = false;
        if(currentType != type){
            if(screen.getElementById(screenElement.getUID()) == null){
                screen.addElement(screenElement);
            } else {
                ((Menu) screenElement).removeAllMenuItems();
            }
            switch(type){
                case Ability:
                    return;
                case Core:
                    break;
                case Debug:
                    /**
                     * Internal use.
                     */
                    break;
                case Environment:
                    break;
                case Equipement:
                    break;
                case Titan:
                    addItem(Action.MOVE);
                    addItem(Action.ABILITY);
                    show = true;
                    break;
                case Unit:
                    addItem(Action.MOVE);
                    addItem(Action.ABILITY);
                    show = true;
                    break;
                default:
                    Logger.getGlobal().log(Level.WARNING, "{0} : Unimplemented type : {1}", new Object[]{getClass().getName(), type.toString()});
                    return;
            }
            currentType = type;
        }
        if(show){
            show(position);
        }
        inspectedEntityId = id;
    }
    
    void addMenuItem(Action action){
        if(action.equals(Action.CUSTOM)) {
            /**
             * Load the action name from files.
             */
        } else {
            String str = action.toString().substring(0, 1) + action.toString().substring(1).toLowerCase();
            ((Menu) screenElement).addMenuItem(" "+ str  +"        ", action, null);
        }
    }
    
    void clearMenuItem(){
        ((Menu) screenElement).removeAllMenuItems();
    }

    private void addItem(Action action) {
        String str = action.toString().substring(0, 1) + action.toString().substring(1).toLowerCase();
        ((Menu) screenElement).addMenuItem(" "+str+"       ", action, null);
    }
}
