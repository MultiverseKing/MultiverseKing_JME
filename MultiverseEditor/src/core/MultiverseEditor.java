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
                new DebugSystem(),
                new RenderDebugSystem());

        getRootFrame().getJMenuBar().add(new JPlayEditorMenu(this));
        getHexMapModule().addPropertiesTab(new JESPropertiesPanel(this, getHexMapModule().getMouseSystem()));
    }
}
