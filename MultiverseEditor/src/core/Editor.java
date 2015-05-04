package core;

import core.entitysystem.JESPropertiesPanel;
import org.multiversekingesapi.EntityDataAppState;
import org.multiversekingesapi.IMultiverCoreGUI;
import org.multiversekingesapi.field.AreaEventSystem;
import org.multiversekingesapi.field.position.HexPositionSystem;
import org.multiversekingesapi.render.AreaEventRenderDebugSystem;
import org.multiversekingesapi.render.RenderSystem;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class Editor extends HexGridEditorMain implements IMultiverCoreGUI {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Editor app = new Editor();
            }
        });
    }

    public Editor() {
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
                new AreaEventSystem(),
                new AreaEventRenderDebugSystem());

        rootWindow.getJMenuBar().add(new JPlayEditorMenu(this));
        addPropertiesPanel(new JESPropertiesPanel(this));
    }
}
