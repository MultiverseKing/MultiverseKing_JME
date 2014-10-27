package editor;

import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import editor.area.AreaEditorSystem;
import editor.card.CardEditorWindow;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import gui.DialogWindow;
import gui.DialogWindowListener;
import gui.FileManagerPopup;
import gui.LayoutWindow;
import gui.LoadingPopup;

/**
 * rootMenu of the Game Editor.
 *
 * @author roah
 */
public final class EditorMainGUI implements DialogWindowListener {

    private final EditorSystem system;
    private final MultiverseMain main;
    private Window mainMenuBar;
    private Menu currentMenuItem;
    private LayoutWindow editorWindow;
    private EditorItem currentMenuValue;
    private String currentSelectedMenuItem;
    private DialogWindow currentDialogPopup;

    public EditorMainGUI(Application app, EditorSystem system) {
        main = (MultiverseMain) app;
        this.system = system;
        CreateMainMenu();
        initKeyMapping();
    }

    public Window getMainMenu() {
        return mainMenuBar;
    }

    private void CreateMainMenu() {
        mainMenuBar = new Window(main.getScreen(), "EditorMainMenu", new Vector2f(5f, 5f), new Vector2f(main.getScreen().getWidth() - 10, 40));
        mainMenuBar.setMinDimensions(new Vector2f());
        mainMenuBar.setHeight(30);
        mainMenuBar.setWindowTitle("Editor Menu |");
        mainMenuBar.setIgnoreMouse(true);
        mainMenuBar.getDragBar().setPosition(mainMenuBar.getDragBar().getPosition().x, mainMenuBar.getDragBar().getPosition().y - 10);
        mainMenuBar.getDragBar().setIgnoreMouse(true);
        main.getScreen().addElement(mainMenuBar);

        int i = 1;
        for (EditorItem e : EditorItem.values()) {
            final EditorItem eValue = e;
            ButtonAdapter editorMenuBtn = new ButtonAdapter(main.getScreen(), eValue + "EditorBtn", new Vector2f((100 * i) + 10, 5), new Vector2f(100, 20)) {
                @Override
                public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    menuTrigger(eValue, false);
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
                if (getMenuItem(index).getSubMenu() != null) {
                    return;
                }
                subMenuTrigger(value.toString());
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

    private void initKeyMapping() {
        if (!main.getScreen().getApplication().getInputManager().hasMapping("confirmDialog")
                && !main.getScreen().getApplication().getInputManager().hasMapping("cancelDialog")) {
            main.getScreen().getApplication().getInputManager().addMapping("confirmDialog", new KeyTrigger(KeyInput.KEY_RETURN));
            main.getScreen().getApplication().getInputManager().addMapping("cancelDialog", new KeyTrigger(KeyInput.KEY_ESCAPE));
        }
    }

    private void menuTrigger(EditorItem menu, boolean forceTrigger) {
        if (currentDialogPopup == null || !currentDialogPopup.isVisible()) {
            boolean showSubMenu = true;
            if (currentMenuValue != menu || forceTrigger || menu == EditorItem.MAP) {
                Menu subMenu;
                currentMenuItem.removeAllMenuItems();
                switch (menu) {
                    case CARD:
                        currentMenuItem.addMenuItem("Edit Card", "EDIT", null);
                        currentMenuItem.addMenuItem("Card Test", "TEST", null);
                        break;
                    case MAP:
                        subMenu = new Menu(main.getScreen(), false) {
                            @Override
                            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                                //Does not work as submenu... why!?
                            }

                            @Override
                            public void onMouseLeftReleased(MouseButtonEvent evt) {
                                super.onMouseLeftReleased(evt);
                                subMenuTrigger(getMenuItem(currentHighlightIndex).getValue().toString());
                            }
                        };
                        /**
                         * @todo: Add the save btn if there a map to save.
                         */
                        subMenu.addMenuItem("New", 0, null);
                        subMenu.addMenuItem("Load", 1, null);
                        if(system.getCurrentMode().equals("area")){
                            subMenu.addMenuItem("Save", 2, null);
                        }
                        for (MapEditorMode mode : MapEditorMode.values()) {
                            String output = mode.toString().substring(0, 1) + mode.toString().substring(1).toLowerCase();
                            currentMenuItem.addMenuItem("Edit " + output, mode, subMenu);
                        }
                        break;
                    case SFX:
                        /**
                         * @todo: Add the SFX-builder window to the screen.
                         * (should remove other window?)
                         */
                        showSubMenu = false;
                        break;
                    case BATTLE:
                        if(system.getCurrentMode().equals("area")){
                            currentMenuItem.addMenuItem("Start from current", "useCurrent", null);
                        }
                        currentMenuItem.addMenuItem("Load Battle Map", "LoadBattle", null);
//                        showSubMenu = false;
                        break;
                    default:
                        throw new UnsupportedOperationException(menu + " is not a supported type.");
                }
                currentMenuValue = menu;
            }
            if (showSubMenu) {
                if (menu != EditorItem.SFX) {// && menu != EditorItem.BATTLE) {
                    Element btn = mainMenuBar.getElementsAsMap().get(menu + "EditorBtn");
                    currentMenuItem.showMenu(null, btn.getAbsoluteX(), btn.getAbsoluteY() - currentMenuItem.getHeight());
                }
            }
        }
    }

    private void subMenuTrigger(String value) {
        switch (currentMenuValue) {
            case CARD:
                if (value.equals("EDIT")) {
                    if (editorWindow == null) {
                        editorWindow = new CardEditorWindow(main.getScreen(), mainMenuBar);
                    } else if (editorWindow instanceof CardEditorWindow == false) {
                        editorWindow.removeFromScreen();
                        editorWindow = new CardEditorWindow(main.getScreen(), mainMenuBar);
                    } else if (editorWindow.isVisible()) {
                        editorWindow.hide();
                    } else {
                        editorWindow.setVisible();
                    }
                } else if (value.equals("TEST")) {
                    /**
                     * @todo: Add the card testing window to the screen. Load a
                     * map if any are initialized. if one is initialized, ask
                     * for : - Save / not save / load / load empty
                     */
                }
                break;
            case MAP:
                MapEditorMode mode = MapEditorMode.valueOf(currentSelectedMenuItem);
                /**
                 * We check if the battle system is on, if yes we show a
                 * warning.
                 */
                switch (mode) {
                    case WORLD:
                        /**
                         * @todo
                         */
                        break;
                    case AREA:
                        if (Byte.valueOf(value).equals((byte) 0)) { //New
                            system.initializeAreaEditor(null);
                        } else if (Byte.valueOf(value).equals((byte) 1)) { //Load
                            if (currentDialogPopup != null) {
                                currentDialogPopup.removeFromScreen();
                            }
                            currentDialogPopup = new FileManagerPopup(main.getScreen(), "Load Area", this, true);
                        } else if (Byte.valueOf(value).equals((byte) 2)) { //Save
                            if (currentDialogPopup != null) {
                                currentDialogPopup.removeFromScreen();
                            }
                            currentDialogPopup = new FileManagerPopup(main.getScreen(), "Save Area", this, false);
                        } else {
                            throw new UnsupportedOperationException(value + " is not a supported type.");
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException(mode + " is not a supporter Type.");
                }
                break;
            case BATTLE:
                if (value.equals("useCurrent")) {
                } else if (value.equals("LoadBattle")) {
                    if (currentDialogPopup != null) {
                        currentDialogPopup.removeFromScreen();
                    }
                    currentDialogPopup = new FileManagerPopup(main.getScreen(), "Load Area", this, true);
                }
                break;
            default:
                if (currentMenuValue.equals(EditorItem.BATTLE) || currentMenuValue.equals(EditorItem.SFX)) {
                    break;
                } else {
                    throw new UnsupportedOperationException(currentMenuValue + " is not a supported type.");
                }
        }
    }

    public void onDialogTrigger(String dialogUID, boolean confirmOrCancel) {
        if (confirmOrCancel) {
            if (dialogUID.equals("Load Area")) {
                system.initializeAreaEditor((FileManagerPopup) currentDialogPopup);
            } else if (dialogUID.equals("Save Area")) {
                system.saveCurrentArea((FileManagerPopup) currentDialogPopup);
            } else if (dialogUID.equals("Load World")) {
                /**
                 * @todo
                 */
            } else if (dialogUID.equals("Load Battle")){
                system.initializeBattle();
            }
        } else {
            currentDialogPopup.removeFromScreen();
        }
    }

    public void cleanup() {
        main.getScreen().getApplication().getInputManager().deleteMapping("confirmDialog");
        main.getScreen().getApplication().getInputManager().deleteMapping("cancelDialog");
        main.getScreen().removeElement(main.getScreen().getElementById("EditorMainMenu"));
    }

    private enum EditorItem {

        MAP,
        CARD,
        SFX,
        BATTLE;
    }

    public enum MapEditorMode {

        AREA,
        WORLD;
    }
}
