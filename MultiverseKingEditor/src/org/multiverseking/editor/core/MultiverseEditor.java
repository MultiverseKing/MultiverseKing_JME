package org.multiverseking.editor.core;

import java.util.logging.Level;
import org.hexgridapi.editor.core.HexGridEditorMain;
import org.multiverseking.editor.core.swingcontrol.JESPropertiesPanel;
import org.multiverseking.EntityDataAppState;
import org.multiverseking.MultiverCoreGUI;
import org.multiverseking.debug.DebugSystemState;
import org.multiverseking.field.position.HexPositionSystem;
import org.multiverseking.debug.RenderDebugSystem;
import org.multiverseking.render.RenderSystem;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class MultiverseEditor extends HexGridEditorMain implements MultiverCoreGUI {

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.jme3").setLevel(Level.WARNING);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MultiverseEditor app = new MultiverseEditor("King Of Multiverse Editor");
            }
        });
    }

    public MultiverseEditor(String str) {
        super(str);
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
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new DebugSystemState(),
                new RenderDebugSystem());

        getHexMapModule().addPropertiesTab(new JESPropertiesPanel(this, getHexMapModule().getMouseSystem()));
    }
}
