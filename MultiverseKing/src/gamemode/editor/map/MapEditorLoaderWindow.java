package gamemode.editor.map;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import gamemode.gui.EditorWindow;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 * Used to load and save a map inside a files.
 * @todo load/save Area.
 * @todo load/save world.
 * @author roah
 */
class MapEditorLoaderWindow extends EditorWindow {

    private Window saveWindow;
    private Menu loadingList;
    private Window popup;

    MapEditorLoaderWindow(ElementManager screen, Element parent) {
        super(screen, parent, "Save & Load");

        addButtonField("Load", 0, Vector2f.ZERO);
        addButtonField("Save", 1, new Vector2f(0, 1));

        if (parent.getUID().equals("WorldEditorMenu")) {
        } else if (parent.getUID().equals("AreaEditorMenu")) {
        }
        showConstrainToParent(new Vector2f(0.8f, 2.8f), HAlign.right);
        initializeLoading();
    }

    @Override
    protected void onButtonTrigger(int index) {
        switch (index) {
            case 0:
                /**
                 * Load
                 */
                loadingList.showMenu(null, getField("Load").getAbsoluteX() + (loadingList.getWidth() / 1.6f), getField("Load").getAbsoluteY());
                break;
            case 1:
                /**
                 * Save
                 */
                if (saveWindow == null) {
                    initializeSave();
                } else if (saveWindow.getIsVisible()) {
                    saveWindow.hide();
                } else {
                    saveWindow.show();
                }
                break;
        }
    }

    private void initializeLoading() {
        File folder = new File(System.getProperty("user.dir") + "/assets/Data/MapData/");
        if (!folder.exists()) {
            Logger.getLogger(AreaEditorSystem.class.getName()).log(Level.SEVERE, "Cannot locate the MapData Folder.", FileNotFoundException.class);
        }
        loadingList = new Menu(screen, "mapFileList", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                if (true || !app.getStateManager().getState(AreaEditorSystem.class).loadCurrent(value.toString())) {
                    popupBox("     File not found.");
                }
                System.err.println("test load : " + value.toString());
            }
        };
        File[] flist = folder.listFiles();
        byte i = 0;
        for (int j = 0; j < flist.length; j++) {
            if (flist[j].getName().equalsIgnoreCase("Reset") || flist[j].getName().equalsIgnoreCase("Temp")) {
                i++;
            } else {
                loadingList.addMenuItem(flist[j].getName(), flist[j].getName(), null);
            }
        }
        screen.addElement(loadingList);
    }

    private void initializeSave() {
        saveWindow = new Window(screen, "saveWindow",
                new Vector2f(getWindow().getWidth(), getWindow().getHeight() - 40), new Vector2f(225, 40));
        saveWindow.getDragBar().removeFromParent();
        saveWindow.setIgnoreMouse(true);
        getWindow().addChild(saveWindow);

        /**
         * Field used to set the name of the map to save.
         */
        final TextField textField = new TextField(screen, "savetextField", new Vector2f(10, 7), new Vector2f(125, 26));
        saveWindow.addChild(textField);
        /**
         * Button used to save the map in a folder/file of his name.
         */
        Button save = new ButtonAdapter(screen, "confirm", new Vector2f(saveWindow.getWidth() - 80, 7), new Vector2f(75, 25)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (textField.getText().length() >= 3) {
                    if (app.getStateManager().getState(AreaEditorSystem.class).saveArea(textField.getText())) {
                        popupBox("     File Saved.");
                    } else {
                        popupBox("     Error file not saved.");
                    }
                }
//                    saveWindow.show();
            }
        };
        save.setText("Confirm");
        saveWindow.addChild(save);
    }

    private void popupBox(String message) {
        if (getWindow().getElementsAsMap().get("popupBox") == null) {
            popup = new Window(screen, "popupBox", new Vector2f(0, getWindow().getHeight()), new Vector2f(getWindow().getWidth() * 2, 25));
            popup.removeAllChildren();
            getWindow().addChild(popup);
        }
        popup.setText(message);
    }
}
