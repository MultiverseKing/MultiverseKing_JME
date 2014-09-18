package gamemode.editor;

import gamemode.gui.DialogWindowListener;
import gamemode.gui.DialogPopup;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.EntitySystemAppState;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import gamemode.editor.map.RoomEditorSystem;
import tonegod.gui.controls.menuing.MenuItem;

/**
 * rootMenu of the Game Editor.
 * @author roah
 */
public class EditorMainGui extends AbstractAppState implements DialogWindowListener {

    private MultiverseMain main;
    private Window mainMenuBar;
    private Menu currentMenuItem;
    private EditorItem currentMenuValue;
    private String currentSelectedMenuItem;
    private EntitySystemAppState usedSystem;
    private DialogPopup currentDialogPopup;

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
                if(getMenuItem(index).getSubMenu() != null){
                    return;
                }
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
                        String output = mode.toString().substring(0, 1) + mode.toString().substring(1).toLowerCase();
                        currentMenuItem.addMenuItem("Edit " + output, mode, subMenu);
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
                switch(mode){
                    case WORLD:
                        loadWorldEditorSystem(new Byte(value));
                        break;
                    case AREA:
                        loadAreaEditorSystem(new Byte(value));
                        break;
                    case ROOM:
                        if(usedSystem instanceof RoomEditorSystem == false){
                            main.getStateManager().detach(usedSystem);
                            usedSystem = null;
                        }
                        loadRoomEditorSystem(new Byte(value));
                        break;
                    default:
                        throw new UnsupportedOperationException(mode + " is not a supporter Type.");
                }
            case SFX:
                break;
            default:
                throw new UnsupportedOperationException(currentMenuValue + " is not a supported type.");
        }
    }
    
    private void loadRoomEditorSystem(byte value) {
        if (value == 0) {
            if (usedSystem != null) {
                ((RoomEditorSystem)usedSystem).reloadRoom();
            } else {
                usedSystem = new RoomEditorSystem();
                main.getStateManager().attach(usedSystem);
            }
        } else if (value == 1) {
            /**
             * @todo: Load.
             */
            if(currentDialogPopup != null){
                currentDialogPopup.removeFromScreen();
            }
            currentDialogPopup = new DialogPopup(main.getScreen(), "Load Room", this);
            currentDialogPopup.addEditableTextField("Name", null, Vector2f.ZERO);
            currentDialogPopup.show();
        } else if (value == 2) {
            /**
             * @todo: Save.
             */
        }
    }
    
    public void onDialogTrigger(String dialogUID, boolean confirmOrCancel) {
        if(dialogUID.equals(currentDialogPopup.getUID())){
            if(confirmOrCancel){
                usedSystem = new RoomEditorSystem(currentDialogPopup.getTextField("Name").getText());
                main.getStateManager().attach(usedSystem);
            } else {
                
            }
        }
    }
    
    private void loadAreaEditorSystem(Byte aByte) {
        System.err.println("Not Supported yet.");
    }

    private void loadWorldEditorSystem(Byte aByte) {
        System.err.println("Not Supported yet.");
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
    public enum MapEditorMode {

        ROOM,
        AREA,
        WORLD;
    }
}
