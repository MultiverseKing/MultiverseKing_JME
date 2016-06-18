package org.multiverseking.editor.core;

import java.util.logging.Level;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.editor.core.EditorMain;
import org.multiverseking.core.MultiverseCoreState;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import org.multiverseking.debug.DebugSystemState;
import org.multiverseking.editor.core.swingcontrol.JESPropertiesTab;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class MultiverseEditor extends EditorMain implements MultiverseCoreGUI {

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MultiverseEditor app = new MultiverseEditor("King Of Multiverse Editor");
            }
        });
    }

    public MultiverseEditor(String windowName) {
        super(windowName);
    }
    Screen screen;

    @Override
    public Screen getScreen() {
        return screen;
    }

    @Override
    public void initApplication() {
        screen = new Screen(this);
        getStateManager().attachAll(
                new MultiverseCoreState(RTSCamera.KeyMapping.col),
                new DebugSystemState());

//        getModuleControl().addRootTab(new testRootModule(this));
        getModuleControl().addRootTab(new EmitterModule(this));
        getHexGridModule().addPropertiesTab(new JESPropertiesTab(this, getHexGridModule().getMouseSystem()));
    }
}
