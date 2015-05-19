package core;

import core.swingcontrol.JESPropertiesPanel;
import org.multiversekingesapi.EntityDataAppState;
import org.multiversekingesapi.MultiverCoreGUI;
import core.escontrol.DebugSystem;
import org.multiversekingesapi.field.position.HexPositionSystem;
import core.escontrol.RenderDebugSystem;
import org.multiversekingesapi.render.RenderSystem;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class MultiverseEditor extends HexGridEditorMain implements MultiverCoreGUI {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MultiverseEditor app = new MultiverseEditor();
            }
        });
    }

    public MultiverseEditor() {
        super("King Of Multiverse Editor");
    }
    Screen screen;

    @Override
    public Screen getScreen() {
        return screen;
    }

    @Override
    public void startHexGridEditor() {
        super.startHexGridEditor();

        screen = new Screen(this);
        getStateManager().attachAll(
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new DebugSystem(),
                new RenderDebugSystem()
                );

        rootWindow.getJMenuBar().add(new JPlayEditorMenu(this));
        addPropertiesPanel(new JESPropertiesPanel(this));
    }
}
