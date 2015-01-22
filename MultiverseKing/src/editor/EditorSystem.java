package editor;

import editor.map.WorldEditorSystem;
import editor.area.AreaEditorSystem;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import editor.area.AreaEventRenderDebugSystem;
import entitysystem.render.RenderSystem;
import hexsystem.battle.BattleTrainingSystem;
import gui.DialogWindow;
import gui.DialogWindowListener;
import gui.FileManagerPopup;
import hexsystem.area.AreaEventSystem;
import hexsystem.area.AreaGridSystem;
import hexsystem.area.MapDataAppState;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.base.AreaMouseAppState;
import org.hexgridapi.base.MapData;
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
    private boolean areaIsInitialised;

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
        if(currentMode.equals("battle")){
            app.getStateManager().getState(BattleTrainingSystem.class).showGUI();
        } else {
            currentMode = "battle";
            clearForCurrent(false);
        }
    }

    /**
     *
     * @param popup Set to null to create new map
     */
    void initializeAreaEditor(FileManagerPopup popup) {
        currentMode = "area";
        fileManagerPopup = popup;
        clearForCurrent(false);
    }

    void saveCurrentArea(FileManagerPopup popup) {
        if (currentMode.equals("area")) {
            app.getStateManager().getState(AreaEditorSystem.class).save(popup);
        }
        fileManagerPopup.removeFromScreen();
    }

    /**
     * Remove uneeded AppState before loading the needed one.
     */
    private void clearForCurrent(boolean force) {
        showDialog = false;
        if (currentMode.equals("battle")) {
            force = true;
        }
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
        if (!currentMode.equals("battle") && app.getStateManager().getState(BattleTrainingSystem.class) != null) {
            if (force) {
                app.getStateManager().detach(app.getStateManager().getState(BattleTrainingSystem.class));
            } else {
                showDialog();
            }
        }
        if (!showDialog) {
            initializeForCurrent();
        }
    }

    private void initializeForCurrent() {
        if (app.getStateManager().getState(RenderSystem.class) == null) {
            app.getStateManager().attach(new RenderSystem());
        }
        if (currentMode.equals("area") || currentMode.equals("battle")) {
            if(!areaIsInitialised){
                initialiseAreaSystem();
            }
            if (currentMode.equals("area")) {
                if (app.getStateManager().getState(AreaEditorSystem.class) == null) {
                    app.getStateManager().attach(new AreaEditorSystem(fileManagerPopup));
                } else {
                    app.getStateManager().getState(AreaEditorSystem.class).loadFromFile(fileManagerPopup);
                }
            } else {
                if (app.getStateManager().getState(BattleTrainingSystem.class) == null) {
                    app.getStateManager().attach(new BattleTrainingSystem());
                } else {
                    app.getStateManager().getState(BattleTrainingSystem.class).reloadSystem();
                }
            }
        } else if (currentMode.equals("world")) {
            /**
             * @todo
             */
        } else {
            throw new UnsupportedOperationException(currentMode + " is not a supported Mode");
        }
    }

    private void initialiseAreaSystem() {
        MapData mapData = ((MultiverseMain) app).getStateManager().getState(MapDataAppState.class).getMapData();
        app.getStateManager().attach(new AreaGridSystem(mapData, true));
        app.getStateManager().attach(new AreaEventRenderDebugSystem());
        app.getStateManager().attach(new AreaEventSystem());
        app.getStateManager().attach(new AreaMouseAppState());
        areaIsInitialised = true;
    }

    @Override
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
        currentDialogPopup.show(true);
        showDialog = true;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        app.getStateManager().detach(app.getStateManager().getState(AreaEditorSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(WorldEditorSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(BattleTrainingSystem.class));
        app.getStateManager().detach(app.getStateManager().getState(RenderSystem.class));
        gui.cleanup();
    }
}
