package gamemode.editor;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import gamemode.editor.map.MapEditorSystem.MapEditorMode;

/**
 *
 * @author roah
 */
public class EditorMainGui extends AbstractAppState {

    private MultiverseMain main;
    private Window mainMenuBar;
    private Menu currentMenuSelector;
    private EditorItem loadedSub;

    public Window getMainMenu() {
        return mainMenuBar;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        CreateMainMenu();
    }

    private void CreateMainMenu() {
        mainMenuBar = new Window(main.getScreen(), "mainWin", new Vector2f(5f, 5f), new Vector2f(main.getScreen().getWidth() - 10, 40));
        mainMenuBar.setMinDimensions(Vector2f.ZERO);
        mainMenuBar.setHeight(30);
        mainMenuBar.setWindowTitle("Editor Menu |");
        mainMenuBar.setIgnoreMouse(true);
        mainMenuBar.getDragBar().setPosition(mainMenuBar.getDragBar().getPosition().x, mainMenuBar.getDragBar().getPosition().y - 10);
        mainMenuBar.getDragBar().setIgnoreMouse(true);
        main.getScreen().addElement(mainMenuBar);

        populateMainMenu();
    }

    private void populateMainMenu() {
        int i = 1;
        for (EditorItem e : EditorItem.values()) {
            final EditorItem eValue = e;
            ButtonAdapter editorMenuBtn = new ButtonAdapter(main.getScreen(), eValue + "EditorBtn", new Vector2f((100 * i) + 10, 5), new Vector2f(100, 20)) {
                @Override
                public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    openMenuItem(eValue);
                }
            };
            String output = eValue.toString().substring(0, 1) + eValue.toString().substring(1).toLowerCase();
            editorMenuBtn.setText(output + " Editor");
            mainMenuBar.addChild(editorMenuBtn);
            i++;
        }
        currentMenuSelector = new Menu(main.getScreen(), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                menuTrigger(value.toString());
            }

            @Override
            public void hide() {
                super.hide();
                hideAllSubmenus(true);
            }
            
        };
        main.getScreen().addElement(currentMenuSelector);
    }

    private void openMenuItem(EditorItem menu) {
        boolean showMenu = true;
        if (loadedSub != menu) {
            currentMenuSelector.removeAllMenuItems();
            switch (menu) {
                case CARD:
                    currentMenuSelector.addMenuItem("Generator", menu + "_GEN", null);
                    currentMenuSelector.addMenuItem("Test Menu", menu.toString() + "_TEST", null);
                    break;
                case MAP:
                    Menu subMenu = new Menu(main.getScreen(), false) {
                         @Override
                         public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                             subMenuTrigger(value.toString());
                         }
                     };
                    subMenu.addMenuItem("New", 0, null);
                    subMenu.addMenuItem("Load", 1, null);
                    /**
                     * @todo: Add the save btn if there a map to save.
                     */
//                    subMenu.addMenuItem("Save", 2, null);
                    for (MapEditorMode mode : MapEditorMode.values()) {
                        if (mode != MapEditorMode.NONE) {
                            String output = mode.toString().substring(0, 1) + mode.toString().substring(1).toLowerCase();
                            currentMenuSelector.addMenuItem("Edit " + output, menu + "_" + mode, subMenu);
                        }
                    }
                    break;
                case SFX:
                    /**
                     * @todo: Add the SFX-builder window to the screen. (should remove
                     * other window?)
                     */
                    showMenu = false;
                    break;
                default:
                    throw new UnsupportedOperationException(menu + " is not a supported type.");
            }
        }
        if (showMenu) {
            Element btn = mainMenuBar.getElementsAsMap().get(menu + "EditorBtn");
            currentMenuSelector.showMenu(null, btn.getAbsoluteX(), btn.getAbsoluteY() - currentMenuSelector.getHeight());
        }
    }

    private void menuTrigger(String value) {
        String[] input = value.split("_");
        EditorItem item = EditorItem.valueOf(input[0]);
        switch (item) {
            case CARD:
                if (input[1].equals("GEN")) {
                    /**
                     * @todo: Add the card generator window to the screen.
                     */
                } else if (input[1].equals("TEST")) {
                    /**
                     * @todo: Add the card testing window to the screen.
                     * Load a map if any are initialized.
                     * if one is initialized, ask for :
                     * - Save / not save / load / load empty
                     */
                } else {
                    throw new UnsupportedOperationException(input[1] + " is not a supported mode.");
                }
                break;
            case MAP:
                return;
            case SFX:
                break;
            default:
                throw new UnsupportedOperationException(item + " is not a supported type.");
        }
    }
    
    private void subMenuTrigger(String value) {
        
    }

    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
    }

    private enum EditorItem {

        MAP,
        CARD,
        SFX;
    }
}
