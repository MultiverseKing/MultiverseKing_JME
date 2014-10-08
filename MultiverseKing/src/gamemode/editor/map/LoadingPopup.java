package gamemode.editor.map;

import com.jme3.math.Vector2f;
import gamemode.gui.Dialogwindow;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class LoadingPopup extends Dialogwindow {

    private boolean isInitialized = false;
    private Menu loadingList;

    public LoadingPopup(Screen screen, String WindowName, LoadingPopupListener listener) {
        super(screen, WindowName, listener);
        addInputText("Name");
        addButton("Load from files.");
    }

    @Override
    protected void onButtonTrigger(String labelName) {
        if (labelName.equals("Load from files.") && !isInitialized) {
            initializeLoading();
        } else if (labelName.equals("Load from files.") && isInitialized) {
            loadingList.showMenu(null, getField("Load from files.").getAbsoluteX(),
                    getField("Load from files.").getAbsoluteY() - loadingList.getHeight());
        } else {
            super.onButtonTrigger(labelName);
        }
    }

    private void initializeLoading() {
        File folder = new File(System.getProperty("user.dir") + "/assets/Data/MapData/");
        if (!folder.exists()) {
            Logger.getLogger(AreaEditorSystem.class.getName()).log(Level.SEVERE, "Cannot locate the MapData Folder.", FileNotFoundException.class);
        }
        loadingList = new Menu(screen, "mapFileList", new Vector2f(), new Vector2f(getField("Load from files.").getDimensions().x, getLayoutGridSize().y), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                if (!((LoadingPopupListener) listener).loadForCurrent(getUID(), value.toString())) {
                    popupBox("     " + value.toString() + " not found.");
                }
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
        loadingList.showMenu(null, getField("Load from files.").getAbsoluteX(),
                getField("Load from files.").getAbsoluteY() - loadingList.getHeight());
        isInitialized = true;
    }
}
