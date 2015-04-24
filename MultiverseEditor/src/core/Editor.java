package core;

import core.entitysystem.JESPropertiesPanel;
import hexmapeditor.gui.JPropertiesPanelHolder;
import java.awt.Component;
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
    Screen screen;

    @Override
    public Screen getScreen() {
        return screen;
    }

    @Override
    public void startAreaEditor() {
        super.startAreaEditor();

        screen = new Screen(this);
        getStateManager().attachAll(
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new AreaEventSystem(),
                new AreaEventRenderDebugSystem());

        JPlayEditorMenu playEditorMenu = new JPlayEditorMenu(this);
        rootWindow.getJMenuBar().add(playEditorMenu);
        for (Component c : rootWindow.getContentPane().getComponents()) {
            if (c instanceof JPropertiesPanelHolder) {
                ((JPropertiesPanelHolder) c).add(new JESPropertiesPanel(this));
                break;
            }
        }
    }
}
