package gamemode.battle;

import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import gamemode.gui.EditorWindow;
import gamemode.gui.PropertiesWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class BattleTrainingGUI extends EditorWindow {

    private final BattleSystem system;
    private final Camera cam;
    private final Screen screen;
    private ContextualMenu actionMenu;
    private PropertiesWindow titanStatsWindow;    
    
    BattleTrainingGUI(Screen screen, Camera cam, BattleSystem system) {
        super(screen, screen.getElementById("EditorMainMenu"), "BattleTrainingGUI", new Vector2f(150, 20));
        this.screen = screen;
        this.cam = cam;
        this.system = system;
        titanStatsWindow = new TitanStatsWindow(screen, system);
        actionMenu = new TitanMenu(screen, system, cam);
        
        //------
        addButtonField("Add Titan");
        addButtonField("Add Unit");
        showConstrainToParent(VAlign.bottom, HAlign.right);
    }

    @Override
    protected void onButtonTrigger(String label) {
        if(label.equals("Add Titan")){
            
        } else if(label.equals("Add Unit")){
            
        }
    }

    
    
    void update(float tpf) {
        actionMenu.update(tpf);
    }

    void showActionMenu(HexCoordinate pos, EntityId id) {
        actionMenu.show(pos, id);
    }

    void showTitanStats(Entity entity) {
        titanStatsWindow.show(entity);
    }
}
