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
import hexsystem.area.AreaEventSystem;

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

    /**
     * Trigger when the user press one entry on the root menu.
     * @param menu the entry the user has pressed on.
     * @param forceTrigger 
     */
    private void menuTrigger(EditorItem menu, boolean forceTrigger) {
        if (currentDialogPopup == null || !currentDialogPopup.isVisible()) {
            boolean showSubMenu = true;
            if (currentMenuValue != menu /*|| forceTrigger */|| menu == EditorItem.MAP || menu == EditorItem.BATTLE) {
                currentMenuItem.removeAllMenuItems();
                switch (menu) {
                    case CARD:
                        currentMenuItem.addMenuItem("Edit Card", "EDIT", null);
                        currentMenuItem.addMenuItem("Card Test", "TEST", null);
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
                                subMenuTrigger(getMenuItem(currentHighlightIndex).getValue().toString());
                            }
                        };
                        subMenu.addMenuItem("New", 0, null);
                        subMenu.addMenuItem("Load", 1, null);
                        /**
                         * If there is no mode currently on all mapSubMenu is the same,
                         * else we create a new submenu with additionnal value and add it to the right button.
                         * */
                        if(!system.getCurrentMode().equals("area") && !system.getCurrentMode().equals("world")){
                            for (MapEditorMode mode : MapEditorMode.values()) {
                                String output = mode.toString().substring(0, 1) + mode.toString().substring(1).toLowerCase();
                                currentMenuItem.addMenuItem("Edit " + output, mode, subMenu);
                            }
                        } else if(system.getCurrentMode().equals("area") || system.getCurrentMode().equals("world")){
                            Menu subMenuImproved = new Menu(main.getScreen(), false) {
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
                            subMenuImproved.addMenuItem("New", 0, null);
                            subMenuImproved.addMenuItem("Load", 1, null);
                            subMenuImproved.addMenuItem("Save", 2, null);
                            subMenuImproved.addMenuItem("Delete", 3, null);
                            for (MapEditorMode mode : MapEditorMode.values()) {
                                String output = mode.toString().substring(0, 1) + mode.toString().substring(1).toLowerCase();
                                if(system.getCurrentMode().equals("area")){
                                    if(mode.equals(MapEditorMode.AREA)){
                                        currentMenuItem.addMenuItem("Edit " + output, mode, subMenuImproved);
                                    } else if(mode.equals(MapEditorMode.WORLD)){
                                        currentMenuItem.addMenuItem("Edit " + output, mode, subMenu);
                                    } else {
                                        throw new UnsupportedOperationException(mode + " mode isn't implemented.");
                                    }
                                } else if(system.getCurrentMode().equals("world")){
                                    if(mode.equals(MapEditorMode.AREA)){
                                        currentMenuItem.addMenuItem("Edit " + output, mode, subMenu);
                                    } else if(mode.equals(MapEditorMode.WORLD)){
                                        currentMenuItem.addMenuItem("Edit " + output, mode, subMenuImproved);
                                    } else {
                                        throw new UnsupportedOperationException(mode + " mode isn't implemented.");
                                    }
                                }
                            }
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
                        /**
                         * TODO : Add the battle area loading when no area is currently loaded or another mode is currently activated.
                         */
                        if (system.getCurrentMode().equals("area") && !main.getStateManager().getState(AreaEditorSystem.class).isEmpty() &&
                                main.getStateManager().getState(AreaEventSystem.class).hasStartPosition()) {
                            currentMenuItem.addMenuItem("Start from current", "useCurrent", null);
                        } else {
                            if (currentDialogPopup != null) {
                                currentDialogPopup.removeFromScreen();
                            }
                            currentDialogPopup = new DialogWindow(main.getScreen(), "Warning !", this);
                            currentDialogPopup.addLabelField("An area with Start position ");
                            currentDialogPopup.addLabelField("must be initialized first.");
                            currentDialogPopup.show(false);
                            showSubMenu = false;
                        }
//                        currentMenuItem.addMenuItem("Load Battle Map", "LoadBattle", null);
                        break;
                    default:
                        throw new UnsupportedOperationException(menu + " is not a supported type.");
                }
                currentMenuValue = menu;
            }
            if (showSubMenu) {
                if (menu != EditorItem.SFX){// && menu == EditorItem.BATTLE && system.getCurrentMode().equals("area") && !main.getStateManager().getState(AreaEditorSystem.class).isEmpty()) {
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
                        } else if (Byte.valueOf(value).equals((byte) 3)) { //Delete
                            if (currentDialogPopup != null) {
                                currentDialogPopup.removeFromScreen();
                            }
                            currentDialogPopup = new DialogWindow(main.getScreen(), "Delete Current Map", this);
                            currentDialogPopup.addLabelField("Are you sure to want ");
                            currentDialogPopup.addLabelField("to delete without saving.");
                            currentDialogPopup.show(true);
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
            } else if (dialogUID.equals("Load Battle")) {
                system.initializeBattle();
            } else if (dialogUID.equals("Warning !")){
                currentDialogPopup.removeFromScreen();
            }
            if (dialogUID.equals("Delete Current Map")) {
                system.clearCurrent();
                currentDialogPopup.removeFromScreen();
            }
        } else {
            currentDialogPopup.removeFromScreen();
        }
    }

    public void cleanup() {
        main.getScreen().getApplication().getInputManager().deleteMapping("confirmDialog");
        main.getScreen().getApplication().getInputManager().deleteMapping("cancelDialog");
        main.getScreen().removeElement(main.getScreen().getElementById("EditorMainMenu"));
        if(currentDialogPopup != null){
            currentDialogPopup.removeFromScreen();
        }
        if(currentMenuItem != null){
            main.getScreen().removeElement(currentMenuItem);
        }
        main.getScreen().removeElement(mainMenuBar);
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
