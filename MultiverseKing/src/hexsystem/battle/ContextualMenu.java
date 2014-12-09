package hexsystem.battle;

import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityId;
import entitysystem.render.RenderComponent.Type;
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
    private Type currentType;

    ContextualMenu(Screen screen, Camera camera, BattleTrainingSystem guiSystem) {
        super(screen, camera);
        this.system = guiSystem;
        screenElement = new Menu(screen, new Vector2f(), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                system.setAction(inspectedEntityId, (Action)value);
            }
        };
        screen.addElement(screenElement);
    }
    
    void show(HexCoordinate position, EntityId id, Type type){
        if(currentType != type){
            ((Menu) screenElement).removeAllMenuItems();
            switch(type){
                case Core:
                    addItem(Action.STATS);
                    break;
                case Debug:
                    /**
                     * Internal use.
                     */
                    break;
                case Titan:
                    addItem(Action.MOVE);
                    addItem(Action.ABILITY);
                    addItem(Action.STATS);
                    break;
                case Environments:
                    addItem(Action.STATS);
                    break;
                case Units:
                    addItem(Action.MOVE);
                    addItem(Action.ABILITY);
                    addItem(Action.STATS);
                    break;
                default:
                    Logger.getGlobal().log(Level.WARNING, "{0} : Unimplemented type : {1}", new Object[]{getClass().getName(), type.toString()});
                    return;
            }
            currentType = type;
        }
        super.show(position);
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
