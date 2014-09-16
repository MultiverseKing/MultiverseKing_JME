package gamemode.editor;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import gamemode.editor.map.MapEditorSystem;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import gamemode.editor.map.MapEditorSystem.MapEditorMode;
import static gamemode.editor.map.MapEditorSystem.MapEditorMode.AREA;
import static gamemode.editor.map.MapEditorSystem.MapEditorMode.NONE;
import static gamemode.editor.map.MapEditorSystem.MapEditorMode.ROOM;
import static gamemode.editor.map.MapEditorSystem.MapEditorMode.WORLD;
import tonegod.gui.controls.menuing.MenuItem;

/**
 *
 * @author roah
 */
public class EditorMainGui extends AbstractAppState {

    private MultiverseMain main;
    private Window mainMenuBar;
    private Menu currentMenuItem;
    private EditorItem currentMenuValue;
    private String currentSelectedMenuItem;

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
        currentMenuItem = new Menu(main.getScreen(), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                menuTrigger(value.toString());
            }

            @Override
            public void hide() {
                super.hide();
                hideAllSubmenus(true);
            }

            @Override
            public void setHighlight(int index) {
                super.setHighlight(index);
                currentSelectedMenuItem = getMenuItem(index).getValue().toString();
            }
            
            
        };
        main.getScreen().addElement(currentMenuItem);
    }

    private void openMenuItem(EditorItem menu) {
        boolean showMenu = true;
        if (currentMenuValue != menu) {
            currentMenuItem.removeAllMenuItems();
            switch (menu) {
                case CARD:
                    currentMenuItem.addMenuItem("Generator", "GEN", null);
                    currentMenuItem.addMenuItem("Test Menu", "TEST", null);
                    break;
                case MAP:
                    Menu subMenu = new Menu(main.getScreen(), false) {
                        @Override
                        public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                            //Does not work as submenu... why!?
                        }

                        @Override
                        public void onMouseLeftReleased(MouseButtonEvent evt) {
                            super.onMouseLeftReleased(evt);
                            menuTrigger(getMenuItem(currentHighlightIndex).getValue().toString());
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
                            currentMenuItem.addMenuItem("Edit " + output, mode, subMenu);
                        }
                    }
                    break;
                case SFX:
                    /**
                     * @todo: Add the SFX-builder window to the screen. (should
                     * remove other window?)
                     */
                    showMenu = false;
                    break;
                default:
                    throw new UnsupportedOperationException(menu + " is not a supported type.");
            }
            currentMenuValue = menu;
        }
        if (showMenu && menu != EditorItem.SFX) {
            Element btn = mainMenuBar.getElementsAsMap().get(menu + "EditorBtn");
            currentMenuItem.showMenu(null, btn.getAbsoluteX(), btn.getAbsoluteY() - currentMenuItem.getHeight());
        }
    }

    private void menuTrigger(String value) {
        switch (currentMenuValue) {
            case CARD:
                if (value.equals("GEN")) {
                    /**
                     * @todo: Add the card generator window to the screen.
                     */
                } else if (value.equals("TEST")) {
                    /**
                     * @todo: Add the card testing window to the screen. Load a
                     * map if any are initialized. if one is initialized, ask
                     * for : - Save / not save / load / load empty
                     */
                } else {
                    return;
                }
                break;
            case MAP:
                MapEditorMode mode = MapEditorMode.valueOf(currentSelectedMenuItem);
                switch (mode) {
                    case WORLD:
                        /**
                         * @todo
                         */
                        if (value.equals("0")) {
                        } else if (value.equals("1")) {
                        } else {
                            return;
                        }
                        break;
                    case AREA:
                        /**
                         * @todo
                         */
                        if (value.equals("0")) {
                        } else if (value.equals("1")) {
                        } else {
                            return;
                        }
                        break;
                    case ROOM:
                        /**
                         * @todo
                         */
                        if (value.equals("0")) {
                            main.getStateManager().attach(new MapEditorSystem());
                        } else if (value.equals("1")) {
                        } else {
                            return;
                        }
                        break;
                    case NONE:
                        return;
                    default:
                        throw new UnsupportedOperationException(mode + " is not a supported type.");
                }
            case SFX:
                break;
            default:
                throw new UnsupportedOperationException(currentMenuValue + " is not a supported type.");
        }
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
