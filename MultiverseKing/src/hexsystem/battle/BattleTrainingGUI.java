package hexsystem.battle;

import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityId;
import com.simsilica.es.PersistentComponent;
import org.multiversekingesapi.loader.GameProperties;
import org.multiversekingesapi.render.RenderComponent.RenderType;
import gui.EditorWindow;
import gui.EntityStatsWindow;
import java.util.ArrayList;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 * Main GUI for the battleTraining.
 * @author roah
 */
public class BattleTrainingGUI extends EditorWindow {

    private final Camera cam;
    private ContextualMenu contextualActionMenu;
    private EntityStatsWindow statsWindow;
    private Menu entityMenu;
    private RenderType current;

    BattleTrainingGUI(Screen screen, Camera cam, BattleTrainingSystem system) {
        super(screen, screen.getElementById("EditorMainMenu"), "Battle Training", new Vector2f(150, 20));
        this.cam = cam;
        contextualActionMenu = new ContextualMenu(screen, cam, system);
        statsWindow = new EntityStatsWindow(screen, this.parent);

        entityMenu = new Menu(screen, "entityMenuList", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                entityMenuItemTrigger((String) value);
            }
        };

        //------
        addButtonField("Add Titan");
        addButtonField("Add Unit");
        showGUI();
    }

    final void showGUI() {
        if(window == null){
            showConstrainToParent(VAlign.bottom, HAlign.right);
        } else if (screen.getElementById(window.getUID()) == null){
            screen.addElement(window);
        } else if (!window.getIsVisible()){
            window.setIsVisible(true);
        }
    }

    @Override
    protected void onButtonTrigger(String label) {
        if (screen.getElementById(entityMenu.getUID()) == null) {
            screen.addElement(entityMenu);
        }
        GameProperties properties = GameProperties.getInstance(screen.getApplication().getAssetManager());

        switch (label) {
            case "Add Titan":
                if(current == null || !current.equals(RenderType.Titan)){
                    entityMenu.removeAllMenuItems();
                    for (String s : properties.getTitanList()) {
                        entityMenu.addMenuItem(s, s, null);
                    }
                }
                showMenu(getButtonField("AddTitan", false));
                current = RenderType.Titan;
                break;
            case "Add Unit":
                if(current == null || !current.equals(RenderType.Unit)){
                    entityMenu.removeAllMenuItems();
                    for (String s : properties.getUnitList()) {
                        entityMenu.addMenuItem(s, s, null);
                    }
                }
                showMenu(getButtonField("AddUnit", false));
                current = RenderType.Unit;
                break;
        }
    }

    private void showMenu(ButtonAdapter btn) {
        entityMenu.showMenu(null, btn.getAbsoluteX() - btn.getDimensions().x + (spacement * 2), btn.getAbsoluteY() + btn.getDimensions().y - entityMenu.getDimensions().y);
    }

    private void addUnit(String name) {
    }

    private void addTitan(String name) {
    }

    private void entityMenuItemTrigger(String value) {
    }

    @Override
    public void onPressCloseAndHide() {
    }

    void update(float tpf) {
        if (contextualActionMenu != null) {
            contextualActionMenu.update(tpf);
        }
    }

    void showActionMenu(HexCoordinate pos, EntityId id, RenderType type) {
        contextualActionMenu.show(id, type, pos);
    }

    void statsWindow(EntityId id, RenderType renderType, ArrayList<PersistentComponent> comps) {
        statsWindow.show(id, renderType, comps);
    }
}
