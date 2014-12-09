package hexsystem.battle;

import com.jme3.asset.AssetKey;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import entitysystem.loader.PropertiesLoader;
import entitysystem.render.RenderComponent.Type;
import gui.EditorWindow;
import gui.PropertiesWindow;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 * Main GUI for the battleTraining.
 * @author roah
 */
public class BattleTrainingGUI extends EditorWindow {

    private final BattleTrainingSystem system;
    private final Camera cam;
    private ContextualMenu contextualActionMenu;
    private PropertiesWindow titanStatsWindow;
    private Menu entityMenu;

    BattleTrainingGUI(Screen screen, Camera cam, BattleTrainingSystem system) {
        super(screen, screen.getElementById("EditorMainMenu"), "BattleTrainingGUI", new Vector2f(150, 20));
        this.cam = cam;
        this.system = system;
        contextualActionMenu = new ContextualMenu(screen, cam, system);
        titanStatsWindow = new TitanStatsWindow(screen, system);

        entityMenu = new Menu(screen, "entityMenuList", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                entityMenuItemTrigger((String) value);
            }
        };

        //------
        addButtonField("Add Titan");
        addButtonField("Add Unit");
        showConstrainToParent(VAlign.bottom, HAlign.right);
    }

    @Override
    protected void onButtonTrigger(String label) {
        if (screen.getElementById(entityMenu.getUID()) == null) {
            screen.addElement(entityMenu);
        }
        PropertiesLoader properties = (PropertiesLoader) screen.getApplication().getAssetManager().loadAsset(new AssetKey<>("Properties.json"));

        switch (label) {
            case "Add Titan":
                for (String s : properties.getTitanList()) {
                    entityMenu.addMenuItem(s, s, null);
                }
                ButtonAdapter btn = getButtonField("AddTitan", false);
                entityMenu.showMenu(null, btn.getAbsoluteX() - btn.getDimensions().x + (spacement * 2), btn.getAbsoluteY() + btn.getDimensions().y - entityMenu.getDimensions().y);
                break;
            case "Add Unit":
                break;
        }
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

    void showActionMenu(HexCoordinate pos, EntityId id, Type type) {
        contextualActionMenu.show(pos, id, type);
    }

    void showTitanStats(Entity entity) {
        titanStatsWindow.show(entity);
    }
}
