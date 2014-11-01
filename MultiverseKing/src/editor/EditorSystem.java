package editor;

import editor.map.WorldEditorSystem;
import editor.area.AreaEditorSystem;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import entitysystem.render.RenderSystem;
import hexsystem.battle.BattleSystem;
import gui.DialogWindow;
import gui.DialogWindowListener;
import gui.FileManagerPopup;
import hexsystem.area.AreaMouseInputSystem;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.core.Element;

/**
 * take care of all Editor system conflict and constraint.
 *
 * @author roah
 */
public class EditorSystem extends AbstractAppState implements DialogWindowListener {

    private SimpleApplication app;
    private EditorMainGUI gui;
    private DialogWindow currentDialogPopup;
    private String currentMode = "null";
    private boolean showDialog = false;
    private FileManagerPopup fileManagerPopup;

    public Element getGUI() {
        return gui.getMainMenu();
    }

    public String getCurrentMode() {
        return currentMode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        gui = new EditorMainGUI(app, this);
        this.app = (SimpleApplication) app;
    }

    void initializeBattle() {
        currentMode = "battle";
        clearForCurrent(false);
    }

    /**
     *
     * @param popup Set to null to create new map
     */
    void initializeAreaEditor(FileManagerPopup popup) {
        currentMode = "area";
        clearForCurrent(false);
        fileManagerPopup = popup;
    }
    
    void saveCurrentArea(FileManagerPopup popup){
        if(currentMode.equals("area")){
            app.getStateManager().getState(AreaEditorSystem.class).save(popup);
        }
        fileManagerPopup.removeFromScreen();
    }

    private void clearForCurrent(boolean force) {
        showDialog = false;
        if (!currentMode.equals("area") && app.getStateManager().getState(AreaEditorSystem.class) != null) {
            if (force) {
                app.getStateManager().detach(app.getStateManager().getState(AreaEditorSystem.class));
            } else {
                showDialog();
            }
        }
        if (!currentMode.equals("world") && app.getStateManager().getState(WorldEditorSystem.class) != null) {
            if (force) {
                app.getStateManager().detach(app.getStateManager().getState(WorldEditorSystem.class));
            } else {
                showDialog();
            }
        }
        if (!currentMode.equals("battle") && app.getStateManager().getState(BattleSystem.class) != null) {
            if (force) {
                app.getStateManager().detach(app.getStateManager().getState(BattleSystem.class));
            } else {
                showDialog();
            }
        }
        if (!showDialog) {
            initializeForCurrent();
        }
    }

    private void initializeForCurrent() {
        if (currentMode.equals("area")) {
            if (app.getStateManager().getState(RenderSystem.class) == null) {
                app.getStateManager().attach(new RenderSystem());
            }
            if (app.getStateManager().getState(AreaMouseInputSystem.class) == null) {
                app.getStateManager().attach(new AreaMouseInputSystem());
            }
            if (app.getStateManager().getState(AreaEditorSystem.class) == null) {
                app.getStateManager().attach(new AreaEditorSystem(fileManagerPopup));
            } else {
                app.getStateManager().getState(AreaEditorSystem.class).reloadSystem(fileManagerPopup);
            }
        } else if (currentMode.equals("world")) {
        } else if (currentMode.equals("battle")) {
            if (app.getStateManager().getState(RenderSystem.class) == null) {
                app.getStateManager().attach(new RenderSystem());
            }
            if (app.getStateManager().getState(AreaMouseInputSystem.class) == null) {
                app.getStateManager().attach(new AreaMouseInputSystem());
            }
            if (app.getStateManager().getState(BattleSystem.class) == null) {
                app.getStateManager().attach(new BattleSystem());
            } else {
                app.getStateManager().getState(BattleSystem.class).reloadSystem();
            }

        } else {
            throw new UnsupportedOperationException(currentMode + " is not a supported Mode");
        }
    }

    public void onDialogTrigger(String dialogUID, boolean confirmOrCancel) {
        if (confirmOrCancel) {
            if (dialogUID.equals("Warning !")) {
                clearForCurrent(true);
            }
        }
        currentDialogPopup.removeFromScreen();
    }

    private void showDialog() {
        if (currentDialogPopup != null) {
            currentDialogPopup.removeFromScreen();
        }
        currentDialogPopup = new DialogWindow(((MultiverseMain) app).getScreen(), "Warning !", this);
        currentDialogPopup.addLabelField("All current system will be removed.");
        currentDialogPopup.show();
        showDialog = true;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        gui.cleanup();
    }
}
