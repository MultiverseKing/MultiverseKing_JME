package gui;

import com.jme3.math.Vector2f;
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
public class FileManagerPopup extends DialogWindow {

    private final boolean isLoading;
    private Menu loadingList;

    public FileManagerPopup(Screen screen, String WindowName, DialogWindowListener listener, boolean isLoading) {
        super(screen, WindowName, listener);
        addInputText("Name");
        this.isLoading = isLoading;
        if(isLoading){
            addButton("Load from files.");
        } else {
            addButton("Override existing.");
        }
        show(true);
    }

    @Override
    protected void onButtonTrigger(String labelName) {
        if (labelName.equals((isLoading ? "Load from files." : "Override existing.")) && loadingList == null) {
            initializeLoading();
        } else if (labelName.equals((isLoading ? "Load from files." : "Override existing."))) {
            loadingList.showMenu(null, getField((isLoading ? "Load from files." : "Override existing.")).getAbsoluteX(),
                    getField((isLoading ? "Load from files." : "Override existing.")).getAbsoluteY() - loadingList.getHeight());
        } else if (labelName.equals("Confirm") && getTextInput("Name").length() < 2) {
        } else {
            super.onButtonTrigger(labelName);
        }
    }

    private void initializeLoading() {
        File folder = new File(System.getProperty("user.dir") + "/assets/Data/MapData/");
        if (!folder.exists()) {
            Logger.getLogger(FileManagerPopup.class.getName()).log(Level.SEVERE, "Cannot locate the MapData Folder.", FileNotFoundException.class);
        }
        loadingList = new Menu(screen, "mapFileList", new Vector2f(), 
                new Vector2f(getField((isLoading ? "Load from files." : "Override existing.")).getDimensions().x, getLayoutGridSize().y), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                getTextField("Name").setText((String) value);
                listener.onDialogTrigger(getUID(), true);
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
        loadingList.showMenu(null, getField((isLoading ? "Load from files." : "Override existing.")).getAbsoluteX(),
                getField((isLoading ? "Load from files." : "Override existing.")).getAbsoluteY() - loadingList.getHeight());
    }

    public String getInput() {
        return getTextInput("Name");
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void removeFromScreen() {
        super.removeFromScreen();
        if(loadingList != null){
            screen.removeElement(loadingList);
        }
    }
    
}
