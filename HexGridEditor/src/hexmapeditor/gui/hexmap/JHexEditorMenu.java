package hexmapeditor.gui.hexmap;

import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.hexgridapi.core.appstate.MapDataAppState;
import core.HexGridEditorMain;

/**
 *
 * @author roah
 */
public final class JHexEditorMenu extends JMenu {

    private final HexGridEditorMain main;

    public JHexEditorMenu(HexGridEditorMain editorMain) {
        super("Hex Editor");
        this.main = editorMain;
    }

    public void setAction(HexMenuAction action) {
        String name = action.toString() + " Map";
        add(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
    }

    public void onAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New Map":
                if (!main.isStart()) {
                    main.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            main.startAreaEditor();
                            return null;
                        }
                    });
                    setAction(HexMenuAction.Save);
                } else {
                    //@todo create a new map from a new seed
                    //@todo show warning popup
                }
                break;
            case "Load Map":
                JLoaderDialog loadDialog = new JLoaderDialog(main.getRootWindow(), false);
                loadDialog.setLocationRelativeTo(main.getRootWindow());
                loadDialog.setVisible(true);

                String loadName = loadDialog.getValidatedText();
                if (loadName != null) {
                    //The text is valid.
                    System.err.println("@todo Load Map " + loadName);
                }
                loadDialog.dispose();
                break;
            case "Save Map":
                JLoaderDialog saveDialog = new JLoaderDialog(main.getRootWindow(),
                        main.getStateManager().getState(MapDataAppState.class).getMapData().getMapName());
                saveDialog.setLocationRelativeTo(main.getRootWindow());
                saveDialog.setModal(true);
                saveDialog.setVisible(true);
                String saveName = saveDialog.getValidatedText();
                if (saveName != null) {
                    //The text is valid.
                    main.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
//                            editorSystem.save(null);
                            System.err.println("@todo Save Map");
                            return null;
                        }
                    });
                }
                saveDialog.dispose();
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }

    public enum HexMenuAction {

        New,
        Load,
        Save;
    }
}
